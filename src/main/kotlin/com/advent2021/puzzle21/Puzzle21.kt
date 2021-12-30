package com.advent2021.puzzle21

import com.advent2021.base.Base
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
    internal val activePlayer: PlayerState,
    internal val otherPlayer: PlayerState
) {
    fun activePlayerWon(): Boolean = activePlayer.score >= 21
    fun flip() = GameState(otherPlayer, activePlayer)
}

data class WinsCount(
    val activePlayer: BigInteger = BigInteger.ZERO,
    val otherPlayer: BigInteger = BigInteger.ZERO
) {
    operator fun plus(cnt: WinsCount) = WinsCount(activePlayer.add(cnt.activePlayer), otherPlayer.add(cnt.otherPlayer))
    fun win() = WinsCount(activePlayer.inc(), otherPlayer)
    fun flip() = WinsCount(otherPlayer, activePlayer)
}
typealias Working = HashMap<GameState, WinsCount>

fun main() {
    try {
        val puz = Puzzle21()
//        val solution1 = puz.solvePuzzle("inputs.txt", Data())
//        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
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
        val initialState = GameState(data[0].toState(), data[1].toState())
        val answer = playUniverse(initialState, working)
        return if (answer.activePlayer.compareTo(answer.otherPlayer) > 0) {
            answer.activePlayer
        } else {
            answer.otherPlayer
        }
    }

    fun playUniverse(universe: GameState, working: Working): WinsCount {
        return working.getOrPut(universe) {
            val thisPlayer: PlayerState = universe.activePlayer
            var winsCount = WinsCount()

            (1..3).forEach { dice1 ->
                (1..3).forEach { dice2 ->
                    (1..3).forEach { dice3 ->
                        val newDice = Dice(thisPlayer.diceNumber).add(dice1 + dice2 + dice3)
                        val score = thisPlayer.score + newDice
                        if (score >= 21) {
                            winsCount = winsCount.win()
                        } else {
                            val childState = GameState(universe.otherPlayer, PlayerState(newDice, score))
                            val otherPlayer = playUniverse(childState, working)
                            winsCount += otherPlayer.flip()
                        }
                    }
                }
            }
            winsCount
        }
    }
}