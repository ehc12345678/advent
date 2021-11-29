package com.advent.advent2020.puzzle24

import java.io.File
import java.lang.RuntimeException

typealias Point = Pair<Int, Int>
typealias Data = HashSet<Point>

fun main() {
    val puzzle = Puzzle24()
    try {
        val data = puzzle.readInputs("inputs.txt")
        val answerA = puzzle.solutionA(data)
        println("Answer A is ${answerA.size}")

        val answerB = puzzle.solutionB(answerA)
        println("Answer B is $answerB")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle24 {
    fun readInputs(filename: String): List<String> {
        val file = File(filename)
        return file.readLines()
    }

    fun parseLine(line: String) : String {
        return line
    }

    fun solutionA(lines: List<String>) : Data {
        val allSeen = Data()
        lines.forEach { line ->
            val pt = traverse(line)
            if (allSeen.contains(pt)) {
                allSeen.remove(pt)
            } else {
                allSeen.add(pt)
            }
        }
        return allSeen
    }

    fun traverse(line: String) : Point {
        var index = 0

        var pair = Pair(0, 0)
        while (index < line.length) {
            when {
                line.startsWith("se", index) -> {
                    pair = Pair(pair.first + 1, pair.second + 2)
                    index += 2
                }
                line.startsWith("ne", index) -> {
                    pair = Pair(pair.first + 1, pair.second - 2)
                    index += 2
                }
                line.startsWith("sw", index) -> {
                    pair = Pair(pair.first - 1, pair.second + 2)
                    index += 2
                }
                line.startsWith("nw", index) -> {
                    pair = Pair(pair.first - 1, pair.second - 2)
                    index += 2
                }
                line.startsWith("e", index) -> {
                    pair = Pair(pair.first + 2, pair.second)
                    index += 1
                }
                line.startsWith("w", index) -> {
                    pair = Pair(pair.first - 2, pair.second)
                    index += 1
                }
                else -> throw RuntimeException("oops ${line.substring(index)}")
            }
        }
        return pair
    }

    
    fun solutionB(data: Data) : Int {
        var solve = data
        for (x in 0 until 100) {
            solve = flipOneDay(solve)
        }
        return solve.size
    }
    
    fun flipOneDay(data: Data) : Data {
        val ret = Data()
        data.forEach { pt ->
            if (!(numAdjacent(pt, data) == 0 || numAdjacent(pt, data) > 2)) {
                ret.add(pt)
            }
            adjacent(pt).forEach { adj ->
                if (!data.contains(adj) && numAdjacent(adj, data) == 2) {
                    ret.add(adj)
                }
            }
        }
        return ret
    }

    fun numAdjacent(pair: Point, data: Data) : Int {
        val num = adjacent(pair).intersect(data).size
        return num
    }

    fun adjacent(pair: Point) : Set<Point> {
        return setOf(
            Pair(pair.first + 1, pair.second + 2),
            Pair(pair.first + 1, pair.second - 2),
            Pair(pair.first - 1, pair.second + 2),
            Pair(pair.first - 1, pair.second - 2),
            Pair(pair.first + 2, pair.second),
            Pair(pair.first - 2, pair.second),
        )
    }
    
}