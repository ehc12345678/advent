package com.advent.puzzle6

import java.io.File

typealias Answers = HashSet<Char>
typealias Grouping = ArrayList<Answers>

fun main() {
    val puzzle = Puzzle6()

    try {
        val inputs = puzzle.readInputs("inputs.txt")
        val part1 = inputs.map { puzzle.reducePart1(it) }
        val count1 = puzzle.countEm(part1)
        println("Part1 = $count1")

        val part2 = inputs.map { puzzle.reducePart2(it) }
        val count2 = puzzle.countEm(part2)
        println("Part 2 = $count2")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}


class Puzzle6 {
    fun readInputs(filename: String): List<Grouping> {
        val file = File(filename)
        val ret : MutableList<Grouping> = ArrayList()
        ret.add(Grouping())
        file.readLines().forEach { parseLine(it, ret) }
        return ret
    }

    private fun parseLine(line: String, ret: MutableList<Grouping>) : List<Grouping> {
        if (line.isEmpty()) {
            ret.add(Grouping())
        } else {
            val current : Grouping = ret.last()
            val answers = line.asSequence().toHashSet()
            current.add(answers)
        }
        return ret
    }

    fun reducePart1(grouping: Grouping) : Answers {
        return grouping.reduce { acc, it -> HashSet(acc.union(it)) }
    }

    fun reducePart2(grouping: Grouping) : Answers {
        return grouping.reduce { acc, it -> HashSet(acc.intersect(it)) }
    }
    
    fun countEm(inputs: List<Answers>): Int {
        var countAll = 0
        inputs.forEach {
            countAll += it.size
        }
        return countAll
    }
}