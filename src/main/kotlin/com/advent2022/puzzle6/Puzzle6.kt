package com.advent2022.puzzle6

import com.advent2021.base.Base

typealias Data = ArrayList<String>
typealias Solution = Int
typealias Solution2 = Solution

// https://adventofcode.com/2022/day/3
fun main() {
    try {
        val puz = Puzzle6()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle6 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line)
    }

    override fun computeSolution(data: Data): Solution {
        return findMarker(data, 4)
    }

    private fun findMarker(data: Data, markerLen: Int): Int {
        var pos = 0
        val line = data[0]
        while (line.substring(pos, pos + markerLen).toSet().size < markerLen) {
            ++pos
        }
        return pos + markerLen
    }

    override fun computeSolution2(data: Data): Solution2 {
        return findMarker(data, 14)
    }
}

