# Task Tracker CLI

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/Gradle-8.10-green.svg)](https://gradle.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform: Windows](https://img.shields.io/badge/Platform-Windows-lightgrey.svg)](https://www.microsoft.com/windows)

Task Tracker CLI — это консольное приложение для управления задачами, написанное на Kotlin. Оно позволяет добавлять, обновлять, удалять, отмечать как выполненные и просматривать задачи в виде таблицы. Данные хранятся в файле `tasks.json` в директории `%LocalAppData%\TaskTrackerCLI` (например, `C:\Users\<Имя>\AppData\Local\TaskTrackerCLI`).

Проект использует объектно-ориентированный подход, Gradle для сборки и Inno Setup для создания установщика на Windows.

## Возможности
- Добавление задачи: `task-cli add "Купить продукты"`
- Обновление задачи: `task-cli update 1 "Купить продукты и приготовить ужин"`
- Удаление задачи: `task-cli delete 1`
- Отметка задачи как выполняемой/завершённой: `task-cli mark-in-progress 1`, `task-cli mark-done 1`
- Просмотр задач в виде таблицы: `task-cli list [todo|in-progress|done]`

Пример вывода команды `task-cli list`:
```
|-------+----------------------+--------------+---------------------+---------------------|
| ID    | Description          | Status       | Created             | Updated             |
|-------+----------------------+--------------+---------------------+---------------------|
| 1     | Купить продукты      | done         | 2025-04-23T10:00:00 | 2025-04-23T10:05:00 |
|-------+----------------------+--------------+---------------------+---------------------|
```

## Требования
- **JDK 23**
- Windows (для установщика, созданного с помощью Inno Setup).
- Gradle (для сборки проекта).

## Установка

### Для пользователей (Windows)
1. Скачай `task-tracker-cli-installer.exe` из [Releases](https://github.com/foxseele/task-tracker-cli/releases).
2. Запусти установщик с правами администратора.
3. Убедись, что JDK 23 установлен:
    - Скачай JDK 23 с [Adoptium](https://adoptium.net/temurin/releases/?version=23).
    - Добавь `C:\Program Files\Eclipse Adoptium\jdk-23.0.1+9\bin` (или аналогичный путь) в системную переменную `Path`.
4. Перезапусти PowerShell и используй команды, например:
   ```powershell
   task-cli add "Купить продукты"
   task-cli list
   ```

### Для разработчиков (сборка из исходников)
1. Клонируй репозиторий:
   ```bash
   git clone https://github.com/foxseele/task-tracker-cli.git
   cd task-tracker-cli
   ```
2. Убедись, что JDK 23 установлен.
3. Собери проект с помощью Gradle:
   ```bash
   ./gradlew clean build
   ```
   Это создаст `task-tracker-cli-1.0.jar` в `build/libs`.
4. (Опционально) Создай установщик с помощью Inno Setup:
    - Установи [Inno Setup](https://jrsoftware.org/isdl.php).
    - Скопируй `task-tracker-cli-1.0.jar` в папку `installer`.
    - Открой `installer/task-tracker-cli-installer.iss` в Inno Setup и нажми "Compile".
    - Установщик `task-tracker-cli-installer.exe` появится в `installer/Output`.

## Использование
После установки ты можешь использовать следующие команды:
- `task-cli add <описание>` — добавить задачу.
- `task-cli update <id> <новое_описание>` — обновить задачу.
- `task-cli delete <id>` — удалить задачу.
- `task-cli mark-in-progress <id>` — отметить задачу как выполняемую.
- `task-cli mark-done <id>` — отметить задачу как завершённую.
- `task-cli list [todo|in-progress|done]` — вывести список задач (с фильтром или без).

Пример:
```powershell
task-cli add "Протестировать приложение"
task-cli list
task-cli mark-done 1
task-cli list done
```

Данные хранятся в `%LocalAppData%\TaskTrackerCLI\tasks.json`.

## Лицензия
Проект распространяется под лицензией MIT. См. [LICENSE](LICENSE) для подробностей.

## Ссылка на проект
Проект доступен на платформе [Roadmap.sh](https://roadmap.sh/projects/task-tracker).

## Автор
Мурат FoxSeele Макаов
