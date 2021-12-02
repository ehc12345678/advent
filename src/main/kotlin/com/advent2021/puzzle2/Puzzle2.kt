package com.advent2021.puzzle2

import com.advent2021.base.Base
import java.lang.IllegalArgumentException

enum class Direction { forward, up, down }
data class InputLine(
    val dir: Direction,
    val x: Int
)
typealias Data = ArrayList<InputLine>
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle2()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle2 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val parts = line.split(" ")
        data.add(InputLine(Direction.valueOf(parts[0]), parts[1].toInt()))
    }

    override fun computeSolution(data: Data): Solution {
        data class Position(
            var depth: Int = 0,
            var distance: Int = 0
        )
        val pos = Position()
        data.forEach { line ->
            when (line.dir) {
                Direction.forward -> pos.distance += line.x
                Direction.down -> pos.depth += line.x
                Direction.up -> pos.depth -= line.x
            }
        }
        return pos.depth * pos.distance
    }

    override fun computeSolution2(data: Data): Solution2 {
        data class Position(
            var depth: Int = 0,
            var distance: Int = 0,
            var aim: Int = 0
        )
        val pos = Position()
        data.forEach { line ->
            when (line.dir) {
                Direction.forward -> {
                    pos.distance += line.x
                    pos.depth += (pos.aim * line.x)
                }
                Direction.down -> pos.aim += line.x
                Direction.up -> pos.aim -= line.x
            }
        }
        return pos.depth * pos.distance
    }
}

