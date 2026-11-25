#!/usr/bin/env python3
"""Generate class-name-only mappings for Minecraft 26.3.

26.3 ships de-obfuscated classes and Mojang no longer publishes a mappings
file for it. This script bridges the 1.21.11 Yarn class names to the shipped
Mojang class names through the shared 1.21.11 obfuscated names. Member names
stay exactly as shipped by Mojang: transformFor26() already translates the
shared Yarn source members to Mojang members, and keeping the mapping jar
identity for members makes clean CI builds match local builds.
"""
import collections
import json
import re
import struct
import urllib.request
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
CACHE = ROOT / "tools" / "cache"
OUT_DIR = ROOT / "mappings"
OUT_JAR = OUT_DIR / "26.3-mappings.jar"
YARN_VERSION = "1.21.11+build.6"
YARN_URL = f"https://maven.fabricmc.net/net/fabricmc/yarn/{YARN_VERSION}/yarn-{YARN_VERSION}.jar"
MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"

# Member names are intentionally left untouched. A full member bridge caused
# inheritance conflicts in TinyRemapper and, more importantly, made clean CI
# builds use different member names than the cached local build.
MEMBER_ALIASES = {}


def fetch(url: str, name: str) -> Path:
    CACHE.mkdir(parents=True, exist_ok=True)
    path = CACHE / name
    if not path.exists():
        print("downloading", name)
        urllib.request.urlretrieve(url, path)
    return path


def version_info(version: str) -> dict:
    with urllib.request.urlopen(MANIFEST_URL, timeout=60) as response:
        manifest = json.load(response)
    match = next((v for v in manifest["versions"] if v["id"] == version), None)
    if match is None:
        raise RuntimeError(f"version {version} not found")
    with urllib.request.urlopen(match["url"], timeout=60) as response:
        return json.load(response)


def slash(name: str) -> str:
    return name.replace(".", "/")


def parse_mojang_mappings(path: Path):
    """Return (classes, members).

    classes: Mojang name -> OBF name
    members: (OBF owner, OBF member, kind) -> Mojang member name
    """
    classes = {}
    members = {}
    current_obf = None
    for line in path.read_text(encoding="utf-8").splitlines():
        if not line.strip() or line.lstrip().startswith("#"):
            continue
        class_match = re.match(r"^(\S[^:]*) -> (\S+):$", line)
        if class_match:
            current_obf = class_match.group(2)
            classes[slash(class_match.group(1))] = current_obf
            continue
        if current_obf is None or "->" not in line:
            continue
        left, obf_member = line.rsplit("->", 1)
        obf_member = obf_member.strip()
        left = re.sub(r"^\s*(?:\d+:\d+:)?", "", left).strip()
        if not left or obf_member in ("<init>", "<clinit>"):
            continue
        if "(" in left:
            kind = "method"
            name = left.split("(", 1)[0].strip().split()[-1]
        else:
            kind = "field"
            name = left.split()[-1]
        members.setdefault((current_obf, obf_member, kind), name)
    return classes, members


def parse_yarn_mappings(path: Path):
    with zipfile.ZipFile(path) as archive:
        lines = archive.read("mappings/mappings.tiny").decode("utf-8").splitlines()
    classes = {}
    members = {}
    for line in lines[1:]:
        parts = line.split("\t")
        if parts[0] == "CLASS":
            obf, _inter, named = parts[1], parts[2], parts[3]
            classes[obf] = named
        elif parts[0] in ("FIELD", "METHOD"):
            owner, desc, obf, _inter, named = parts[1], parts[2], parts[3], parts[4], parts[5]
            kind = "method" if desc.startswith("(") else "field"
            members.setdefault((owner, obf, kind), named)
    return classes, members


class ClassReader:
    def __init__(self, data: bytes):
        self.data = data
        self.pos = 0

    def u1(self) -> int:
        value = self.data[self.pos]
        self.pos += 1
        return value

    def u2(self) -> int:
        value = struct.unpack_from(">H", self.data, self.pos)[0]
        self.pos += 2
        return value

    def u4(self) -> int:
        value = struct.unpack_from(">I", self.data, self.pos)[0]
        self.pos += 4
        return value

    def skip(self, count: int) -> None:
        self.pos += count


def read_utf8(cp, index: int) -> str:
    value = cp[index]
    if not isinstance(value, str):
        raise ValueError(f"constant pool entry {index} is not UTF-8")
    return value


def parse_class(data: bytes):
    reader = ClassReader(data)
    if reader.u4() != 0xCAFEBABE:
        raise ValueError("not a class file")
    reader.u2()  # minor
    reader.u2()  # major
    count = reader.u2()
    cp = [None] * count
    index = 1
    while index < count:
        tag = reader.u1()
        if tag == 1:
            length = reader.u2()
            cp[index] = reader.data[reader.pos:reader.pos + length].decode("utf-8", "replace")
            reader.skip(length)
        elif tag in (7, 8, 16, 19, 20):
            cp[index] = reader.u2()
        elif tag in (15,):
            reader.u1()
            cp[index] = reader.u2()
        elif tag in (3, 4):
            cp[index] = reader.u4()
        elif tag in (5, 6):
            cp[index] = (reader.u4(), reader.u4())
            index += 1
        elif tag in (9, 10, 11, 12, 17, 18):
            cp[index] = (reader.u2(), reader.u2())
        else:
            raise ValueError(f"unsupported constant pool tag {tag}")
        index += 1

    reader.u2()  # access flags
    this_class = cp[reader.u2()]
    class_name = read_utf8(cp, this_class)
    reader.u2()  # super class
    for _ in range(reader.u2()):
        reader.u2()

    def skip_attributes():
        for _ in range(reader.u2()):
            reader.u2()
            length = reader.u4()
            reader.skip(length)

    fields = []
    methods = []
    for target in (fields, methods):
        for _ in range(reader.u2()):
            reader.u2()
            name = read_utf8(cp, reader.u2())
            desc = read_utf8(cp, reader.u2())
            target.append((name, desc))
            skip_attributes()
    return class_name, fields, methods


def main():
    yarn_path = fetch(YARN_URL, f"yarn-{YARN_VERSION}.jar")
    info_1211 = version_info("1.21.11")
    mappings_url = info_1211["downloads"]["client_mappings"]["url"]
    mojang_path = fetch(mappings_url, "client-1.21.11-mappings.txt")
    info_263 = version_info("26.3")
    client_path = fetch(info_263["downloads"]["client"]["url"], "mc-26.3-client.jar")

    mojang_classes, mojang_members = parse_mojang_mappings(mojang_path)
    yarn_classes, yarn_members = parse_yarn_mappings(yarn_path)

    obf_to_mojang_class = {obf: name for name, obf in mojang_classes.items()}
    mojang_to_yarn_class = {}
    for obf, yarn_name in yarn_classes.items():
        mojang_name = obf_to_mojang_class.get(obf)
        if mojang_name:
            mojang_to_yarn_class[mojang_name] = yarn_name

    # Keep member names identical by default and apply only the curated
    # aliases above. A full automatic bridge creates unfixable inheritance
    # conflicts in TinyRemapper.
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    lines = ["v1\tofficial\tintermediary\tnamed"]
    class_count = 0
    member_count = 0
    matched_classes = 0
    matched_members = 0
    with zipfile.ZipFile(client_path) as archive:
        for entry in archive.infolist():
            if not entry.filename.endswith(".class") or entry.filename.endswith("module-info.class"):
                continue
            class_name, fields, methods = parse_class(archive.read(entry))
            yarn_class = mojang_to_yarn_class.get(class_name, class_name)
            if yarn_class != class_name:
                matched_classes += 1
            lines.append(f"CLASS\t{class_name}\t{class_name}\t{yarn_class}")
            class_count += 1
            aliases = MEMBER_ALIASES.get(class_name, {})
            for kind, members in (("field", fields), ("method", methods)):
                tag = "FIELD" if kind == "field" else "METHOD"
                for name, desc in members:
                    yarn_member = aliases.get(name, name)
                    if yarn_member != name:
                        matched_members += 1
                    lines.append(f"{tag}\t{class_name}\t{desc}\t{name}\t{name}\t{yarn_member}")
                    member_count += 1

    with zipfile.ZipFile(OUT_JAR, "w", zipfile.ZIP_DEFLATED) as archive:
        archive.writestr("mappings/mappings.tiny", "\n".join(lines) + "\n")
    print(f"wrote {OUT_JAR} ({OUT_JAR.stat().st_size} bytes)")
    print(f"classes={class_count} members={member_count} renamed_classes={matched_classes} renamed_members={matched_members}")


if __name__ == "__main__":
    main()
