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
) {
    fun swap() = WinsCount(player2Wins, player1Wins)
}
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
        while (playerOneScore >= 0) {
            var playerTwoScore = 20
            while (playerTwoScore >= 0) {
                computeWins(playerOneScore, playerTwoScore, working)
                --playerTwoScore
            }
            --playerOneScore
        }

        val answer = getState(GameState(data[0].toState(), data[1].toState()), working, true)!!
        return if (answer.player1Wins > answer.player2Wins) answer.player1Wins else answer.player2Wins
    }

    fun computeWins(playerOneScore: Int, playerTwoScore: Int, working: Working) {
        for (dice in 1..10) {
            val playersDice = Dice(dice)

            val player1 = PlayerState(dice, playerOneScore)
            for (secondDice in 1..10) {
                val secondPlayersDice = Dice(secondDice)
                val player2 = PlayerState(secondDice, playerTwoScore)
                val thisState = GameState(player1, player2)

                // we have already seen this
                if (getState(thisState, working, true) != null) {
                    continue
                }

                var numPlayer1Wins = BigInteger.valueOf(0)
                var numPlayer2Wins = BigInteger.valueOf(0)

                for (newDice in getDiceInDescendingOrder(playersDice)) {
                    val thisScore = newDice.number + player1.score
                    if (thisScore >= 21) {
                        ++numPlayer1Wins
                    } else {
                        for (otherDice in getDiceInDescendingOrder(secondPlayersDice)) {
                            val otherScore = otherDice.number + player2.score
                            if (otherScore >= 21) {
                                ++numPlayer2Wins
                            } else {
                                val otherState =
                                    GameState(
                                        PlayerState(newDice.number, thisScore),
                                        PlayerState(otherDice.number, otherScore)
                                    )
                                val lookup = getState(otherState, working, false)
                                if (lookup == null) {
                                    val all =
                                        working.filter { it.key.player1 == PlayerState(newDice.number, thisScore) }
                                    throw IllegalStateException()
                                }
                                numPlayer1Wins += lookup.player1Wins
                                numPlayer2Wins += lookup.player2Wins
                            }
                        }
                    }
                }
                addState(thisState, numPlayer1Wins, numPlayer2Wins, working)
            }
        }
    }

    fun getState(state: GameState, working: Working, firstPlayerTurn: Boolean): WinsCount? {
        val otherState =  GameState(state.player2, state.player1)

        val lookup = working[state]
        val invLookup = working[otherState]
        val playerOneRet = lookup ?: invLookup

        return when (firstPlayerTurn) {
            true -> playerOneRet
            false -> playerOneRet?.swap()
        }
    }

    fun addState(state: GameState, numPlayer1Wins: BigInteger, numPlayer2Wins: BigInteger, working: Working) {
        working[state] = WinsCount(numPlayer1Wins, numPlayer2Wins)
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

