package com.advent2022.puzzle4

import com.advent2021.base.Base

typealias Data = ArrayList<Pair<IntRange, IntRange>>
typealias Solution = Int
typealias Solution2 = Solution

// https://adventofcode.com/2022/day/4
fun main() {
    try {
        val puz = Puzzle4()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

fun String.toRange(): IntRange {
    val parts = this.split("-")
    return IntRange(parts[0].toInt(), parts[1].toInt())
}

class Puzzle4 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val parts = line.split(",")
        val rangePair = Pair(parts[0].toRange(), parts[1].toRange())
        data.add(rangePair)
    }

    override fun computeSolution(data: Data): Solution {
        return data.sumOf { computeOne(it) }
    }

    private fun computeOne(pair: Pair<IntRange, IntRange>): Int {
        return if (overlapCompletely(pair)) {
            1
        } else {
            0
        }
    }

    private fun overlapCompletely(pair: Pair<IntRange, IntRange>): Boolean {
        val firstStart = pair.first.first
        val firstEnd = pair.first.last
        val secondStart = pair.second.first
        val secondEnd = pair.second.last
        return ((firstStart <= secondStart && firstEnd >= secondEnd) ||
            secondStart <= firstStart && secondEnd >= firstEnd)
    }

    override fun computeSolution2(data: Data): Solution2 {
        return data.sumOf { computeSecond(it) }
    }

    private fun computeSecond(pair: Pair<IntRange, IntRange>): Int {
        return if (overlapCompletely(pair) || overlapPartial(pair)) {
            1
        } else {
            0
        }
    }

    private fun overlapPartial(pair: Pair<IntRange, IntRange>): Boolean {
        val firstRange = pair.first
        val secondRange = pair.second
        return firstRange.contains(secondRange.first) || firstRange.contains(secondRange.last)
                || secondRange.contains(firstRange.first) || secondRange.contains(firstRange.last)
    }
}

