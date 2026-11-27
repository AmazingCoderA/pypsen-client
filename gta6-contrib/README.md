# GTA VI PC-port: как войти в проект без железа и без `gh`

Составлено 2026-11-26 по данным GitHub API. Ничего публично не отправлено: GitHub здесь не авторизован.

## Что такое `gh`

`gh` — это **GitHub CLI**, консольная утилита для работы с GitHub (`gh issue comment`, `gh pr create`, `gh auth login`). Это не «портабельный GitHub». У тебя её нет — и не надо: всё ниже делается через обычный сайт GitHub в браузере.

## Главная идея

У тебя слабый ПК — это не проблема, если не брать неподходящие задачи.

❌ Не надо:
- брать задачи, где нужны FPS-замеры, логи из игры, GPU-профили, hardware-репорты;
- качать «готовые лаунчеры» и билды — в сети есть фейки с само-подписанным сертификатом `CN=Rockstar Games, Inc.` и malware;
- обещать мейнтейнерам тесты на железе, которого нет.

✅ Можно и нужно:
- править индекс/документацию (идеальный первый PR);
- писать маленькие самодостаточные изменения с синтетическими юнит-тестами (без файлов игры);
- ревьюить чужие PR по чеклисту проекта;
- честно говорить, что ты используешь ИИ-помощника (в AnyXboxX и VIPort это прямо требуют указывать в PR).

## Порядок действий

1. Прочитай `outreach.md` — там готовые тексты. Отправь **один** из них (лучше всего — в VIPort issue #29).
2. Сделай первый PR по `pr-1-leonida.patch`: он добавляет пропущенный проект в awesome-лист. Детали — в `FIRST-PR.md`.
3. После того как первый PR зайдёт, сделай второй по `pr-2-macos.patch`.
4. Только после этого проси у мейнтейнера задачу из `T`-списка, которая не требует запуска игры.

## Карта реальных проектов (проверено 2026-11-26)

| Проект | Звёзд | Язык | Вход без железа |
|---|---:|---|---|
| [ProjectLeonidaL/leonida](https://github.com/ProjectLeonidaL/leonida) | 496 | Rust | issue [#6](https://github.com/ProjectLeonidaL/leonida/issues/6), Telegram `@projectleonidal`; есть метки `good first issue` / `help wanted` |
| [LooperSalty/AnyXboxX](https://github.com/LooperSalty/AnyXboxX) | 406 | Rust/C++/Python | `CONTRIBUTING.md`, треды [#427](https://github.com/LooperSalty/AnyXboxX/issues/427), [#339](https://github.com/LooperSalty/AnyXboxX/issues/339) (синтетические тесты), Discord |
| [DesertGamer/VIPort](https://github.com/DesertGamer/VIPort) | 136 | Rust | доска задач [#29](https://github.com/DesertGamer/VIPort/issues/29), `CONTRIBUTING.md`; `cargo test --workspace` не требует игры |
| [Native-Victory-macOS](https://github.com/LakshanSivakumar/Native-Victory-macOS) | 85 | Rust | issue [#4](https://github.com/LakshanSivakumar/Native-Victory-macOS/issues/4) `good first issue` — но только если есть Mac |
| [Steinkoloss/port-of-the-americas](https://github.com/Steinkoloss/port-of-the-americas) | 10 | C++ | Linux/Proton, без железа почти нечего сделать |
| [awesome-gta6-reverse-engineering](https://github.com/Ahmet-Dedeler/awesome-gta6-reverse-engineering) | 784 | Markdown | **лучшая точка входа**: один проект = один PR |

## Правила, которые надо соблюдать

- **Никаких файлов игры**, дампов, скриншотов, шейдеров, ключей, логов с путями — это жёсткое правило во всех репозиториях (`docs/asset-policy.md`), CI его проверяет.
- Один PR = одна тема. Не переформатировать чужой код.
- В VIPort перед PR на `build.rs`, CI, скрипты, зависимости — сначала issue и обсуждение.
- В AnyXboxX/issues и PR — **только английский**; обязательно указывать использование ИИ-помощника в описании PR.
- Ничего не играбельно: лучшие цифры — 16–17 fps в мире на одном ПК, тени чёрные, 28–30 ГБ RAM.

## Следующие PR без железа

`gap_finder.py` ищет активные публичные репозитории по теме, которых ещё нет в awesome-индексе:

```powershell
python gap_finder.py --min-stars 25 --days 10 --limit 12
```

Выдаёт **кандидатов**, а не готовые PR: в выдачу попадают трекеры, SEO-страницы и клоны без оригинальной работы. Перед PR проверь репозиторий по `CONTRIBUTING.md`: реальный код, активные коммиты, честный статус, не копия. После того как наши две строки зайдут, `ProjectLeonidaL/leonida` и `Native-Victory-macOS` из выдачи исчезнут — это удобная проверка.

## Статус

- [x] Разведка: найдены активные репозитории, мейнтейнеры, доски задач и отсутствующие записи в индексе.
- [x] Подготовлены 2 готовых PR-патча и тексты для мейнтейнеров.
- [ ] Отправить комментарий от твоего имени (требует твой вход в GitHub).
- [ ] Отправить PR #1, потом PR #2.
- [ ] Получить первый task от мейнтейнера, который не требует запуска игры.
