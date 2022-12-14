package com.advent2022.puzzle14

import com.advent2021.base.Base
import kotlin.math.max
import kotlin.math.min

data class Point(val x: Int, val y: Int) {
    fun add(incX: Int, incY: Int) = Point(x + incX, y + incY)
    fun add(pt: Point) = add(pt.x, pt.y)
}
data class Line(val start: Point, val end: Point) {
    val xRange: IntRange
        get() = min(start.x, end.x) .. max(start.x, end.x)
    val yRange: IntRange
        get() = min(start.y, end.y) .. max(start.y, end.y)
}
class Data {
    val sand = HashSet<Point>()

    val lines = ArrayList<Line>()
    var maxY = 0
    var maxX = 0
    var minX = Int.MAX_VALUE

    fun add(line: Line) {
        lines.add(line)
        maxY = max(max(line.start.y, maxY), line.end.y)
        maxX = max(max(line.start.x, maxX), line.end.x)
        minX = min(min(line.start.x, minX), line.end.x)
    }

    fun inBounds(pt: Point): Boolean {
        return pt.x in minX..maxX && pt.y <= maxY
    }

    fun pointOccupied(pt: Point): Boolean {
        if (sand.contains(pt)) {
            return true
        }
        return lines.any { interceptWithLine(pt, it) }
    }

    fun addSand(pt: Point) {
        sand.add(pt)
    }

    private fun interceptWithLine(pt: Point, line: Line): Boolean {
        return when {
            pt.x == line.start.x && line.start.x == line.end.x ->
                pt.y in line.yRange
            pt.y == line.start.y && line.start.y == line.end.y ->
                pt.x in line.xRange
            else ->
                false
        }
    }

    override fun toString(): String {
        val buf = StringBuffer()
        for (x in minX..maxX+2) {
            buf.append('-')
        }
        buf.append("\n")
        for (y in 0..maxY) {
            buf.append("${y % 10}:")
            for (x in minX..maxX) {
                val pos = Point(x, y)
                if (sand.contains(pos)) {
                    buf.append('o')
                } else if (pointOccupied(pos)) {
                    buf.append('#')
                } else {
                    buf.append(' ')
                }
            }
            buf.append("\n")
        }
        for (x in minX..maxX+2) {
            buf.append('-')
        }
        return buf.toString()
    }
}
typealias Solution = Int
typealias Solution2 = Solution

fun String.toPoint(): Point {
    val parts = split(',')
    return Point(parts[0].toInt(), parts[1].toInt())
}

fun main() {
    try {
        val puz = Puzzle16()
        val solution1 = puz.solvePuzzle("inputsTest.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle16 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val parts = line.split(" -> ")
        var last = parts[0].toPoint()
        for (i in 1 until parts.size) {
            val pt = parts[i].toPoint()
            data.add(Line(last, pt))
            last = pt
        }
    }

    override fun computeSolution(data: Data): Solution {
        var done = false
        val start = Point(500, 0)
        while (!done) {
            val sand = dropSand(start, data)
            if (sand == null) {
                done = true
            } else {
                data.addSand(sand)
                println(data)
            }
        }
        return data.sand.size
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }

    fun dropSand(pt: Point, data: Data): Point? {
        val nextPossible = listOf(Point(0, 1), Point(-1, 1), Point(1, 1)).map { it.add(pt) }
        if (nextPossible.any { !data.inBounds(it) }) {
            return null
        }
        // find the first place that is not occupied
        var next = nextPossible.find { !data.pointOccupied(it) }
        return if (next == null) {
            return pt // at rest
        } else {
            dropSand(next, data)
        }
    }
}

