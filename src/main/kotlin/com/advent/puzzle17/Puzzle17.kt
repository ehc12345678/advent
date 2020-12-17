package com.advent.puzzle17

import java.io.File

data class PointND(val dims: Int, val coords: ArrayList<Int>) {
    constructor(dims: Int) : this(dims, ArrayList()) {
        for (i in 0 until dims) {
            coords.add(0)
        }
    }
    constructor(dims: Int, x: Int, y: Int) : this(dims) {
        coords[0] = x
        coords[1] = y
    }

    override fun toString(): String {
        return coords.toString()
    }
}

infix fun PointND.add(other: PointND) : PointND {
    val ret = PointND(other.dims)
    for (index in coords.indices) {
        ret.coords[index] = coords[index] + other.coords[index]
    }
    return ret
}
typealias CubesND = HashSet<PointND>

fun main() {
    val puzzle = Puzzle17()
    try {
        val data = puzzle.readInputs("inputs.txt", 3)
        val answer = puzzle.partA(data)
        println("Answer is $answer")

        val dataB = puzzle.readInputs("inputs.txt", 4)
        val answerB = puzzle.partA(dataB)
        println("Answer is $answerB")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle17 {
    fun readInputs(filename: String, dims: Int): CubesND {
        val file = File(filename)
        val lines = file.readLines()
        val ret = CubesND()
        lines.forEachIndexed { yIndex, line ->
            line.forEachIndexed { xIndex, ch ->
                if (ch == '#') {
                    ret.add(PointND(dims, xIndex, yIndex))
                }
            }
        }
        return ret
    }

    fun partA(data: CubesND): Int {
        var newState = data
        for (i in 0 until 6) {
            newState = doOneTurn(newState)
        }
        return newState.size
    }

    private fun calcNeighbors(dims: Int) : Set<PointND> {
        val ret = HashSet<PointND>()
        addNeighbors(0, dims, PointND(dims), ret)
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

    private fun doOneTurn(data: CubesND) : CubesND {
        val ret = CubesND()
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

    private fun getAllNeighbors(point: PointND): List<PointND> {
        val neighbors = calcNeighbors(point.dims)
        return neighbors.map { it add point }
    }

    private fun countNeighbors(point: PointND, data: CubesND): Int {
        val allNeighbors = getAllNeighbors(point)
        val theCount = allNeighbors.count { data.contains(it) }
        return theCount
    }
}