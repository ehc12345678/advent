package com.advent2022.puzzle3

import com.advent2021.base.Base

typealias Data = ArrayList<String>
typealias Solution = Int
typealias Solution2 = Solution

// https://adventofcode.com/2022/day/3
fun main() {
    try {
        val puz = Puzzle3()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle3 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line)
    }

    override fun computeSolution(data: Data): Solution {
        return data.sumOf { computeOne(it) }
    }

    private fun computeOne(items: String): Int {
        val string1 = items.substring(0, items.length / 2)
        val string2 = items.substring(items.length / 2)
        val set1 = string1.toSet()
        val set2 = string2.toSet()
        val common = set1.intersect(set2)
        return common.sumOf { charWorth(it) }
    }

    private fun charWorth(ch: Char): Int {
        val diff: Int =
            if (ch in 'a'..'z') {
                (ch - 'a' + 1)
            } else {
                (ch - 'A' + 27)
            }
        return diff
    }

    override fun computeSolution2(data: Data): Solution2 {
        val groups = data.chunked(3)
        return groups.sumOf { scoreGroup(it) }
    }

    private fun scoreGroup(group: List<String>): Int {
        val common = group.map { it.toSet() }.reduce { acc, g -> acc.intersect(g) }
        return common.sumOf { charWorth(it) }
    }
}

