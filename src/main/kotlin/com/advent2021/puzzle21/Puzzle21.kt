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
    val player1: PlayerState,
    val player2: PlayerState
) {
    fun player1Won(): Boolean = player1.score >= 21
}

data class WinsCount(
    val player1Wins: BigInteger = BigInteger.ZERO,
    val player2Wins: BigInteger = BigInteger.ZERO
) {
    fun swap() = WinsCount(player2Wins, player1Wins)
    operator fun plus(other: WinsCount) = WinsCount(player1Wins.add(other.player1Wins), player2Wins.add(other.player2Wins))
    fun win1() = WinsCount(player1Wins.inc(), player2Wins)
    fun win2() = WinsCount(player1Wins, player2Wins.inc())
    fun win(firstPlayerTurn: Boolean) = if (firstPlayerTurn) win1() else win2()
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
            for (player1Dice in 1..10) {
                val thisPlayer = PlayerState(player1Dice, playerOneScore)
                var playerTwoScore = 20
                while (playerTwoScore >= 0) {
                    for (player2Dice in 1..10) {
                        val otherPlayer = PlayerState(player2Dice, playerTwoScore)
                        val universe = GameState(thisPlayer, otherPlayer)
                        playUniverse(universe, working, true)
                    }
                    --playerTwoScore
                }
            }
            --playerOneScore
        }

        val answer = getState(GameState(data[0].toState(), data[1].toState()), working, true)!!
        return if (answer.player1Wins > answer.player2Wins) answer.player1Wins else answer.player2Wins
    }

    fun playUniverse(universe: GameState, working: Working, firstPlayerTurn: Boolean): WinsCount {
        val subUniverses = ArrayList<GameState>()

        // dice roll 1
        (1..3).map { Dice(it).add(universe.player1.diceNumber) }.forEach { total1 ->
            val subUniverse1 = GameState(PlayerState(total1, universe.player1.score + total1), universe.player2)
            subUniverses.add(subUniverse1)

            if (!subUniverse1.player1Won()) {

                // dice roll 2
                (1..3).map { Dice(it).add(universe.player1.diceNumber) }.forEach { total2 ->
                    val subUniverse2 = GameState(PlayerState(total2, universe.player1.score + total2), universe.player2)
                    subUniverses.add(subUniverse2)

                    if (!subUniverse2.player1Won()) {

                        // dice roll 3
                        (1..3).map { Dice(it).add(universe.player1.diceNumber) }.forEach { total3 ->
                            subUniverses.add(GameState(PlayerState(total3, universe.player1.score + total3), universe.player2))
                        }
                    }
                }
            }
        }

        // the working set keeps the stack from becoming too deep
        val winsCount = subUniverses.fold(WinsCount()) { acc, subUniverse ->
            val subWinsCount: WinsCount = when {
                subUniverse.player1Won() -> WinsCount().win(firstPlayerTurn)
                else -> {
                    var state = getState(subUniverse, working, !firstPlayerTurn)
                    if (state == null) {
                        state = playUniverse(subUniverse, working, !firstPlayerTurn)
                    }
                    state
                }
            }
            acc + subWinsCount
        }

        // store for the next time
        working[universe] = winsCount
        return winsCount
    }

    fun getState(state: GameState, working: Working, firstPlayerTurn: Boolean): WinsCount? {
        val lookup = working[state]
        return when (firstPlayerTurn) {
            true -> lookup
            false -> lookup?.swap()
        }
    }
}

