package com.advent2021.puzzle2

import com.advent2021.base.Base
import com.advent2021.puzzle2.Position.Companion.incDepth
import com.advent2021.puzzle2.Position.Companion.incDistance
import com.advent2021.puzzle2.Position2.Companion.incAim

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

    companion object {
        fun <Pos: Position> incDistance(x: Int, pos: Pos): Pos = pos.also { pos.distance += x }
        fun <Pos: Position> incDepth(x: Int, pos: Pos): Pos = pos.also { pos.depth += x }
    }
}

class Position2(
    depth: Int = 0,
    distance: Int = 0,
    var aim: Int = 0
) : Position(depth, distance) {
    companion object {
        fun incAim(x: Int, pos: Position2): Position2 = pos.also { pos.aim += x }
    }
}


class Puzzle2 : Base<Data, Solution, Solution2>() {
    override fun parseLine(line: String, data: Data) {
        val parts = line.split(" ")
        data.add(InputLine(Direction.valueOf(parts[0]), parts[1].toInt()))
    }

    override fun computeSolution(data: Data): Solution {
        return with(data.fold(Position()) { acc, line ->
            when (line.dir) {
                Direction.forward -> incDistance(line.x, acc)
                Direction.down -> incDepth(line.x, acc)
                Direction.up -> incDepth(-line.x, acc)
            }
        }) { solution }
    }

    override fun computeSolution2(data: Data): Solution2 {
        return with(data.fold(Position2()) { acc, line ->
            when (line.dir) {
                Direction.forward -> incDistance(line.x, acc).also { incDepth(acc.aim * line.x, it) }
                Direction.down -> incAim(line.x, acc)
                Direction.up -> incAim(-line.x, acc)
            }
        }) { solution }
    }
}

