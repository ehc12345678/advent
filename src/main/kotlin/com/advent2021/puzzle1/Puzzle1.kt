package com.advent.advent2021.puzzle1

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

    override fun computeSolution(data: Data): Solution {
        var last = data[0]
        val solution = Solution(0)
        data.subList(1, data.size).forEach {
            if (it > last) {
                solution.count++
            }
            last = it
        }
        return solution
    }

    override fun computeSolution2(data: Data): Solution2 {
        var last = sumWindow(data, 0)
        val solution = Solution2(0)
        (1..data.size - 3).forEach { index ->
            val window = sumWindow(data, index)
            if (window > last) {
                solution.count++
            }
            last = window
        }
        return solution
    }

    private fun sumWindow(data: Data, index: Int) : Int {
        return data.subList(index, index + 3).reduce { acc, i -> acc + i }
    }
}

