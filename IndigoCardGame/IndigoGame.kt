package indigo

import indigo.player.AbstractPlayer
import indigo.player.Computer
import indigo.player.Human
import kotlin.system.exitProcess

class IndigoGame(isHumanFirst: Boolean) {
    private object Constants {
        const val INITIAL_TABLE_CARDS_NUMBER = 4
        const val INITIAL_CARDS_NUMBER = 6
    }

    private lateinit var lastWinner: AbstractPlayer
    private var table = CardHolder()
    private var deck = Deck()
    private var human: AbstractPlayer = Human("Player")
    private var computer: AbstractPlayer = Computer("Computer")

    private var currentPlayer: AbstractPlayer = if (isHumanFirst) human else computer

    fun play() {
        dealInitialCards()
        while (true) {
            printTableInfo()
            if (currentPlayer.hasNoCards()) gameOver()
            currentPlayer.toss(table.topCard())?.let { processTossedCard(it) } ?: exit()
            if (currentPlayer.hasNoCards()) dealCards(currentPlayer)
            togglePlayer()
        }
    }

    private fun processTossedCard(tossedCard: Card) {
        val topCard = table.topCard()
        table.putCard(tossedCard)
        if (topCard?.let { tossedCard.similar(it) } == true) processWin(currentPlayer)
    }

    private fun processWin(winner: AbstractPlayer) {
        winner.account.scores += table.allCards().sumOf { it.scoresCount() }
        winner.account.cards += table.cardsCount()
        printWinInfo(true)
        // remove all cards from table
        table.takeCards(table.cardsCount())
        lastWinner = winner
    }

    private fun printWinInfo(needPrintWinner: Boolean) {
        if (needPrintWinner) println("${currentPlayer.name} wins cards")
        println("Score: Player ${human.account.scores} - Computer ${computer.account.scores}")
        println("Cards: Player ${human.account.cards} - Computer ${computer.account.cards}\n")
    }

    private fun togglePlayer() {
        currentPlayer = if (currentPlayer == human) computer else human
    }

    private fun gameOver() {
        lastWinner.account.scores += table.allCards().sumOf { it.scoresCount() }
        lastWinner.account.cards += table.cardsCount()
        if (human.account.cards >= computer.account.cards) {
            human.account.scores += 3
        } else {
            computer.account.scores += 3
        }
        printWinInfo(false)
        exit()
    }

    private fun printTableInfo() {
        if (table.cardsCount() > 0) {
            println("\n${table.cardsCount()} cards on the table, and the top card is ${table.topCard()}")
        } else {
            println("No cards on the table")
        }
    }

    private fun dealInitialCards() {
        table.putCards(deck.takeCards(Constants.INITIAL_TABLE_CARDS_NUMBER))
        println("Initial cards on the table: ${table.allCards().joinToString(" ")}")
        dealCards(computer)
        dealCards(human)
    }

    private fun dealCards(receiver: CardHolder) {
        if (!deck.hasNoCards()) receiver.putCards(deck.takeCards(Constants.INITIAL_CARDS_NUMBER))
    }

    private fun exit() {
        println("Game Over")
        exitProcess(0)
    }
}