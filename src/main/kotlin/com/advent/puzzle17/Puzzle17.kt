package com.advent.puzzle17

import java.io.File

data class Point(val x: Int, val y: Int, val z: Int)
data class PointND(val dims: Int) {
    val coords = Array(dims) { 0 }
}
infix fun Point.add(other: Point) : Point = Point(x + other.x, y + other.y, z + other.z)
infix fun PointND.add(other: PointND) : PointND {
    val ret = PointND(other.dims)
    for (index in coords.indices) {
        ret.coords[index] = coords[index] + other.coords[index]
    }
    return ret
}
typealias Cubes = HashSet<Point>
typealias CubesND = HashSet<Point>

fun main() {
    val puzzle = Puzzle17()
    try {
        val data = puzzle.readInputs("inputs.txt")
        val answer = puzzle.partA(data)
        println("Answer is $answer")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle17 {
    private val neighbors: Set<Point>
    init {
        neighbors = calcNeighbors()
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

    fun partA(data: java.util.HashSet<Point>): Int {
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

    private fun countNeighbors(point: Point, data: Cubes): Int {
        return getAllNeighbors(point).fold(0) { acc, it -> acc + if (data.contains(it)) 1 else 0 }
    }
}