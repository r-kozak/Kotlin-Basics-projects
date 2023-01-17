package tictactoe

import java.lang.Exception
import java.lang.Math.abs
import java.util.*

const val EMPTY_CELL = " "
const val O: String = "O"
const val X: String = "X"

const val IMPOSSIBLE: String = "Impossible"
const val DRAW: String = "Draw"
const val O_WINS: String = "O wins"
const val X_WINS: String = "X wins"
const val GAME_NOT_FINISHED: String = "Game not finished"

val s = Scanner(System.`in`)

class Coordinates constructor(val x: Int, val y: Int)

fun main() {
    var currentPlayer = X
    val field: Array<Array<String>> = createField(EMPTY_CELL.repeat(9))
    printFieldState(field)

    var gameResult = GAME_NOT_FINISHED
    while (gameResult == IMPOSSIBLE || gameResult == GAME_NOT_FINISHED) {
        val moveData = makeMove(field)
        setMoveToField(field, moveData, currentPlayer)
        printFieldState(field)
        gameResult = defineGameResult(field)
        currentPlayer = tooglePlayer(currentPlayer)
    }
    print(gameResult)
}

fun tooglePlayer(currentPlayer: String): String = if (currentPlayer == X) O else X

fun makeMove(field: Array<Array<String>>): Coordinates {
    while (true) {
        print("Enter the coordinates: ")
        val coordinates = s.nextLine().split(" ")

        var result: Coordinates
        try {
            result = Coordinates(coordinates[0].toInt() - 1, coordinates[1].toInt() - 1)
        } catch (e: Exception) {
            println("You should enter numbers!")
            continue
        }
       if (!isDigitFrom1To3(coordinates[0]) || !isDigitFrom1To3(coordinates[1])) {
            println("Coordinates should be from 1 to 3!")
            continue
       } else if (field[result.x][result.y] != EMPTY_CELL) {
           println("This cell is occupied! Choose another one!")
           continue
       } else {
           return result
       }
    }
}

fun setMoveToField(field: Array<Array<String>>, coordinates: Coordinates, currentPlayer: String) {
    field[coordinates.x][coordinates.y] = currentPlayer
}

fun isDigitFrom1To3(digit: String): Boolean {
    return digit.toInt() in 1..3
}

fun printFieldState(field: Array<Array<String>>) {
    println("---------\n" +
            "| ${field[0][0]} ${field[0][1]} ${field[0][2]} |\n" +
            "| ${field[1][0]} ${field[1][1]} ${field[1][2]} |\n" +
            "| ${field[2][0]} ${field[2][1]} ${field[2][2]} |\n" +
            "---------")
}

fun defineGameResult(field: Array<Array<String>>): String {
    val isWinnerO = isWinner(O, field)
    val isWinnerX = isWinner(X, field)

    val fieldString = "${field[0][0]}${field[0][1]}${field[0][2]}${field[1][0]}${field[1][1]}${field[1][2]}${field[2][0]}${field[2][1]}${field[2][2]}"
    val countO = fieldString.count { it == O.first() }
    val countX = fieldString.count { it == X.first() }
    val countEmpty = fieldString.count { it == EMPTY_CELL.first() }

    val diffOfXO = abs(countO - countX)

    if ((isWinnerX && isWinnerO) || diffOfXO > 1) {
        return IMPOSSIBLE
    } else if (isWinnerO) {
        return O_WINS
    } else if (isWinnerX) {
        return X_WINS
    } else if (countEmpty == 0) {
        return DRAW
    } else {
        return GAME_NOT_FINISHED
    }
}

fun isWinner(XO: String, field: Array<Array<String>>): Boolean {
    //check rows and columns
    for (i in field.indices) {
        if (field[i][0] == XO && field[i][1] == XO && field[i][2] == XO) return true
        if (field[0][i] == XO && field[1][i] == XO && field[2][i] == XO) return true
    }
    //check diagonals
    if (field[1][1] == XO) {
        if (field[0][0] == XO && field[2][2] == XO) return true
        if (field[0][2] == XO && field[2][0] == XO) return true
    }
    return false
}

fun createField(input: String): Array<Array<String>> {
    val result = arrayOf(
        arrayOf("", "", ""),
        arrayOf("", "", ""),
        arrayOf("", "", "")
    )
    var inputIndex = 0;
    for (i in 0..2) {
        for (j in 0..2) {
            result[i][j] = input[inputIndex++].toString()
        }
    }
    return result
}