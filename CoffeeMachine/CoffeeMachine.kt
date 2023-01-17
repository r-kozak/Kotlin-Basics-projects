package machine

import kotlin.system.exitProcess

class CoffeeMachine {

    private val resources = Resources(400, 540, 120, 9, 550)

    fun start() {
        val actions = listOf(::buy, ::fill, ::take, ::remaining, ::exit).associateBy { it.name }
        while (true) {
            println("Write action (buy, fill, take, remaining, exit): ")
            actions[readln()]?.invoke()
        }
    }

    private fun buy() {
        println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ")
        val userChoice = readln()
        if (userChoice == "back") return

        val coffeeType = CoffeeType.values()[userChoice.toInt()]
        val missingResources = checkResources(coffeeType)
        if (missingResources.isEmpty()) {
            makeCoffee(coffeeType)
        } else {
            println("Sorry, not enough ${missingResources.joinToString(", ")}!")
        }
    }

    private fun makeCoffee(coffeeType: CoffeeType) {
        println("I have enough resources, making you a coffee!")
        resources.water -= coffeeType.water
        resources.milk -= coffeeType.milk
        resources.coffee -= coffeeType.coffee
        resources.disposableCups--
        resources.money += coffeeType.costs
    }

    private fun checkResources(coffeeType: CoffeeType): MutableList<String> {
        val missingIngredients = mutableListOf<String>()
        if (resources.water < coffeeType.water) missingIngredients.add("water")
        if (resources.milk < coffeeType.milk) missingIngredients.add("milk")
        if (resources.coffee < coffeeType.coffee) missingIngredients.add("coffee beans")
        if (resources.disposableCups == 0) missingIngredients.add("disposable cups")
        return missingIngredients
    }

    private fun fill() {
        println("Write how many ml of water you want to add:")
        resources.water += readln().toInt()
        println("Write how many ml of milk you want to add:")
        resources.milk += readln().toInt()
        println("Write how many grams of coffee beans you want to add:")
        resources.coffee += readln().toInt()
        println("Write how many disposable cups you want to add:")
        resources.disposableCups += readln().toInt()
    }

    private fun take() {
        println("I gave you $${resources.money}")
        resources.money = 0
    }

    private fun remaining() {
        println("""
            The coffee machine has:
            ${resources.water} ml of water
            ${resources.milk} ml of milk
            ${resources.coffee} g of coffee beans
            ${resources.disposableCups} disposable cups
            $${resources.money} of money
        """.trimIndent())
    }

    private fun exit() {
        exitProcess(0)
    }
}
