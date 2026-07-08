# Baritone Fabric MVP for Minecraft 1.21.1

Оригинальный учебный/личный Fabric-мод под Minecraft **1.21.1**, Java **21**, Mod ID **baritone**.

> Это не официальный Baritone и не использует его исходники. Это компилируемый MVP-каркас с базовой автосистемой, командами `#`, A* pathfinding, конфигом, HUD и визуализацией пути. Автодобыча, фарм и строительство реализованы как простые расширяемые процессы, а не как полный промышленный бот.

## Что уже есть

- Fabric Client Mod Initializer.
- Команды с префиксом `#`:
  - `#help`
  - `#version`
  - `#debug`
  - `#settings`
  - `#stop`
  - `#pause`
  - `#resume`
  - `#goto X Y Z`
  - `#goto ~ ~ ~`
  - `#mine <block> [count]`
  - `#mine area X Y Z`
  - `#farm wheat|carrots|potatoes|beetroots|all|replant`
  - `#follow player Steve`
  - `#follow entity cow`
  - `#follow stop`
  - `#build schematic house.schematic`
  - `#build clear`
- A* pathfinding с Manhattan heuristic.
- Обход лавы/огня/кактусов/магмы по умолчанию.
- Ходьба, плавание, подъём на 1 блок, падение до настраиваемой высоты.
- Автооткрытие дверей и калиток при движении.
- Рендер пути частицами и HUD-индикатор.
- JSON-конфиг: `config/baritone.json`.
- GitHub Actions сборка.

## Безопасность и ограничения

Мод не содержит packet spoofing, обход античита, скрытие автоматизации или специальных механизмов «защиты от бана». Используйте его только в одиночной игре или на серверах, где такая автоматизация разрешена правилами.

## Сборка локально

```bash
gradle build
```

Итоговый jar будет в:

```text
build/libs/
```

## Сборка на GitHub

1. Создайте репозиторий.
2. Залейте все файлы проекта.
3. Откройте вкладку **Actions**.
4. Запустите workflow **Build Fabric Mod** или сделайте push.
5. Готовый jar появится в artifacts.

## Настройка версий

Зависимости указаны в `gradle.properties`:

```properties
minecraft_version=1.21.1
yarn_mappings=1.21.1+build.3
loader_version=0.16.10
fabric_version=0.116.4+1.21.1
cloth_config_version=15.0.140
```

Если Maven/Gradle скажет, что конкретная версия Fabric API или Cloth Config недоступна, замените её на актуальную совместимую для Minecraft 1.21.1.

## Дальнейшее развитие

Для уровня полноценного Baritone нужно сильно расширять:

- стоимость движений A*;
- работу с чанками и кэшем мира;
- безопасное копание шахт;
- real-time replanning;
- полноценный inventory management;
- NBT-парсеры `.schematic` / `.litematic`;
- очереди действий и отмену действий;
- проверки прав сервера/регионов.
