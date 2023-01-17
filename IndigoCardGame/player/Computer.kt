package indigo.player

import indigo.Card
import kotlin.random.Random

class Computer(override val name: String): AbstractPlayer(name) {
    override fun cardsInHands() = allCards().joinToString(" ")

    /**
     * @param tableTopCard - <null> if there are no cards on the table at all
     * @return the best card to toss on the table and win table`s cards
     */
    override fun toss(tableTopCard: Card?): Card {
        println(cardsInHands())
        // select all cards that can win the cards on the table
        val candidates = allCards().filter { it.similar(tableTopCard) }

        val tossedCard = if (cardsCount() == 1) allCards().first()
        else if (candidates.size == 1) candidates.first()
        else if (candidates.isEmpty()) chooseCardByMultipleSuitOrRankFrom(allCards())
        else chooseCardByMultipleSuitOrRankFrom(candidates)

        println("$name plays $tossedCard")
        takeCard(tossedCard)
        return tossedCard
    }

    private fun chooseCardByMultipleSuitOrRankFrom(cards: List<Card>): Card {
        val suitsMap = mutableMapOf<String, List<Card>>()
        suits.forEach { suit -> suitsMap[suit] = cards.filter { it.similarSuit(suit) } }
        val maxSuits = suitsMap.maxByOrNull { (_, value) -> value.size }!!.value

        val ranksMap = mutableMapOf<String, List<Card>>()
        ranks.forEach { rank -> ranksMap[rank] = cards.filter { it.similarRank(rank) } }
        val maxRanks = ranksMap.maxByOrNull { (_, value) -> value.size }!!.value

        return if (maxSuits.size > 1) takeRandomCard(maxSuits)
        else if (maxRanks.size > 1) takeRandomCard(maxRanks)
        else takeRandomCard(cards)
    }

    private fun takeRandomCard(cards: List<Card>) = cards[Random.nextInt(cards.size)]
}