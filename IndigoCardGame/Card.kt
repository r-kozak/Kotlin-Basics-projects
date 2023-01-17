package indigo

data class Card(val rank: String, val suit: String) {

    private val winningRanks = "A 10 J Q K".split(" ")

    override fun toString(): String = rank + suit

    fun similar(card: Card?): Boolean {
        return if (card == null) false
        else similarRank(card.rank) || similarSuit(card.suit)
    }

    fun similarRank(rank: String) = this.rank == rank

    fun similarSuit(suit: String) = this.suit == suit

    fun scoresCount(): Int {
        return if (rank in winningRanks) 1 else 0
    }
}
