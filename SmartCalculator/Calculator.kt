package calculator

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.util.*
import kotlin.math.*

class Calculator {
    private enum class Action {
        VAR_ASSIGN, VAR_PRINT, CALCULATE
    }

    private val vars = mutableMapOf<String, BigInteger>()

    private val validVarNameRegex: Regex = Regex("[a-zA-Z]+")
    private val multipleOperatorsRegex: Regex = Regex("/{2,}|\\*{2,}|\\^{2,}")
    private val invalidVarNameRegex: Regex = Regex("[0-9]+[a-zA-Z]+[0-9]?|[0-9]?[a-zA-Z]+[0-9]+")

    fun processInput(userInput: String) {
        if (userInput.isBlank()) return

        val expression = StringUtils.toMathExpression(userInput)
        val action = getSupposedAction(expression)
        if (canExpressionBeProcessed(expression, action)) processExpression(expression, action)
    }

    private fun canExpressionBeProcessed(expression: String, action: Action): Boolean {
        when(action) {
            Action.VAR_PRINT -> {
                if (!expression.matches(validVarNameRegex)) {
                    println("Invalid identifier")
                    return false
                }
                if (!vars.contains(expression)) {
                    println("Unknown variable")
                    return false
                }
            }
            Action.VAR_ASSIGN -> {
                val sides = expression.split("=")
                if (!sides[0].matches(validVarNameRegex)) {
                    println("Invalid identifier")
                    return false
                }
                if (sides.size > 2 || !canBeCalculated(sides[1])) {
                    println("Invalid assignment")
                    return false
                }
                if (!areVarsKnown(sides[1])) {
                    println("Unknown variable")
                    return false
                }
            }
            Action.CALCULATE -> {
                if (!canBeCalculated(expression)) {
                    println("Invalid expression")
                    return false
                }
                if (!areVarsKnown(expression)) {
                    println("Unknown variable")
                    return false
                }
            }
        }
        return true
    }

    private fun canBeCalculated(expression: String): Boolean {
        return invalidVarNameRegex.find(expression) == null &&
        multipleOperatorsRegex.find(expression) == null &&
                parenthesesCorrect(expression)
    }

    private fun areVarsKnown(expression: String): Boolean {
        val matches = validVarNameRegex.findAll(expression)
        for (match in matches) {
            if (!vars.contains(match.value)) return false
        }
        return true
    }

    private fun getSupposedAction(expression: String): Action {
        return if(expression.contains("=")) {
            Action.VAR_ASSIGN
        } else if (expression.matches(validVarNameRegex)) {
            Action.VAR_PRINT
        } else {
            Action.CALCULATE
        }
    }

    private fun processExpression(expression: String, action: Action) {
        when(action) {
            Action.VAR_PRINT -> println(vars[expression])
            Action.VAR_ASSIGN -> {
                val sides = expression.split("=")
                vars[sides[0]] = calculate(replaceVarsInExpression(sides[1]), 0).toBigInteger()
            }
            Action.CALCULATE -> {
                println(calculate(replaceVarsInExpression(expression), 0).toBigInteger())
            }
        }
    }

    private fun replaceVarsInExpression(expression: String): String {
        var result = expression
        val matches = validVarNameRegex.findAll(expression)
        for (match in matches) {
            val varName = match.value
            result = result.replace(varName, vars[varName].toString())
        }
        return result
    }

    private fun parenthesesCorrect(expression: String): Boolean {
        val stack = Stack<Char>()
        for (c in expression) {
            if (c == '(') {
                stack.push(c)
            } else if (c == ')') {
                if (stack.empty()) {
                    return false
                } else {
                    stack.pop()
                }
            }
        }
        return stack.empty()
    }

    private fun calculate(expression: String, countOperation: Int): BigDecimal {
        val value = expression.replace(" ", "")
        var currentCountOperation = countOperation + 1
        var inside = 0
        var p1 = -1
        var p2 = -1
        var p3 = -1
        val s = value.toCharArray()
        for (i in s.indices.reversed()) {
            when (s[i]) {
                '^' -> if (inside == 0 && p3 == -1) p3 = i
                '*', '/' -> if (inside == 0 && p2 == -1) p2 = i
                '+', '-' -> if (inside == 0 && p1 == -1) p1 = i
                '(' -> inside++
                ')' -> inside--
            }
        }
        if (p1 != -1) p2 = p1
        if (p2 != -1) p3 = p2
        if (p3 != -1) {
            val oldStream = System.out
            val outputStream = ByteArrayOutputStream()
            val newStream = PrintStream(outputStream)
            System.setOut(newStream)
            calculate(value.substring(0, p3), currentCountOperation)
            val part1 = outputStream.toString().trim().split(" ")
            currentCountOperation = max(currentCountOperation, part1[1].toInt())
            outputStream.reset()
            calculate(value.substring(p3 + 1), currentCountOperation)
            val part2 = outputStream.toString().trim().split(" ")
            currentCountOperation = max(currentCountOperation, part2[1].toInt())
            System.setOut(oldStream)
            when (s[p3]) {
                '^' -> {
                    return customPrint(
                        part1[0].toBigDecimal().pow(part2[0].toInt()),
                        countOperation,
                        currentCountOperation
                    )
                }
                '*' -> {
                    return customPrint(part1[0].toBigDecimal() * part2[0].toBigDecimal(), countOperation, currentCountOperation)
                }
                '/' -> {
                    return customPrint(part1[0].toBigDecimal() / part2[0].toBigDecimal(), countOperation, currentCountOperation)
                }
                '+' -> {
                    return customPrint(part1[0].toBigDecimal() + part2[0].toBigDecimal(), countOperation, currentCountOperation)
                }
                '-' -> {
                    return customPrint(part1[0].toBigDecimal() - part2[0].toBigDecimal(), countOperation, currentCountOperation)
                }
            }
        }
        if (s.isNotEmpty() && s[0] == '(' && s[s.size - 1] == ')') {
            return calculate(value.substring(1, s.size - 1), countOperation)
        }
        if (s.size > 5 && Character.isAlphabetic(s[0].code) && s[3] == '(' && s[s.size - 1] == ')') {
            val funcName = value.substring(0, 3)
            val oldStream = System.out
            val outputStream = ByteArrayOutputStream()
            val newStream = PrintStream(outputStream)
            System.setOut(newStream)
            calculate(value.substring(4, s.size - 1), currentCountOperation)
            val part = outputStream.toString().trim().split(" ")
            currentCountOperation = max(currentCountOperation, part[1].toInt())
            System.setOut(oldStream)
            if ("sin" == funcName) {
                return customPrint(sin(Math.toRadians(part[0].toDouble())).toBigDecimal(), countOperation, currentCountOperation)
            }
            if ("cos" == funcName) {
                return customPrint(cos(Math.toRadians(part[0].toDouble())).toBigDecimal(), countOperation, currentCountOperation)

            }
            if ("tan" == funcName) {
                return customPrint(tan(Math.toRadians(part[0].toDouble())).toBigDecimal(), countOperation, currentCountOperation)
            }
        }
        var n = BigDecimal(0)
        val sb = StringBuilder()
        var i = 0
        while (i < s.size && (Character.isDigit(s[i]) || s[i] == '.')) {
            sb.append(s[i])
            i++
        }
        if (sb.isNotEmpty()) {
            n = sb.toString().toBigDecimal()
        }
        return customPrint(n, countOperation, countOperation)
    }

    private fun customPrint(v: BigDecimal, countOperation: Int, currentCountOperation: Int): BigDecimal {
        return if (countOperation == 0) {
            Locale.setDefault(Locale.ENGLISH)
            val df = DecimalFormat("#.##")
            df.format(v).toBigDecimal()
        } else {
            println("$v $currentCountOperation")
            v
        }
    }
}
