package com.advent2021.puzzle2

import com.advent2021.base.Base

enum class Direction { forward, up, down }
data class InputLine(
    val dir: Direction,
    val x: Int
)
typealias Data = ArrayList<InputLine>
typealias Solution = Int
typealias Solution2 = Int

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

open class Position(
    var depth: Int = 0,
    var distance: Int = 0
) {
    val solution: Int
        get() { return depth * distance }
    open fun incDistance(x: Int): Position = Position(depth, distance + x)
    open fun incDepth(x: Int): Position = Position(depth + x, distance)
}

class Position2(
    depth: Int = 0,
    distance: Int = 0,
    var aim: Int = 0
) : Position(depth, distance) {
    override fun incDistance(x: Int): Position2 = Position2(depth, distance + x, aim)
    override fun incDepth(x: Int): Position2 = Position2(depth + x, distance, aim)
    fun incAim(x: Int): Position2 = Position2(depth, distance, aim + x)
}


class Puzzle2 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val parts = line.split(" ")
        data.add(InputLine(Direction.valueOf(parts[0]), parts[1].toInt()))
    }

    override fun computeSolution(data: Data): Solution {
        return with(data.fold(Position()) { acc, line ->
            when (line.dir) {
                Direction.forward -> acc.incDistance(line.x)
                Direction.down -> acc.incDepth(line.x)
                Direction.up -> acc.incDepth(-line.x)
            }
        }) { solution }
    }

    override fun computeSolution2(data: Data): Solution2 {
        return with(data.fold(Position2()) { acc, line ->
            when (line.dir) {
                Direction.forward -> acc.incDistance(line.x).incDepth(acc.aim * line.x)
                Direction.down -> acc.incAim(line.x)
                Direction.up -> acc.incAim(-line.x)
            }
        }) { solution }
    }
}

