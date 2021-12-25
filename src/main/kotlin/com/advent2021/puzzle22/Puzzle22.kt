package com.advent2021.puzzle22

import com.advent2021.base.Base
import com.advent2021.puzzle19.Point3D
import java.math.BigInteger
import kotlin.math.max
import kotlin.math.min

data class Cube(val points: Set<Point3D> = LinkedHashSet()) {
    constructor(xRange: IntRange, yRange: IntRange, zRange: IntRange)
        : this(LinkedHashSet<Point3D>().also {
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

    fun contains(pt: Point3D) = pt.x in xRange && pt.y in yRange && pt.z in zRange
    fun isEmpty() = xRange.isEmpty() || yRange.isEmpty() || zRange.isEmpty()
}

typealias Data = ArrayList<Instruction>
typealias Solution = Int
typealias Solution2 = BigInteger
typealias CubeSet = LinkedHashSet<Cube3D>

fun main() {
    try {
        val puz = Puzzle22()
        val solution1 = puz.solvePuzzle("inputsTest.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputsTest.txt", Data())
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
            workingCubes = if (instruction.onOff) {
                union(workingCubes, instructionCube)
            } else {
                subtract(workingCubes, instructionCube)
            }
        }
        return workingCubes.fold(BigInteger.ZERO) { acc, cube3D -> acc + cube3D.volume() }
    }

    fun union(start: Cube3D, other: Cube3D) : List<Cube3D> {
        return union(listOf(start), other)
    }

    fun union(cubes: List<Cube3D>, other: Cube3D) : List<Cube3D> {
        val ret = CubeSet()
        ret.addIfNotEmpty(other)

        ret.addAll(cubes.map {
            val common = findCommon(it, other)
            subtract(it, common) // cubex + cubey is the sum of the cubes - common
        }.flatten())

        return ret.toList()
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

    fun CubeSet.addIfNotEmpty(xRange: IntRange, yRange: IntRange, zRange: IntRange): CubeSet {
        if (!xRange.isEmpty() && !yRange.isEmpty() && !zRange.isEmpty()) {
            add(Cube3D(xRange, yRange, zRange))
        }
        return this
    }
    fun CubeSet.addIfNotEmpty(cube3D: Cube3D) = addIfNotEmpty(cube3D.xRange, cube3D.yRange, cube3D.zRange)

    enum class Face { TOP, BOTTOM, LEFT, RIGHT, FRONT, BACK }

    fun subtract(cubes: List<Cube3D>, other: Cube3D): List<Cube3D> {
        return cubes.map { subtract(it, other) }.flatten()
    }

    fun subtract(start: Cube3D, other: Cube3D) : List<Cube3D> {
        val faces = findIntersectingFaces(start, other)
        return when {
            other.isEmpty() -> listOf(start)
            !overlap(start, other) -> listOf(start)
            surroundsFully(other, start) -> listOf()
            surroundsFully(start, other) -> subtractSurrounded(start, other)

            faces.size == 1 || (faces.size == 2 && getOpposite(faces[0]) == faces[1]) ->
                subtractOneOrOppositeFaces(start, other, faces[0])

            faces.size == 2 -> subtractNonOppositeFaces(start, other, faces[0], faces[1])

            faces.size == 3 -> {
                val oppositeFaces = faces.map { getOpposite(it) }.toSet()
                val intersect = faces.toSet().intersect(oppositeFaces)
                if (intersect.isNotEmpty()) {
                    subtractOneOrOppositeFaces(start, other, intersect.first())
                }
                else {
                    subtractThreeFaceChunk(start, other, faces)
                }
            }
            faces.size == 4 -> {
                val oppositeFaces = faces.map { getOpposite(it) }.toSet()
                val nonOppositeFaces = faces.toSet() - oppositeFaces.toSet()
                if (nonOppositeFaces.isEmpty()) {
                    sliceIntoTwoCubes(start, other, faces[0])
                } else {
                    val findOppositeFace = faces.find { oppositeFaces.contains(it) }!!
                    subtractThreeFaceChunk(start, other, faces - findOppositeFace)
                }
            }

            faces.size == 5 -> {
                sliceOddFace(start, other, getFirstMissingFace(faces)!!)
            }
            else -> {
                emptyList()
            }
        }
    }

    private fun getFirstMissingFace(faces: List<Face>): Face? {
        var oddFace: Face? = null
        for (face in Face.values()) {
            if (!faces.contains(face)) {
                oddFace = face
                break
            }
        }
        return oddFace
    }

    private fun getOpposite(face1: Face): Face {
        return when (face1) {
            Face.TOP -> Face.BOTTOM
            Face.BOTTOM -> Face.TOP
            Face.LEFT -> Face.RIGHT
            Face.RIGHT -> Face.LEFT
            Face.FRONT -> Face.BACK
            Face.BACK -> Face.FRONT
        }
    }

    private fun findIntersectingFaces(start: Cube3D, other: Cube3D): List<Face> {
        val ret = ArrayList<Face>()
        if (start.xRange.first in other.xRange) { ret.add(Face.LEFT) }
        if (start.xRange.last in other.xRange) { ret.add(Face.RIGHT) }
        if (start.yRange.first in other.yRange) { ret.add(Face.TOP) }
        if (start.yRange.last in other.yRange) { ret.add(Face.BOTTOM) }
        if (start.zRange.first in other.zRange) { ret.add(Face.FRONT) }
        if (start.zRange.last in other.zRange) { ret.add(Face.BACK) }
        return ret
    }

    fun findCommon(range: IntRange, otherRange: IntRange): IntRange {
        return if (overlap(range, otherRange)) {
            max(range.first, min(range.last, otherRange.first))..min(range.last, max(range.first, otherRange.last))
        } else {
            0 until 0
        }
    }

    fun findCommon(cube: Cube3D, other: Cube3D): Cube3D {
        val xRange = findCommon(cube.xRange, other.xRange)
        val yRange = findCommon(cube.yRange, other.yRange)
        val zRange = findCommon(cube.zRange, other.zRange)
        return Cube3D(xRange, yRange, zRange)
    }

    fun subtractSurrounded(start: Cube3D, other: Cube3D): List<Cube3D> {
        val left = Cube3D(start.xRange.first until other.xRange.first, start.yRange, start.zRange)
        val right = Cube3D(other.xRange.last + 1..start.xRange.last, start.yRange, start.zRange)
        val top = Cube3D(
            other.xRange.first until other.xRange.last + 1,
            start.yRange.first until other.yRange.first,
            start.zRange
        )
        val bottom =Cube3D(
            other.xRange.first until other.xRange.last + 1,
            other.yRange.last + 1..start.yRange.last,
            start.zRange
        )
        val front = Cube3D(
            other.xRange.first until other.xRange.last + 1,
            other.yRange.first until other.yRange.last + 1,
            start.zRange.first until other.zRange.first,
        )
        val back = Cube3D(
            other.xRange.first until other.xRange.last + 1,
            other.yRange.first until other.yRange.last + 1,
            other.zRange.last + 1..start.zRange.last,
        )
        return CubeSet()
            .addIfNotEmpty(left)
            .addIfNotEmpty(right)
            .addIfNotEmpty(top)
            .addIfNotEmpty(bottom)
            .addIfNotEmpty(front)
            .addIfNotEmpty(back)
            .toList()
    }

    fun subtractOneOrOppositeFaces(start: Cube3D, other: Cube3D, face: Face): List<Cube3D> {
        val ret = CubeSet()
        val fullLeftFace = Cube3D(
            start.xRange.first until other.xRange.first,
            start.yRange,
            start.zRange)
        val rullRightFace = Cube3D(
            other.xRange.last + 1..start.xRange.last,
            start.yRange,
            start.zRange)
        val fullBottomFace = Cube3D(
            start.xRange,
            other.yRange.last + 1..start.yRange.last,
            start.zRange)

        when (face) {
            Face.TOP, Face.BOTTOM -> {
                ret
                    .addIfNotEmpty(fullLeftFace) // left
                    .addIfNotEmpty(rullRightFace) // right
                    .addIfNotEmpty(
                        Cube3D(
                            other.xRange,
                            start.yRange,
                            start.zRange.first until other.zRange.first)) // front
                    .addIfNotEmpty(
                        Cube3D(
                            other.xRange,
                            start.yRange,
                            other.zRange.last + 1 .. start.zRange.last)) // back
            }
            Face.FRONT, Face.BACK -> {
                ret
                    .addIfNotEmpty(fullLeftFace) // left
                    .addIfNotEmpty(rullRightFace) // right
                    .addIfNotEmpty(
                        Cube3D(
                            other.xRange.first .. other.xRange.last + 1,
                            start.yRange.first until other.yRange.first,
                            start.zRange)) // top
                    .addIfNotEmpty(
                        Cube3D(
                            other.xRange.last + 1 .. start.xRange.last,
                            other.yRange.last .. start.yRange.last,
                            start.zRange)) // bottom
            }
            Face.LEFT, Face.RIGHT -> {
                val fullTopFace = Cube3D(
                    start.xRange,
                    start.yRange.first until other.yRange.first,
                    start.zRange
                )
                ret
                    .addIfNotEmpty(fullTopFace) // top
                    .addIfNotEmpty(fullBottomFace) // bottom
                    .addIfNotEmpty(
                        Cube3D(
                            start.xRange,
                            other.yRange,
                            start.zRange.first until other.zRange.first)) // front
                    .addIfNotEmpty(
                        Cube3D(
                            start.xRange,
                            other.yRange,
                            other.zRange.last + 1 .. start.zRange.last)) // back
            }
        }
        when (face) {
            Face.TOP -> {
                // add bottom
                ret.addIfNotEmpty(
                    Cube3D(
                        other.xRange,
                        other.yRange.last + 1 .. start.yRange.last,
                        other.zRange))
            }
            Face.BOTTOM -> {
                ret.addIfNotEmpty(
                    Cube3D(
                        other.xRange,
                        start.yRange.first until other.yRange.first,
                        other.zRange))
            }
            Face.LEFT -> {
                ret.addIfNotEmpty(
                    Cube3D(
                        other.xRange.last + 1 .. start.xRange.last,
                        other.yRange,
                        other.zRange))
            }
            Face.RIGHT -> {
                ret.addIfNotEmpty(
                    Cube3D(
                        start.xRange.first until other.xRange.first,
                        other.yRange,
                        other.zRange))
            }
            Face.FRONT -> {
                ret.addIfNotEmpty(
                    Cube3D(
                        other.xRange,
                        other.yRange,
                        other.zRange.last + 1 .. start.zRange.last)) // back
            }
            Face.BACK -> {
                ret.addIfNotEmpty(
                    Cube3D(
                        other.xRange,
                        other.yRange,
                        start.zRange.first until other.zRange.first))
            }
        }
        return ret.toList()
    }

    fun sliceOddFace(start: Cube3D, other: Cube3D, face: Face): List<Cube3D> {
        val slice = when (face) {
            Face.TOP -> Cube3D(start.xRange, start.yRange.start until other.yRange.start, start.zRange)
            Face.BOTTOM -> Cube3D(start.xRange, other.yRange.last + 1 .. start.yRange.last, start.zRange)
            Face.LEFT -> Cube3D(start.xRange.start until other.xRange.start, start.yRange, start.zRange)
            Face.RIGHT -> Cube3D(other.xRange.last + 1 .. start.xRange.last, start.yRange, start.zRange)
            Face.FRONT -> Cube3D(start.xRange, start.yRange, start.zRange.start until other.zRange.first)
            Face.BACK -> Cube3D(start.xRange, start.yRange, other.zRange.last + 1 .. start.zRange.last)
        }
        return listOf(slice)
    }

    fun subtractNonOppositeFaces(start: Cube3D, other: Cube3D, face1: Face, face2: Face): List<Cube3D> {
        val ret = CubeSet()
        // todo
        return ret.toList()
    }

    fun sliceIntoTwoCubes(start: Cube3D, other: Cube3D, oneFace: Face): List<Cube3D> {
        return subtractOneOrOppositeFaces(start, other, oneFace)
    }

    fun subtractThreeFaceChunk(start: Cube3D, other: Cube3D, faces: List<Face>): List<Cube3D> {
        val ret = CubeSet()
        val common = findCommon(start, other)

        // put the top or bottom on first
        if (faces.contains(Face.BOTTOM)) {
            ret.addIfNotEmpty(
                Cube3D(
                    start.xRange,
                    start.yRange.first until common.yRange.first,
                    start.zRange)) // top
        } else {
            ret.addIfNotEmpty(
                Cube3D(
                    start.xRange,
                    common.yRange.last + 1 .. start.yRange.last,
                    start.zRange)) // bottom
        }


        // put the left or right on first
        if (faces.contains(Face.RIGHT)) {
            ret.addIfNotEmpty(
                Cube3D(
                    start.xRange.first until common.xRange.first,
                    common.yRange,
                    start.zRange)) // left
        } else {
            ret.addIfNotEmpty(
                Cube3D(
                    common.xRange.last + 1 .. start.xRange.last,
                    common.yRange,
                    start.zRange)) // right
        }

        // one more block to fill in the first
        if (faces.contains(Face.BACK)) {
            ret.addIfNotEmpty(
                Cube3D(
                    common.xRange,
                    common.yRange,
                    start.zRange.start until common.zRange.start))   // front
        } else {
            ret.addIfNotEmpty(
                Cube3D(
                    common.xRange,
                    common.yRange,
                    common.zRange.last + 1 .. start.zRange.last))   // end
        }
        return ret.toList()
    }
}

