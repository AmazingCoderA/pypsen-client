#!/usr/bin/env python3
"""Find active public GTA VI repositories that are missing from the awesome index.

Uses only the Python standard library. No game files, no login, no GPU needed.
The output is a list of CANDIDATES, not automatic PR material: verify each one
against CONTRIBUTING.md before submitting anything.
"""

from __future__ import annotations

import argparse
import datetime as dt
import json
import re
import sys
import urllib.parse
import urllib.request

RAW_README = (
    "https://raw.githubusercontent.com/Ahmet-Dedeler/"
    "awesome-gta6-reverse-engineering/main/README.md"
)
API = "https://api.github.com"
NAME_HINT = re.compile(
    r"gta[-_ ]?6|gta[-_ ]?vi|gtavi|scarlett|victory|leonida|rpf9|xbox", re.I
)
DESC_HINT = re.compile(
    r"gta\s*(vi|6)|gtavi|scarlett|victory|leonida|rpf9|rage\s*9|"
    r"d3d12_xs|rdna2|xbox series",
    re.I,
)
QUERIES = (
    "gta6 port in:name,description,readme",
    "gta6 reverse engineering in:name,description,readme",
    "gta vi recompilation in:name,description",
    "scarlett emulator in:name,description",
    "rpf9 gta6 in:name,description,readme",
)


def get(url: str) -> str:
    req = urllib.request.Request(url, headers={"User-Agent": "gta6-gap-finder"})
    with urllib.request.urlopen(req, timeout=30) as response:
        return response.read().decode("utf-8", errors="replace")


def fetch_index() -> str:
    return get(RAW_README)


def search_repos() -> list[dict]:
    seen: dict[int, dict] = {}
    for query in QUERIES:
        url = API + "/search/repositories?" + urllib.parse.urlencode(
            {"q": query, "sort": "updated", "order": "desc", "per_page": 100}
        )
        try:
            payload = json.loads(get(url))
        except Exception as exc:  # keep going if one query is rate-limited
            print(f"warning: search failed ({query}): {exc}", file=sys.stderr)
            continue
        for repo in payload.get("items", []):
            seen[repo["id"]] = repo
    return list(seen.values())


def main() -> int:
    # Windows consoles default to cp1251/cp1252; don't crash on descriptions.
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--min-stars", type=int, default=20)
    parser.add_argument("--days", type=int, default=14)
    parser.add_argument("--limit", type=int, default=25)
    parser.add_argument("--output", help="write a Markdown table to this file")
    args = parser.parse_args()

    readme = fetch_index()
    repos = search_repos()
    cutoff = dt.datetime.now(dt.timezone.utc) - dt.timedelta(days=args.days)

    candidates = []
    for repo in repos:
        pushed = dt.datetime.fromisoformat(repo["pushed_at"].replace("Z", "+00:00"))
        if repo["stargazers_count"] < args.min_stars or pushed < cutoff:
            continue
        if repo["fork"]:
            continue
        description = repo.get("description") or ""
        if not NAME_HINT.search(repo["full_name"]) or not DESC_HINT.search(description):
            continue
        if repo["full_name"].lower() in readme.lower():
            continue
        candidates.append(repo)

    candidates.sort(key=lambda r: r["stargazers_count"], reverse=True)
    candidates = candidates[: args.limit]

    lines = [
        "# Candidates missing from the awesome GTA VI index",
        "",
        f"Generated {dt.datetime.now(dt.timezone.utc).isoformat(timespec='seconds')}. "
        "Verify each repository against CONTRIBUTING.md before opening a PR.",
        "",
        "| Repository | Stars | Pushed | Language | Description |",
        "| --- | ---: | --- | --- | --- |",
    ]
    for repo in candidates:
        desc = repo["description"].replace("|", "\\|").strip()
        lines.append(
            f"| [{repo['full_name']}]({repo['html_url']}) | {repo['stargazers_count']} "
            f"| {repo['pushed_at'][:10]} | {repo.get('language') or '-'} | {desc} |"
        )

    text = "\n".join(lines) + "\n"
    if args.output:
        with open(args.output, "w", encoding="utf-8") as handle:
            handle.write(text)
        print(f"wrote {len(candidates)} candidates to {args.output}")
    else:
        print(text)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
