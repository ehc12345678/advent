package com.advent2022.puzzle20

import com.advent2021.base.Base

typealias Data = ArrayList<Int>
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle20()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle20 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line.toInt())
    }

    override fun computeSolution(data: Data): Solution {
        val ret = Data(data)
        data.forEach { num ->
            if (num != 0) {
                val index = ret.indexOf(num)
                var destIndex = (index + num) % ret.size
                if (destIndex <= 0) {
                    destIndex += ret.size - 1
                }
                ret.removeAt(index)
                if (index > destIndex) {
                    destIndex++
                }
                ret.add(destIndex, num)
            }
        }

        val zeroIndex = ret.indexOf(0)
        return listOf(1000, 2000, 3000).sumOf { ret[(zeroIndex + it) % ret.size] }
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

