package indigo.player

import indigo.Card

class Human(override val name: String): AbstractPlayer(name) {
    override fun cardsInHands() = allCards().mapIndexed{ i, c -> "${i+1})$c" }.joinToString(" ")

    override fun toss(tableTopCard: Card?): Card? {
        println("Cards in hand: ${cardsInHands()}")
        var cardNumber: Int? = null
        do {
            println("Choose a card to play (1-${cardsCount()}):")
            when (val inp = readln()) {
                "exit" -> return null
                else -> if (inp.toIntOrNull() in 1..cardsCount()) cardNumber = inp.toInt()
            }
        } while (cardNumber == null)
        return takeCard(cardNumber - 1)
    }
}