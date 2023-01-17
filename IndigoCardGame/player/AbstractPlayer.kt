package indigo.player

import indigo.CardHolder

abstract class AbstractPlayer(open val name: String): CardHolder(), IPlayer {
    inner class Account(var scores: Int = 0, var cards: Int = 0)
    var account = Account()
}