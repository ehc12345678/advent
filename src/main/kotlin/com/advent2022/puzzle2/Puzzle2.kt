package com.advent2022.puzzle2

import com.advent2021.base.Base

enum class Rps {
    ROCK,
    PAPER,
    SCISSORS
}

typealias Turn = Pair<Rps, Rps>
typealias Data = ArrayList<Turn>
typealias Solution = Int
typealias Solution2 = Solution

// https://adventofcode.com/2022/day/2
fun main() {
    try {
        val puz = Puzzle2()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle2 : Base<Data, Solution?, Solution2?>() {
    val winsAgainst = mapOf(
        Rps.ROCK to Rps.SCISSORS,
        Rps.SCISSORS to Rps.PAPER,
        Rps.PAPER to Rps.ROCK
    )
    val losesAgainst = mapOf(
        Rps.ROCK to Rps.PAPER,
        Rps.SCISSORS to Rps.ROCK,
        Rps.PAPER to Rps.SCISSORS
    )

    override fun parseLine(line: String, data: Data) {
        val pair = line.split(" ")
        data.add(Pair(letterToRps(pair[0]), letterToRps(pair[1])))
    }

    override fun computeSolution(data: Data): Solution {
        return data.sumOf { scoreOneTurn(it) }
    }

    override fun computeSolution2(data: Data): Solution2 {
        return data.sumOf { scorePartTwo(it) }
    }

    private fun letterToRps(letter: String): Rps {
        return when (letter) {
            "A", "X" -> Rps.ROCK
            "B", "Y" -> Rps.PAPER
            "C", "Z" -> Rps.SCISSORS
            else -> throw IllegalArgumentException(letter)
        }
    }

    private fun scoreOneTurn(turn: Turn): Int  {
        val shape = shapeValue(turn.second)
        return shape + outcome(turn)
    }

    private fun shapeValue(rps: Rps?) = when (rps!!) {
        Rps.ROCK -> 1
        Rps.PAPER -> 2
        Rps.SCISSORS -> 3
    }

    private fun scorePartTwo(turn: Turn): Int {
        val shapeToGetOutcome =
            when (turn.second) {
                Rps.ROCK -> winsAgainst[turn.first]!!
                Rps.PAPER -> turn.first
                Rps.SCISSORS -> losesAgainst[turn.first]!! // win
            }
        return scoreOneTurn(Turn(turn.first, shapeToGetOutcome))
    }

    private fun outcome(turn: Turn): Int {
        return when {
            turn.first == turn.second -> 3
            winsAgainst[turn.second] == turn.first -> 6
            else -> 0
        }
    }
}

