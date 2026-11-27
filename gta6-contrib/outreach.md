# Готовые тексты для мейнтейнеров

Все на английском — в AnyXboxX это прямо требуется, в остальных проектах английский тоже safest.
Отправлять лучше по одному, не спамить во все репозитории сразу.
Я **не отправлял** ничего: GitHub здесь не авторизован.

---

## 1. Рекомендуемый первый контакт — VIPort, issue #29

Куда: https://github.com/DesertGamer/VIPort/issues/29
Зачем: это единственный проект с живой доской задач, где мейнтейнер сам просит людей откликаться.

```text
Hi DesertGamer and everyone working on VIPort — I'd like to help and I'm
introducing myself here rather than grabbing a task blindly.

I read CONTRIBUTING.md and the task board in #29. My machine is too weak to
run the title and I have no GPU to produce frame-time numbers, so I will not
claim any task that needs in-game measurements, captures or hardware reports.
I will also never post game files, dumps or logs with local paths — I understand
the asset policy.

What I can do reliably: small self-contained Rust changes with synthetic unit
tests that pass `cargo test --workspace` without game data, documentation and
asset-policy cleanup, and careful review of open PRs against the checklist in
CONTRIBUTING.md. I work with an AI coding assistant and will disclose that in
every PR description, as AnyXboxX asks.

If there is a T-item that does not require running the game, please point me at
one and I'll comment `I take T<n>` before starting. Otherwise I'll start with a
documentation PR in the awesome-gta6 index and come back once that is merged.

Thanks for keeping the tree green and for the detailed measurements — they are
what made me want to join.
```

Перевод смысла: «Хочу помочь, железа нет, не буду брать задачи с замерами, могу делать маленькие Rust-правки с синтетическими тестами, документацию и ревью; ИИ-помощник буду указывать; дай задачу без запуска игры».

---

## 2. Leonida — ответ в issue #6

Куда: https://github.com/ProjectLeonidaL/leonida/issues/6

```text
Hi orkan1k — I'd like to join too, but let me be honest about what I can bring.
I don't have hardware that can run the title, so I can't contribute FPS reports
or GPU debugging. I can work on documentation, small Rust changes with synthetic
tests, and reviewing pull requests. I use an AI coding assistant and will say so
in every PR.

I see start-measure.cmd exists for people who do have a machine; I won't pretend
to have results from it. Is there a docs/test-only task I could take first?
Happy to start with something tiny and prove I can follow the asset policy.
```

Если зайдёт разговор — у проекта есть Telegram `@projectleonidal`; там быстрее ответят, чем в issue.

---

## 3. Описание для первого PR (awesome-лист)

Заголовок PR:

```text
Add ProjectLeonidaL/leonida to the translation layers table
```

Тело PR:

```text
Adds one project, following CONTRIBUTING.md (one item per PR, two-column table,
star badge copied from a neighbouring row).

ProjectLeonidaL/leonida is the public continuation of Project Leonida after the
2026-11-24 DMCA takedown of the original repository. It has real commit history
(496 stars, GPL-3.0, Rust), is public and active (pushed 2026-11-26), and is a
static-recompilation compatibility layer built on victory-gta6 / VIctory.

Status written in the row is taken from the project's own README: the title
reaches the world at 2-4 fps by default, about 10 fps in the first rooms with
optional switches, shadowed areas are black, not playable. No game files, keys,
binaries or screenshots are added — only a link and a factual description.
```

Второй PR (после первого):

```text
Add Native-Victory-macOS to the macOS table
```

```text
Adds one item per CONTRIBUTING.md. Native-Victory-macOS is a public, active,
GPL-3.0 Rust port of VIctory to Apple Silicon with a native Metal path and no
Wine. Status in the row follows the project README and its tester issues: the
title reaches the story intro, world load still exhausts GPU memory on 16 GB
Macs and some compute shaders are refused; not playable. Star badge format
copied from a neighbouring macOS row. No game material included.
```

---

## Чего НЕ писать

- «Я сделаю порт» / «у меня есть игра» / «могу прислать логи» — если этого нет.
- Вопросы про то, где скачать `game_scarlett_master.exe`: во всех проектах это табу.
- Ссылки на «готовые лаунчеры» и билды: это может быть malware.
- Просьбу сразу дать права на push/merge: сначала один маленький PR.
