package calculator

class StringUtils {
    companion object {
        fun toMathExpression(str: String): String {
            var result = str.replace("\\s".toRegex(), "")
            result = result.replace("-{2}".toRegex(), "+")
            result = result.replace("\\+{2,}".toRegex(), "+")
            result = result.replace("(\\+-|-\\+)".toRegex(), "-")
            return result
        }
    }
}