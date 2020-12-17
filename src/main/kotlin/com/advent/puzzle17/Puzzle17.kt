package com.advent.puzzle17

import java.io.File

data class Point(val x: Int, val y: Int, val z: Int)
data class Point4D(val x: Int, val y: Int, val z: Int, val w: Int)

infix fun Point.add(other: Point)  = Point(x + other.x, y + other.y, z + other.z)
infix fun Point4D.add(other: Point4D)  = Point4D(x + other.x, y + other.y, z + other.z, w + other.z)

typealias Cubes = HashSet<Point>
typealias Cubes4D = HashSet<Point4D>

fun main() {
    val puzzle = Puzzle17()
    try {
        val data = puzzle.readInputs("inputs.txt")
        val answer = puzzle.partA(data)
        println("Answer is $answer")

        val dataB = puzzle.readInputsB("inputs.txt")
        val answer2 = puzzle.partB(dataB)
        println("Answer is $answer2")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle17 {
    private val neighbors: Set<Point>
    private val neighbors4D: Set<Point4D>
    init {
        neighbors = calcNeighbors()
        neighbors4D = calcNeighbors4D()
    }

    fun readInputs(filename: String): Cubes {
        val file = File(filename)
        val lines = file.readLines()
        val ret = Cubes()
        lines.forEachIndexed { yIndex, line ->
            line.forEachIndexed { xIndex, ch ->
                if (ch == '#') {
                    ret.add(Point(xIndex, yIndex, 0))
                }
            }
        }
        return ret
    }

    fun readInputsB(filename: String): Cubes4D {
        val file = File(filename)
        val lines = file.readLines()
        val ret = Cubes4D()
        lines.forEachIndexed { yIndex, line ->
            line.forEachIndexed { xIndex, ch ->
                if (ch == '#') {
                    ret.add(Point4D(xIndex, yIndex, 0, 0))
                }
            }
        }
        return ret
    }

    fun partA(data: Cubes): Int {
        var newState = data
        for (i in 0 until 6) {
            newState = doOneTurn(newState)
        }
        return newState.size
    }

    private fun calcNeighbors() : Set<Point> {
        val ret = HashSet<Point>()
        for (x in -1..1) {
            for (y in -1..1) {
                for (z in -1..1) {
                    if (!(x == 0 && y == 0 && z == 0)) {
                        ret.add(Point(x, y, z))
                    }
                }
            }
        }
        return ret
    }

    private fun addNeighbors(dimStart: Int, dims: Int, point: PointND, points: HashSet<PointND>) {
        if (dimStart == dims) {
            if (point != PointND(dims)) {
                points.add(point)
            }
        } else {
            for (num in -1..1) {
                val newPoint = point add PointND(dims)
                newPoint.coords[dimStart] = num
                addNeighbors(dimStart + 1, dims, newPoint, points)
            }
        }
    }

    private fun doOneTurn(data: Cubes) : Cubes {
        val ret = Cubes()
        data.forEach {
            // active
            if (countNeighbors(it, data) in 2..3) {
                ret.add(it)
            }

            // inactive
            val allMyNeighbors = getAllNeighbors(it)
            allMyNeighbors.forEach { neighbor ->
                if (!data.contains(neighbor) && countNeighbors(neighbor, data) == 3) {
                    ret.add(neighbor)
                }
            }
        }

        return ret
    }

    private fun getAllNeighbors(point: Point): List<Point> {
        return neighbors.map { it add point }
    }

    private fun countNeighbors(point: PointND, data: CubesND): Int {
        val allNeighbors = getAllNeighbors(point)
        val theCount = allNeighbors.count { data.contains(it) }
        return theCount
    }
}