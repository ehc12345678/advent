package com.advent.advent2021.puzzle1

import com.advent2021.base.Base

typealias Data = HashSet<Int>
typealias Solution = Pair<Int, Int>
typealias Solution2 = HashSet<Int>

val sum = 2020
fun main() {
    val puz = Puzzle1()
    val solution1 = puz.solvePuzzle("inputs.txt", Data())
    if (solution1 != null) {
        // todo
    }
    val solution2 = puz.solvePuzzle2("inputs.txt", Data())
    if (solution2 != null) {
        // todo
    }
}

class Puzzle1 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        // data.add(line.toInt())
        // todo
    }

    override fun computeSolution(data: Data): Solution? {
        // todo
        return null
    }

    override fun computeSolution2(data: Data): Solution2? {
        // todo
        return null
    }
}

