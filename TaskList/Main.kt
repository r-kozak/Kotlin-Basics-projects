package tasklist

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.*
import java.io.File
import tasklist.Task.*

fun main() { TaskManager().run() }

object Color {
    const val RED = "\u001B[101m \u001B[0m"
    const val YELLOW = "\u001B[103m \u001B[0m"
    const val GREEN = "\u001B[102m \u001B[0m"
    const val BLUE = "\u001B[104m \u001B[0m"
}
class Task(var priority: Priority, var date: String, var time: String, var lines: List<String>) {
    enum class Priority(val color: String) { C(Color.RED), H(Color.YELLOW), N(Color.GREEN), L(Color.BLUE) }
    enum class Tag(val color: String) { T(Color.YELLOW), I(Color.GREEN), O(Color.RED) }
    enum class Field { PRIORITY, DATE, TIME, TASK }
    private fun getTag(): Tag {
        val taskDate = LocalDateTime.parse("${date}T${time}").date
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val numberOfDays = currentDate.daysUntil(taskDate)
        return if (numberOfDays > 0) Tag.I else if (numberOfDays < 0) Tag.O else Tag.T
    }
    fun print(id: Int) {
        lines.forEachIndexed { i, line ->
            line.chunked(44).forEachIndexed { j, ch ->
                if (i == 0 && j == 0) print("| ${id.toString().padEnd(3)}| $date | $time | ${priority.color} | ${getTag().color} |")
                else print("|    |            |       |   |   |")
                println(ch.padEnd(44) + "|")
            }
        }
        println("+----+------------+-------+---+---+--------------------------------------------+")
    }
}

class TaskManager {
    val jsonFile = File("tasklist.json")
    private val tasks = mutableListOf<Task>()
    fun run() {
        loadTasks()
        while (true) {
            println("Input an action (add, print, edit, delete, end):")
            when(readln()) {
                "mockup" -> addMockUpData()
                "add" -> addTask()
                "print" -> printTasks()
                "delete" -> delete()
                "edit" -> edit()
                "end" -> saveTasks().run { println("Tasklist exiting!") }.run { return }
                else -> println("The input action is invalid")
            }
        }
    }
    private fun addMockUpData() {
        tasks.add(Task(Priority.C, "2021-01-02", "17:01", listOf("line 1")))
        tasks.add(Task(Priority.N, "2023-01-06", "13:04", listOf("line 1", "line 2")))
        tasks.add(Task(Priority.L, "2023-01-07", "13:08", listOf("Editing a task requires the change of priority, date, time, or task description. Due to IntelliJ run terminal limitations, editing can not be done over the data from the old task. That is, printing the old.")))
        tasks.add(Task(Priority.N, "2023-01-09", "15:09", listOf("The process of removing a task is based on the tasks sequence numbers. After deletion, the sequence numbers change.", "Line 2")))
        tasks.add(Task(Priority.H, "2025-12-23", "23:59", listOf("Don't forget to look into the stage 6 links about it.", "Find resources about Ansi colors and cursor movement.", "---Line 3")))
    }
    private fun askTaskNumber(): Int {
        return try {
            println("Input the task number (1-${tasks.size}):")
            val input = readln().toInt()
            if(input - 1 !in tasks.indices) throw Exception()
            input
        } catch (e: Exception) {
            println("Invalid task number")
            askTaskNumber()
        }
    }
    private fun delete() {
        if (tasks.isEmpty()) println("No tasks have been input").run { return }
        printTasks()
        tasks.removeAt(askTaskNumber() - 1)
        println("The task is deleted")
    }
    private fun edit() {
        if (tasks.isEmpty()) println("No tasks have been input").run { return }
        printTasks()
        val task = tasks[askTaskNumber() - 1]
        when(askField()) {
            Field.DATE -> task.date = askDate()
            Field.TIME -> task.time = askTime()
            Field.PRIORITY -> task.priority = askPriority()
            Field.TASK -> task.lines = askLines()
        }
        println("The task is changed")
    }
    private fun addTask() {
        val priority = askPriority()
        val date = askDate()
        val time = askTime()
        val taskLines = askLines()

        if (taskLines.isEmpty()) println("The task is blank")
        else tasks.add(Task(priority, date, time, taskLines))
    }
    private fun askLines(): List<String> {
        println("Input a new task (enter a blank line to end):")
        var userInput = readln()
        val taskLines = mutableListOf<String>()
        while (userInput.isNotBlank()) {
            taskLines.add(userInput.trim())
            userInput = readln()
        }
        return taskLines
    }
    private fun askTime(): String {
        return try {
            println("Input the time (hh:mm):")
            val i = readln().split(":")
            LocalDateTime(1, 1, 1, i[0].toInt(), i[1].toInt())
            "${i[0].padStart(2, '0')}:${i[1].padStart(2, '0')}"
        } catch (e: Exception) {
            println("The input time is invalid")
            askTime()
        }
    }
    private fun askDate(): String {
        return try {
            println("Input the date (yyyy-mm-dd):")
            val i = readln().split("-")
            LocalDate(i[0].toInt(), i[1].toInt(), i[2].toInt())
            "${i[0].padStart(4, '0')}-${i[1].padStart(2, '0')}-${i[2].padStart(2, '0')}"
        } catch (e: Exception) {
            println("The input date is invalid")
            askDate()
        }
    }
    private fun askPriority(): Priority {
        return try {
            println("Input the task priority (${Priority.values().joinToString(", ")}):")
            Priority.valueOf(readln().uppercase())
        } catch (e: IllegalArgumentException) {
            askPriority()
        }
    }
    private fun askField(): Field {
        return try {
            println("Input a field to edit (${Field.values().joinToString(", ") { it.toString().lowercase() }}):")
            Field.valueOf(readln().uppercase())
        } catch (e: IllegalArgumentException) {
            println("Invalid field")
            askField()
        }
    }
    private fun printTasks() {
        if (tasks.isEmpty()) println("No tasks have been input")
        else {
            println("""
                +----+------------+-------+---+---+--------------------------------------------+
                | N  |    Date    | Time  | P | D |                   Task                     |
                +----+------------+-------+---+---+--------------------------------------------+
                """.trimIndent())
            tasks.forEachIndexed { i, task -> task.print(i + 1) }
        }
    }
    private fun getTasksJsonAdapter(): JsonAdapter<List<Task>> {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(List::class.java, Task::class.java)
        return moshi.adapter(type)
    }
    private fun saveTasks() = jsonFile.writeText(getTasksJsonAdapter().toJson(tasks))

    private fun loadTasks() {
        if (jsonFile.exists()) {
            tasks.addAll(getTasksJsonAdapter().fromJson(jsonFile.readText())!!)
        }
    }
}