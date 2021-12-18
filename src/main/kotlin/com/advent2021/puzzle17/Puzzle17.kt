package com.advent2021.puzzle17

import com.advent2021.base.Base
import com.advent2021.puzzle5.Point
import kotlin.math.max

data class Bound(
    val minXY: Point,
    val maxXY: Point
) {
    fun inBounds(pt: Point): Boolean {
        return pt.x >= minXY.x && pt.y >= minXY.y && pt.x <= maxXY.x && pt.y <= maxXY.y
    }
}
data class Data(var bounds: Bound = Bound(Point(0,0), Point(0,0)))
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle17()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle17 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val xyParts = line.split(", ")
        val xParts = xyParts[0].split("=")[1].split("..").map { it.toInt() }
        val yParts = xyParts[1].split("=")[1].split("..").map { it.toInt() }
        data.bounds =
            Bound(
                Point(xParts.minOf { it }, yParts.minOf { it }),
                Point(xParts.maxOf { it }, yParts.maxOf { it })
            )
    }

    override fun computeSolution(data: Data): Solution {
        var highestPoint = 0

        // brute force it
        for (velX in 1..data.bounds.minXY.x) {
            for (velY in 1..120) {
                highestPoint = max(highestPoint, findHighest(data.bounds, velX, velY))
            }
        }
        return highestPoint
    }

    private fun findHighest(bounds: Bound, vX: Int, vY: Int): Int {
        var pt = Point(0, 0)
        var high = 0
        var velX = vX
        var velY = vY

        while (pt.x <= bounds.maxXY.x && pt.y >= bounds.minXY.y) {
            if (bounds.inBounds(pt)) {
                return high
            }
            high = max(pt.y, high)
            pt = Point(pt.x + velX, pt.y + velY)
            velX = when {
                velX == 0 -> 0
                velX > 0 -> { velX - 1 }
                else -> { velX + 1 }
            }
            velY -= 1
        }

        return -1
    }

    override fun computeSolution2(data: Data): Solution2 {
        var count = 0

        // brute force it
        for (velX in 1..data.bounds.maxXY.x) {
            for (velY in data.bounds.minXY.y..100) {
                if (findHighest(data.bounds, velX, velY) >= 0) {
                    ++count
                }
            }
        }
        return count
    }
}

