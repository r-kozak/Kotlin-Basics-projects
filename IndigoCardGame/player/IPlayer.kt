package indigo.player

import indigo.Card

interface IPlayer {
    fun toss(tableTopCard: Card?): Card?
    fun cardsInHands(): String
}