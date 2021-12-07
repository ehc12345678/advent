package com.advent2021.puzzle7

import com.advent2021.base.Base
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

typealias Data = ArrayList<Int>
typealias Solution = Int
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
        data.addAll(line.split(",").map { it.toInt() })
    }

    override fun computeSolution(data: Data): Solution {
        data.sort()
        val median = data[data.size / 2]
        return data.sumOf { num -> Math.abs(num - median) }
    }

    override fun computeSolution2(data: Data): Solution2 {
        val ceil = ceil(data.average()).toInt()
        val floor = floor(data.average()).toInt()
        return min(sumDiffToNum(data, ceil), sumDiffToNum(data, floor))
    }

    // Gausses formula simplified because we know the low is zero
    private fun sumDiffToNum(data: Data, num: Int): Int {
        return data.sumOf { n ->
            val diff = Math.abs(n - num)
            (((diff + 1) / 2F) * diff).toInt()
        }
    }
}

