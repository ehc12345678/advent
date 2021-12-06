package com.advent2021.puzzle7

import com.advent2021.base.Base

typealias Data = ArrayList<Int>
data class Solution(
    var count: Int
)
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle7()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle7 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line.toInt())
    }

    override fun computeSolution(data: Data): Solution {
        return Solution(0)
    }
    override fun computeSolution2(data: Data): Solution2 {
        return Solution2(0)
    }
}

