package com.advent2021.puzzle22

import com.advent2021.base.Base
import com.advent2021.puzzle19.Point3D

data class Cube(val points: Set<Point3D> = HashSet()) {
    constructor(xRange: IntRange, yRange: IntRange, zRange: IntRange)
        : this(HashSet<Point3D>().also {
            for (x in xRange) {
                for (y in yRange) {
                    for (z in zRange) {
                        it.add(Point3D(x, y, z))
                    }
                }
            }
        })

    fun union(other: Cube): Cube { return Cube(points.union(other.points).toMutableSet()) }
    fun subtract(other: Cube): Cube { return Cube(points - other.points)}
}
data class Instruction(val onOff: Boolean, val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    fun getCube() = Cube(xRange, yRange, zRange)
}

typealias Data = ArrayList<Instruction>
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle22()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle22 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val parts = line.split(" ")
        val onOff = parts[0] == "on"

        val ranges = parts[1].split(",")
        val xRange = parseRange(ranges[0])
        val yRange = parseRange(ranges[1])
        val zRange = parseRange(ranges[2])
        data.add(Instruction(onOff, xRange, yRange, zRange))
    }

    private fun parseRange(rangeStr: String): IntRange {
        val range = rangeStr.substringAfter("=")
        val minMax = range.split("..").map { it.toInt() }
        return IntRange(minMax.minOfOrNull { it }!!, minMax.maxOfOrNull { it }!!)
    }

    override fun computeSolution(data: Data): Solution {
        var workingCube = Cube(emptySet())

        val inBounds = data.filter { it.xRange.first >= -50 && it.xRange.last <= 50 }
        for (instruction in inBounds) {
            val instructionCube = instruction.getCube()
            if (instruction.onOff) {
                workingCube = workingCube.union(instructionCube)
            } else {
                workingCube = workingCube.subtract(instructionCube)
            }
        }
        return workingCube.points.size
    }
    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

