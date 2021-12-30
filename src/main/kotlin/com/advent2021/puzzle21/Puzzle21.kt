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
            for (player1Dice in 1..10) {
                val thisPlayer = PlayerState(player1Dice, playerOneScore)
                var playerTwoScore = 20
                while (playerTwoScore >= 0) {
                    for (player2Dice in 1..10) {
                        val otherPlayer = PlayerState(player2Dice, playerTwoScore)
                        val universe = GameState(thisPlayer, otherPlayer)
                        playUniverse(universe, working)
                    }
                    --playerTwoScore
                }
            }
            --playerOneScore
        }

        val answer = getState(GameState(data[0].toState(), data[1].toState()), working)!!
        return if (answer.activePlayer.compareTo(answer.otherPlayer) > 0) {
            answer.activePlayer
        } else {
            answer.otherPlayer
        }
    }

    fun playUniverse(universe: GameState, working: Working): WinsCount {
        val subUniverses = ArrayList<GameState>()

        val thisPlayer: PlayerState = universe.activePlayer
        val otherPlayer: PlayerState = universe.otherPlayer

        // dice roll 1
        (1..3).map { Dice(it).add(thisPlayer.diceNumber) }.forEach { total1 ->
            val subUniverse1 = GameState(PlayerState(total1, thisPlayer.score + total1), otherPlayer)
            subUniverses.add(subUniverse1)

            if (!subUniverse1.activePlayerWon()) {

                // dice roll 2
                (1..3).map { Dice(it).add(thisPlayer.diceNumber) }.forEach { total2 ->
                    val subUniverse2 = GameState(PlayerState(total2, thisPlayer.score + total2), otherPlayer)
                    subUniverses.add(subUniverse2)

                    if (!subUniverse2.activePlayerWon()) {

                        // dice roll 3
                        (1..3).map { Dice(it).add(thisPlayer.diceNumber) }.forEach { total3 ->
                            subUniverses.add(GameState(PlayerState(total3, thisPlayer.score + total3), otherPlayer))
                        }
                    }
                }
            }
        }

        // store for the next time
        val winsCount = calcSubuniversesCount(subUniverses, working)
        working[universe] = winsCount
        return winsCount
    }

    private fun calcSubuniversesCount(
        subUniverses: ArrayList<GameState>,
        working: Working
    ): WinsCount {
        return subUniverses.fold(WinsCount()) { acc, subUniverse ->
            val subWinsCount: WinsCount = when {
                subUniverse.activePlayerWon() -> WinsCount().win()
                else -> {
                    val flipped = subUniverse.flip()
                    var state = getState(flipped, working)
                    if (state == null) {
                        state = playUniverse(flipped, working)
                    }
                    state
                }
            }
            acc + subWinsCount
        }
    }

    fun getState(state: GameState, working: Working): WinsCount? {
        // it doesn't matter if we have seen our state or the flipped state as long as we add proper counts
        return working[state] ?: working[state.flip()]?.flip()
    }
}