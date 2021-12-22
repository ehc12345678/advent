package com.advent2021.puzzle21

import com.advent2021.base.Base
import java.lang.IllegalStateException
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList

open class Dice(var number: Int) {
    fun add(i: Int): Int {
        number = wrap(number + i, 10, 1)
        return number
    }
    fun wrap(number: Int, mod: Int, base: Int) = ((number - base) % mod) + base
    open fun roll(): Int = 0
}
data class Player(val dice: Dice, var score: Int = 0) {
    fun toState() = PlayerState(dice.number, score)
}

class DeterministicDice : Dice(1) {
    override fun roll(): Int = number.also { add(1) }
}
typealias Data = ArrayList<Player>
typealias Solution = Int
typealias Solution2 = BigInteger

data class PlayerState(
    val diceNumber: Int,
    val score: Int
)
data class GameState(
    val player1: PlayerState,
    val player2: PlayerState
)

data class WinsCount(
    val player1Wins: BigInteger,
    val player2Wins: BigInteger
)
typealias Working = HashMap<GameState, WinsCount>

fun main() {
    try {
        val puz = Puzzle21()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputsTest.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle21 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(Player(Dice(line.substring(line.indexOf(": ") + 2).toInt())))
    }

    override fun computeSolution(data: Data): Solution {
        val deterministic = DeterministicDice()
        var winningPlayer: Player? = null
        var rolls = 0
        while (winningPlayer == null) {
            for (player in data) {
                doTurn(player, deterministic)
                rolls += 3
                if (player.score >= 1000) {
                    winningPlayer = player
                    break
                }
            }
        }

        return data.find { it != winningPlayer }!!.score * rolls
    }

    private fun doTurn(player: Player, otherDice: Dice) {
        val threeRolls = otherDice.roll() + otherDice.roll() + otherDice.roll()
        player.score += player.dice.add(threeRolls)
    }

    override fun computeSolution2(data: Data): Solution2 {
        val working = Working()

        var playerOneScore = 20
        while (playerOneScore >= data[0].dice.number) {
            var playerTwoScore = 20
            while (playerTwoScore >= data[1].dice.number) {
                computeWins(playerOneScore, playerTwoScore, working)
                --playerTwoScore
            }
            --playerOneScore
        }

        val answer = working[GameState(data[0].toState(), data[1].toState())]!!
        return if (answer.player1Wins > answer.player2Wins) answer.player1Wins else answer.player2Wins
    }

    fun computeWins(playerOneScore: Int, playerTwoScore: Int, working: Working) {
        for (dice in 1..10) {
            val playersDice = Dice(dice)
            var numPlayer1Wins = BigInteger.valueOf(0)
            var numPlayer2Wins = BigInteger.valueOf(0)

            val player1 = PlayerState(dice, playerOneScore)
            val player2 = PlayerState(dice, playerTwoScore)
            val thisState = GameState(player1, player2)
            for (newDice in getDiceInDescendingOrder(playersDice)) {
                val thisScore = newDice.number + player1.score
                if (thisScore >= 21) {
                    ++numPlayer1Wins
                } else {
                    for (otherDice in getDiceInDescendingOrder(playersDice)) {
                        val otherScore = otherDice.number + player1.score
                        if (otherScore >= 21) {
                            ++numPlayer2Wins
                        }
                        else {
                            val otherState =
                                GameState(
                                    PlayerState(newDice.number, thisScore),
                                    PlayerState(otherDice.number, otherScore)
                                )
                            val lookup = working[otherState]
                            if (lookup == null) {
                                throw IllegalStateException()
                            }
                            numPlayer1Wins += lookup.player2Wins
                            numPlayer2Wins += lookup.player1Wins
                        }
                    }
                }
            }
            working[thisState] = WinsCount(numPlayer1Wins, numPlayer2Wins)
        }
    }

    private fun getDiceInDescendingOrder(baseDice: Dice): List<Dice> {
        val theseDice = ArrayList<Dice>()
        for (quantum in 1..3) {
            theseDice.add(Dice(quantum).also { it.add(baseDice.number) })
        }
        theseDice.sortByDescending { it.number }
        return theseDice
    }

}

