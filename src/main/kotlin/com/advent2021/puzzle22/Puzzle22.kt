package com.advent2021.puzzle22

import com.advent2021.base.Base
import com.advent2021.puzzle19.Point3D
import java.math.BigInteger

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
    fun volume() = points.size
}
data class Instruction(val onOff: Boolean, val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    fun getCube() = Cube(xRange, yRange, zRange)
    fun getCube3D() = Cube3D(xRange, yRange, zRange)
}

data class Cube3D(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    fun volume(): BigInteger {
        return BigInteger.valueOf(xRange.last.toLong() - xRange.first.toLong() + 1).
            multiply(BigInteger.valueOf(yRange.last.toLong() - yRange.first.toLong() + 1)).
            multiply(BigInteger.valueOf(zRange.last.toLong() - zRange.first.toLong() + 1))
    }
}

typealias CubeFunc = (cube: Cube3D, newRange: IntRange) -> Cube3D
typealias Data = ArrayList<Instruction>
typealias Solution = Int
typealias Solution2 = BigInteger

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
            workingCube = if (instruction.onOff) {
                workingCube.union(instructionCube)
            } else {
                workingCube.subtract(instructionCube)
            }
        }
        return workingCube.points.size
    }

    override fun computeSolution2(data: Data): Solution2 {
        var workingCubes: List<Cube3D> = emptyList()
        for (instruction in data) {
            val instructionCube = instruction.getCube3D()
            if (workingCubes.isEmpty()) {
                workingCubes = listOf(instructionCube)
            } else {
                workingCubes = if (instruction.onOff) {
                    workingCubes.map { union(it, instructionCube) }.flatten()
                } else {
                    workingCubes.map { subtract(it, instructionCube) }.flatten()
                }

            }
        }
        return workingCubes.fold(BigInteger.ZERO) { acc, cube3D -> acc + cube3D.volume() }
    }

    fun union(start: Cube3D, other: Cube3D) : List<Cube3D> {
        return when {
            !overlap(start, other) -> listOf(start, other)
            surroundsFully(start, other) -> listOf(start)
            surroundsFully(other, start) -> listOf(other)
            else -> {
                val workingSet = HashSet(splitX(start, other))
                workingSet.addAll(workingSet.map { splitY(start, it) }.flatten())
                workingSet.addAll(workingSet.map { splitZ(start, it) }.flatten())
                workingSet.toList()
            }
        }
    }

    private fun surroundsFully(
        bigger: Cube3D,
        smaller: Cube3D
    ) = surroundsFully(bigger.xRange, smaller.xRange) &&
        surroundsFully(bigger.yRange, smaller.yRange) &&
        surroundsFully(bigger.zRange, smaller.zRange)

    private fun surroundsFully(
        bigger: IntRange,
        smaller: IntRange
    ) = smaller.first in bigger && smaller.last in bigger

    fun overlap(
        cube1: Cube3D,
        cube2: Cube3D
    ) = overlap(cube1.xRange, cube2.xRange) &&
        overlap(cube1.yRange, cube2.yRange) &&
        overlap(cube1.zRange, cube2.zRange)

    fun overlap(
        range1: IntRange,
        range2: IntRange
    ): Boolean {
        return range1.first in range2 || range1.last in range2 || range2.first in range1 || range2.last in range1
    }

    fun HashSet<Cube3D>.addIfNotEmpty(xRange: IntRange, yRange: IntRange, zRange: IntRange) {
        if (!xRange.isEmpty() && !yRange.isEmpty() && !zRange.isEmpty()) {
            add(Cube3D(xRange, yRange, zRange))
        }
    }
    fun HashSet<Cube3D>.addIfNotEmpty(cube3D: Cube3D) = addIfNotEmpty(cube3D.xRange, cube3D.yRange, cube3D.zRange)

    fun splitX(
        start: Cube3D,
        other: Cube3D
    ): HashSet<Cube3D> {
        return split(start.xRange, other.xRange, start, other)
        { cube, newRange -> Cube3D(newRange, cube.yRange, cube.zRange) }
    }

    fun splitY(
        start: Cube3D,
        other: Cube3D
    ): HashSet<Cube3D> {
        return split(start.yRange, other.yRange, start, other)
        { cube, newRange -> Cube3D(cube.xRange, newRange, cube.zRange) }
    }

    fun splitZ(
        start: Cube3D,
        other: Cube3D
    ): HashSet<Cube3D> {
        return split(start.zRange, other.zRange, start, other)
        { cube, newRange -> Cube3D(cube.xRange, cube.yRange, newRange) }
    }

    private fun split(
        s: IntRange, o: IntRange, start: Cube3D, other: Cube3D, cubeFunc: CubeFunc
    ): HashSet<Cube3D> {
        val ret = HashSet<Cube3D>()
        if (overlap(s, o)) {
            // o.first   s.first  s.last    o.last
            // |---------|---------|---------|
            if (surroundsFully(bigger = o, smaller = s)) {
                // break the pieces into 3.
                ret.addIfNotEmpty(cubeFunc(other, o.first until s.first))
                ret.addIfNotEmpty(start.copy())
                ret.addIfNotEmpty(cubeFunc(other, s.last + 1 .. o.last))
            }
            // s.first   o.first  o.last    s.last
            // |---------|---------|---------|
            else if (surroundsFully(bigger = s, smaller = o)) {
                // break the pieces into 3.  The one in the middle is going to overlap, but will drop in snips later
                ret.addIfNotEmpty(cubeFunc(other, s.first until o.first))
                ret.addIfNotEmpty(other.copy())
                ret.addIfNotEmpty(cubeFunc(other, o.last + 1 .. s.last))
            }
            // o.first   s.first  o.last    s.last
            // |---------|---------|---------|
            else if (o.first < s.first) {
                ret.addIfNotEmpty(cubeFunc(other, o.first until s.first))
                ret.addIfNotEmpty(cubeFunc(other, s.first until o.last))
                ret.addIfNotEmpty(cubeFunc(other, o.last .. s.last))
            }
            // s.first  o.first    s.last    o.last
            // |---------|---------|---------|
            else {
                ret.addIfNotEmpty(cubeFunc(other, s.first until o.first))
                ret.addIfNotEmpty(cubeFunc(other, o.first until s.last))
                ret.addIfNotEmpty(cubeFunc(other, s.last .. o.last))
            }
        } else {
            ret.add(start)
            ret.add(other)
        }
        return ret
    }

    fun subtract(start: Cube3D, other: Cube3D) : List<Cube3D> {
        return when {
            !overlap(start, other) -> listOf(start)
            surroundsFully(other, start) -> listOf()

// TODO:
//            surroundsFully(start, other) -> listOf(start)
//            overlap(start.xRange, other.xRange) -> snipX(start, other)
//            overlap(start.yRange, other.yRange) -> snipY(start, other)
//            overlap(start.zRange, other.zRange) -> snipZ(start, other)
            else -> {
                // how did we get here
                emptyList()
            }
        }

    }
}

