package calculator

import java.math.MathContext

object Commands {
    const val EXIT = "/exit"
    const val HELP = "/help"
    const val COMMAND_PREFIX = "/"
    const val UNKNOWN_MSG = "Unknown command"
}

fun main(args: Array<String>) {
    val calculator = Calculator()
    while (true) {
        val userInput = readln()
        when {
            userInput == Commands.EXIT -> break
            userInput == Commands.HELP -> println("This is a calc")
            userInput.startsWith(Commands.COMMAND_PREFIX) -> println(Commands.UNKNOWN_MSG)
            else -> calculator.processInput(userInput)
        }
    }
    println("Bye!")
}

