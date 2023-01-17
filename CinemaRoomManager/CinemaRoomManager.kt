package cinema

import java.lang.IndexOutOfBoundsException

const val BUSY_SEAT = 'B'
const val EMPTY_SEAT = "S"

val statisticsData = StatisticsData()

fun main() {
    val field: Array<Array<Char>>

    println("Enter the number of rows:")
    val rowsCount = read()
    println("Enter the number of seats in each row:")
    val seatsCount = read()
    val totalSeats = rowsCount * seatsCount

    statisticsData.totalIncome = countTotalIncome(rowsCount, seatsCount, totalSeats)

    field = createField(rowsCount, seatsCount)

    var menuSelection = getMenuSelection()
    while (menuSelection != 0) {
        when (menuSelection) {
            1 -> printField(field)
            2 -> buyTicket(field, rowsCount, totalSeats)
            3 -> printStatistics()
        }
        menuSelection = getMenuSelection()
    }
}

fun printStatistics() {
    println("""
        Number of purchased tickets: ${statisticsData.purchasedTicketsCount}
        Percentage: ${"%.2f".format(statisticsData.percentage)}%
        Current income: $${statisticsData.currentIncome}
        Total income: $${statisticsData.totalIncome}
    """.trimIndent())
}

fun countTotalIncome(rowsCount: Int, seatsCount: Int, totalSeats: Int): Int {
    return if (totalSeats <= 60) {
        totalSeats * 10
    } else {
        var frontRowsCount = rowsCount / 2
        var backRowsCount = frontRowsCount
        if (rowsCount % 2 == 1) {
            frontRowsCount = rowsCount / 2
            backRowsCount = rowsCount / 2 + 1
        }
        (frontRowsCount * 10 + backRowsCount * 8) * seatsCount
    }
}

fun buyTicket(field: Array<Array<Char>>, rowsCount: Int, totalSeats: Int) {
    println("Enter a row number:")
    val rowNumber = read()
    println("Enter a seat number in that row:")
    val seatNumber = read()

    try {
        if (field[rowNumber][seatNumber] == BUSY_SEAT) {
            println("That ticket has already been purchased!")
            buyTicket(field, rowsCount, totalSeats)
            return
        }
    } catch (e: IndexOutOfBoundsException) {
        println("Wrong input!")
        buyTicket(field, rowsCount, totalSeats)
        return
    }

    val ticketPrice = if (totalSeats <= 60) {
        10
    } else {
        val frontRowsCount = rowsCount / 2
        if (rowNumber <= frontRowsCount) {
            10
        } else {
            8
        }
    }
    println("Ticket price: $$ticketPrice")

    statisticsData.currentIncome += ticketPrice
    statisticsData.purchasedTicketsCount++
    statisticsData.percentage = statisticsData.purchasedTicketsCount * 100.0 / totalSeats

    field[rowNumber][seatNumber] = BUSY_SEAT
}

fun getMenuSelection(): Int {
    println("""
        1. Show the seats
        2. Buy a ticket
        3. Statistics
        0. Exit
        """.trimIndent())
    return read()
}

fun createField(rowsCount: Int, seatsCount: Int): Array<Array<Char>> {
    val result = mutableListOf<Array<Char>>()
    val firstLine = (0..seatsCount).toMutableList().joinToString("") { if (it == 0) " " else "$it" }
    result.add(0, firstLine.toCharArray().toTypedArray())

    for (i in 1..rowsCount) {
        result.add("$i${EMPTY_SEAT.repeat(seatsCount)}".toCharArray().toTypedArray())
    }
    return result.toTypedArray()
}

fun printField(field: Array<Array<Char>>) {
    println("\nCinema:")
    /* println((0..seatsCount).toMutableList().joinToString(" ") { if (it == 0) " " else "$it" })
    for (i in 1..rowsCount) {
        var rowStr = "$i${EMPTY_SEAT.repeat(seatsCount)}"
        // add a space after each char
        // println(rowStr.chunked(1).joinToString(" "))
        println(rowStr.replace(Regex("(?<!^)(\\B|b)(?!\$)"), " "))
    } */
    for (i in field) {
        println(i.joinToString(" "))
    }
    println()
}

fun read() = readLine()!!.toInt()
