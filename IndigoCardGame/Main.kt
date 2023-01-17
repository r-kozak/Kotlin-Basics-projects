package indigo

var isHumanFirst = false

fun main() {
    println("Indigo Card Game")
    askWhoIsFirstPlayer()
    IndigoGame(isHumanFirst).play()
}

private fun askWhoIsFirstPlayer() {
    do {
        println("Play first?")
        val answer = readln().lowercase().also { isHumanFirst = it == "yes" }
    } while (!answer.matches(Regex("yes|no")))
}