package indigo

class Deck: CardHolder() {
    init {
        putCards(suits.flatMap { suit -> ranks.map { Card(it, suit) } }.shuffled().toMutableList())
    }
}