package com.advent2022.puzzle1

import com.advent2021.base.Base

typealias Data = ArrayList<ArrayList<Int>>
typealias Solution = Int
typealias Solution2 = Solution

// https://adventofcode.com/2022/day/1
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
        if (line.isBlank() || data.isEmpty()) {
            data.add(ArrayList())
        }

        if (line.isNotBlank()) {
            val thisElf = data.last()
            thisElf.add(line.toInt())
        }
    }

    override fun computeSolution(data: Data): Solution {
        return data.maxOf { it.sum() }
    }
    override fun computeSolution2(data: Data): Solution2 {
        return data.map { it.sum() }.sortedDescending().subList(0, 3).sum()
    }
}

