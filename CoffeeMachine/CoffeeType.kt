package machine

enum class CoffeeType(val water: Int, val milk: Int, val coffee: Int, val costs: Int) {
    NONE(0,0,0,0),
    ESPRESSO(250, 0, 16, 4),
    LATTE(350, 75, 20, 7),
    CAPPUCCINO(200, 100, 12, 6)
}
