package com.advent2021.puzzle1

import com.advent2021.base.Base

typealias Data = ArrayList<Int>
data class Solution(
   var count: Int
)
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle1()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle1 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line.toInt())
    }

    override fun computeSolution(data: Data): Solution = computeWindow(data, 1)
    override fun computeSolution2(data: Data): Solution2 = computeWindow(data, 3)

    private fun computeWindow(data: Data, windowSize: Int) : Solution {
        var last = sumWindow(data, 0, windowSize)
        val solution = Solution(0)
        (1..data.size - windowSize).forEach { index ->
            val window = sumWindow(data, index, windowSize)
            if (window > last) {
                solution.count++
            }
            last = window
        }
        return solution
    }

    private fun sumWindow(data: Data, index: Int, windowSize: Int) : Int {
        return data.subList(index, index + windowSize).reduce { acc, i -> acc + i }
    }
}

