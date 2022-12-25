package com.advent2022.puzzle25

import com.advent2021.base.Base

typealias Data = ArrayList<Long>
typealias Solution = String
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle25()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

fun String.toFivePlaces(): Long {
    var answer = 0L
    var fivePlace = 1L
    for (place in 1..length) {
        val ch = this[length - place]
        answer += ch.fromSnafuChar() * fivePlace
        fivePlace *= 5
    }
    return answer
}

fun Char.fromSnafuChar(): Int {
    return when (this) {
        '2' -> 2
        '1' -> 1
        '0' -> 0
        '-' -> -1
        '=' -> -2
        else -> throw IllegalArgumentException("oops $this")
    }
}

fun Long.toSnafuChar(): Char {
    return when (this) {
        0L -> '0'
        1L -> '1'
        2L -> '2'
        3L -> '='
        4L -> '-'
        else -> throw IllegalArgumentException("oops $this")
    }
}

class Puzzle25 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line.toFivePlaces())
    }

    override fun computeSolution(data: Data): Solution {
        return toSnafu(data.sum())
    }
    override fun computeSolution2(data: Data): Solution2 {
        return ""
    }

    fun toSnafu(num: Long): String {
        var answer = ""
        var working = num
        while (working > 0) {
            val remainder = working % 5
            val snafuChar = remainder.toSnafuChar()

            answer = snafuChar + answer

            working -= snafuChar.fromSnafuChar()
            working /= 5L
        }
        return answer
    }
}

