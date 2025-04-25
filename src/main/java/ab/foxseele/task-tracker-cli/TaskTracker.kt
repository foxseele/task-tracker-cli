package ab.foxseele.`task-tracker-cli`
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter

@Serializable
data class Task(
    val id: Int,
    val description: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
) {
    fun withDescription(newDescription: String): Task {
        return copy(description = newDescription, updatedAt = DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
    }

    fun withStatus(newStatus: String): Task {
        return copy(status = newStatus, updatedAt = DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
    }
}

class TaskStorage {
    private val file: File
        get() {
            val localAppData = System.getenv("LocalAppData") ?: throw IllegalStateException("LocalAppData environment variable not found")
            val appDir = File(localAppData, "TaskTrackerCLI")
            val taskFile = File(appDir, "tasks.json")
            taskFile.parentFile.mkdirs()
            if (!taskFile.exists()) {
                taskFile.writeText("[]")
            }
            return taskFile
        }

    fun loadTasks(): MutableList<Task> {
        return if (file.exists()) {
            val json = file.readText()
            Json.decodeFromString(json)
        } else {
            mutableListOf()
        }
    }

    fun saveTasks(tasks: List<Task>) {
        val json = Json.encodeToString(tasks)
        file.writeText(json)
    }
}

class TaskManager(private val storage: TaskStorage) {
    private val tasks: MutableList<Task>
        get() = storage.loadTasks()

    fun add(description: String): Task {
        val tasks = this.tasks
        val newId = if (tasks.isEmpty()) 1 else tasks.maxOf { it.id } + 1
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val newTask = Task(newId, description, "todo", timestamp, timestamp)
        tasks.add(newTask)
        storage.saveTasks(tasks)
        return newTask
    }

    fun update(id: Int, description: String): Task? {
        val tasks = this.tasks
        val task = tasks.find { it.id == id } ?: return null
        val updatedTask = task.withDescription(description)
        tasks[tasks.indexOf(task)] = updatedTask
        storage.saveTasks(tasks)
        return updatedTask
    }

    fun delete(id: Int): Boolean {
        val tasks = this.tasks
        val task = tasks.find { it.id == id } ?: return false
        tasks.remove(task)
        storage.saveTasks(tasks)
        return true
    }

    fun mark(id: Int, status: String): Task? {
        if (status != "in-progress" && status != "done") return null
        val tasks = this.tasks
        val task = tasks.find { it.id == id } ?: return null
        val updatedTask = task.withStatus(status)
        tasks[tasks.indexOf(task)] = updatedTask
        storage.saveTasks(tasks)
        return updatedTask
    }

    fun list(filter: String? = null): List<Task> {
        val tasks = this.tasks
        return when (filter) {
            "todo" -> tasks.filter { it.status == "todo" }
            "in-progress" -> tasks.filter { it.status == "in-progress" }
            "done" -> tasks.filter { it.status == "done" }
            else -> tasks
        }
    }
}

fun printTasksTable(tasks: List<Task>) {
    if (tasks.isEmpty()) {
        println("Список задач пуст")
        return
    }

    // Определяем длины столбцов
    val idWidth = 5
    val descWidth = tasks.maxOfOrNull { it.description.length }?.coerceAtLeast(12) ?: 12
    val statusWidth = 12
    val createdWidth = 19
    val updatedWidth = 19

    // Заголовок таблицы
    val header = "| %-${idWidth}s | %-${descWidth}s | %-${statusWidth}s | %-${createdWidth}s | %-${updatedWidth}s |".format(
        "ID", "Description", "Status", "Created", "Updated"
    )
    val separator = "|-${"-".repeat(idWidth + 2)}+-${"-".repeat(descWidth + 2)}+-${"-".repeat(statusWidth + 2)}+-${"-".repeat(createdWidth + 2)}+-${"-".repeat(updatedWidth + 2)}|"

    println(separator)
    println(header)
    println(separator)

    // Строки таблицы
    tasks.forEach { task ->
        val row = "| %-${idWidth}d | %-${descWidth}s | %-${statusWidth}s | %-${createdWidth}s | %-${updatedWidth}s |".format(
            task.id,
            task.description.take(descWidth),
            task.status,
            task.createdAt.take(createdWidth),
            task.updatedAt.take(updatedWidth)
        )
        println(row)
    }
    println(separator)
}

fun main(args: Array<String>) {
    val storage = TaskStorage()
    val manager = TaskManager(storage)

    if (args.isEmpty()) {
        println("Использование: task-cli <команда> [аргументы]")
        println("Команды: add, update, delete, mark-in-progress, mark-done, list [todo|in-progress|done]")
        return
    }

    when (args[0]) {
        "add" -> {
            if (args.size < 2) {
                println("Ошибка: Укажите описание задачи")
            } else {
                val description = args.drop(1).joinToString(" ")
                val task = manager.add(description)
                println("Задача добавлена успешно (ID: ${task.id})")
            }
        }
        "update" -> {
            if (args.size < 3) {
                println("Ошибка: Укажите ID и новое описание")
            } else {
                val id = args[1].toIntOrNull()
                if (id == null) {
                    println("Ошибка: ID должен быть числом")
                } else {
                    val description = args.drop(2).joinToString(" ")
                    val updatedTask = manager.update(id, description)
                    if (updatedTask != null) {
                        println("Задача обновлена успешно (ID: $id)")
                    } else {
                        println("Ошибка: Задача с ID $id не найдена")
                    }
                }
            }
        }
        "delete" -> {
            if (args.size < 2) {
                println("Ошибка: Укажите ID задачи")
            } else {
                val id = args[1].toIntOrNull()
                if (id == null) {
                    println("Ошибка: ID должен быть числом")
                } else {
                    val success = manager.delete(id)
                    if (success) {
                        println("Задача удалена успешно (ID: $id)")
                    } else {
                        println("Ошибка: Задача с ID $id не найдена")
                    }
                }
            }
        }
        "mark-in-progress" -> {
            if (args.size < 2) {
                println("Ошибка: Укажите ID задачи")
            } else {
                val id = args[1].toIntOrNull()
                if (id == null) {
                    println("Ошибка: ID должен быть числом")
                } else {
                    val updatedTask = manager.mark(id, "in-progress")
                    if (updatedTask != null) {
                        println("Задача помечена как выполняемая (ID: $id)")
                    } else {
                        println("Ошибка: Задача с ID $id не найдена")
                    }
                }
            }
        }
        "mark-done" -> {
            if (args.size < 2) {
                println("Ошибка: Укажите ID задачи")
            } else {
                val id = args[1].toIntOrNull()
                if (id == null) {
                    println("Ошибка: ID должен быть числом")
                } else {
                    val updatedTask = manager.mark(id, "done")
                    if (updatedTask != null) {
                        println("Задача помечена как завершённая (ID: $id)")
                    } else {
                        println("Ошибка: Задача с ID $id не найдена")
                    }
                }
            }
        }
        "list" -> {
            val filter = if (args.size > 1) args[1] else null
            val tasks = manager.list(filter)
            printTasksTable(tasks)
        }
        else -> println("Неизвестная команда: ${args[0]}")
    }
}