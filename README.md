# Pypsen v2

Клиентский Fabric-мод для **Minecraft 1.20.1**.

## Требования

- Minecraft **1.20.1**
- Fabric Loader `>= 0.14.21`
- Fabric API `0.92.7+1.20.1`
- Java **17+**

## Сборка

Для сборки нужен JDK **21+** (Fabric Loom 1.15), хотя сама модификация рассчитана на Java 17.

```bash
./gradlew build
```

На Windows:

```powershell
.\gradlew.bat build
```

Готовый мод появляется в `build/libs/pypsen-2.0.0.jar`. Исходники — `pypsen-2.0.0-sources.jar`.

## Установка

1. Установите Fabric Loader для Minecraft 1.20.1.
2. Положите Fabric API и `pypsen-2.0.0.jar` в папку `mods`.
3. Запустите клиент.

## Управление

| Клавиша | Действие |
| --- | --- |
| `-` | Открыть меню мода |
| `V` | Speed |
| `R` | KillAura |
| `G` | Fly |
| `X` | XRay |
| `H` | Hitbox |

Остальные бинды назначаются прямо в GUI (кнопка `Key` → нажмите клавишу; middle click сбрасывает бинд).
Средняя кнопка мыши по игроку добавляет/убирает его из списка друзей.

В меню есть классический и современный GUI, пресеты, настройки режимов и кнопка сохранения конфига.

## Конфиг

Файл `run/pypsen_config.txt` создаётся/обновляется кнопкой **Save Config** в меню.
Там же хранятся друзья и выбранные блоки XRay.

## Возможности

- **Combat:** KillAura (режимы/ротации/таргетинг), AutoClicker, TriggerBot, AutoTotem, AutoArmor, FastBow, AimAssist/AimLock.
- **Movement:** Speed, Fly, Spider, NoWeb, Freeze, AirJump, NoFall, Jesus, Noclip, Velocity, Strafe, Anti-Flag.
- **Visuals:** XRay с выбором блоков, ESP, Target HUD, FullBright, NameHider и др.
- **Misc:** Freecam, GodMode, Panic и прочие утилиты.

## Релизы

При push тега вида `v*` GitHub Actions автоматически собирает мод и прикрепляет JAR-файлы к релизу.

## Лицензия

[GPL-3.0](LICENSE).
