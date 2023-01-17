package converter

import java.math.BigDecimal
import kotlin.math.pow
import java.math.BigInteger
import java.math.RoundingMode

const val SYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun main() {
    // println(fractionTo10Base("XY", 35, 9))
    // println(fractionFrom10Base("970612244", 17.toBigInteger(), 5))
    firstLevel()
}

fun firstLevel() {
    while (true) {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        when (val userInput = readln()) {
            "/exit" -> break
            else -> secondLevel(userInput.split(' ').map{it.toInt()}.toMutableList())
        }
    }
}

fun secondLevel(bases: MutableList<Int>) {
    val (sourceBase, targetBase) = bases
    while (true) {
        println("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back)")
        when (val userInput = readln()) {
            "/back" -> break
            else -> convertNumbers(userInput.uppercase(), sourceBase, targetBase)
        }
    }
}

fun convertNumbers(numberStr: String, sourceBase: Int, targetBase: Int) {
    var result = ""
    if (numberStr.contains(".")) {
        var (number, fraction) = numberStr.split(".")

        number = if (number != "0") from10(to10(number, sourceBase), targetBase) else "0"

        fraction = if (isFractionNotEquals0(fraction)) {
            fractionFrom10Base(fractionTo10Base(fraction, sourceBase, 9), targetBase.toBigInteger(), 5)
        } else {
            "0".repeat(5)
        }
        fraction = fraction.padEnd(5, '0')
        result = "$number.$fraction"
    } else {
        result = from10(to10(numberStr, sourceBase), targetBase)
    }
    println("Conversion result: ${result.lowercase()}\n")
}

fun isFractionNotEquals0(fraction: String): Boolean = fraction.count { it == '0' } != fraction.length

fun to10(number: String, base: Int): BigInteger = number.toBigInteger(base)

fun from10(number: BigInteger,  base: Int): String = number.toString(base)

fun fractionTo10Base(decimalFraction: String, sourceBase: Int, places: Int): String {
    val sourceBaseBd = sourceBase.toBigDecimal().setScale(places, RoundingMode.CEILING)
    var twos = sourceBaseBd
    var result = BigDecimal.ZERO
    for (i in decimalFraction.indices) {
        result += (charVal(decimalFraction[i]).toBigDecimal().setScale(places) / twos).setScale(places, RoundingMode.CEILING)
        twos *= sourceBaseBd
    }
    val resultStr = result.toString()
    if (resultStr.contains(".")) {
        return resultStr.split(".")[1]
    }
    return resultStr
}

fun fractionFrom10Base(decimalFraction: String, targetBase: BigInteger, places: Int): String {
    var numerator = decimalFraction.toBigInteger()
    val denominator = 10.0.pow(decimalFraction.length).toBigDecimal().toBigInteger()
    var result = ""
    for (i in 0 until places) {
        numerator *= targetBase
        result += SYMBOLS[(numerator / denominator).toInt()]
        numerator %= denominator
        if (numerator == BigInteger.ZERO) break
    }
    return result
}

fun charVal(c: Char): Int {
    return if (c in '0'..'9')
        c - '0';
    else
        c - 'A' + 10;
}