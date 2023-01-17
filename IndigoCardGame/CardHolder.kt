package indigo

open class CardHolder {
    protected val ranks = "A 2 3 4 5 6 7 8 9 10 J Q K".split(" ")
    protected val suits = "♦ ♥ ♠ ♣".split(" ")

    private var cards = mutableListOf<Card>()

    fun takeCards(cardNumbers: Int): List<Card> {
        val pickedCards = cards.slice(0 until cardNumbers)
        cards.removeAll(pickedCards)
        return pickedCards
    }

    fun allCards() = cards.toList()

    fun takeCard(index: Int) = cards.removeAt(index)

    fun takeCard(card: Card) = cards.remove(card)

    fun putCard(cardToPut: Card) = cards.add(cardToPut)

    fun putCards(cardsToPut: List<Card>) = cards.addAll(cardsToPut)

    fun cardsCount() = cards.size

    fun topCard() = cards.lastOrNull()

    fun hasNoCards(): Boolean = cards.size == 0
}