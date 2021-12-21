package com.advent2021.puzzle21

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayList

open class Dice(var number: Int) {
    fun add(i: Int): Int {
        number = wrap(number + i, 10, 1)
        return number
    }
    fun wrap(number: Int, mod: Int, base: Int) = ((number - base) % mod) + base
    open fun roll(): Int = 0
    fun copy() = Dice(number)
}
data class Player(val dice: Dice, var score: Int = 0) {
    fun copy() = Player(dice.copy(), score)
}

class DeterministicDice : Dice(1) {
    override fun roll(): Int = number.also { add(1) }
}
typealias Data = ArrayList<Player>
typealias Solution = Int
typealias Solution2 = Solution

fun Data.copy(): Data = Data(this)

fun main() {
    try {
        val puz = Puzzle21()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

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
        val playerOneTurnGames = Stack<Data>()
        playerOneTurnGames.add(data)

        val playerTwoTurnGames = Stack<Data()

        for (turn in 0 until 3) {
            while (playerOneTurnGames.isNotEmpty()) {
                val top = playerOneTurnGames.pop()
                val newList = doTurn2(top, top[0])
                playerTwoTurnGames.addAll(newList)
            }
            while (playerTwoTurnGames.isNotEmpty()) {
                val top = playerTwoTurnGames.pop()
                val newList = doTurn2(top, top[1])
                playerOneTurnGames.addAll(newList)
            }
        }
        val diceRolls = 0
        return diceRolls
    }

    fun doTurn2(game: Data, player: Player): List<Data> {
        val produced = ArrayList<Data>()
        for (dice in 1..3) {
            val firstPlayer: Player = game[0].copy().also {
                if (player == it) {
                    it.dice.add(dice)
                }
            }
            val secondPlayer: Player = game[1].copy().also {
                if (player == it) {
                    it.dice.add(dice)
                }
            }
            produced.add(Data(listOf(firstPlayer, secondPlayer)))
        }
        return produced
    }

}

