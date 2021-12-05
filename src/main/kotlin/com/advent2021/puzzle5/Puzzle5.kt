package com.advent2021.puzzle5

import com.advent2021.base.Base

data class Point(val x: Int, val y: Int)
data class Line(val pt1: Point, val pt2: Point)
class Data(
    val lines: ArrayList<Line> = ArrayList(),
)
class Solution {
    val pointMap: HashMap<Point, Int> = HashMap()
    fun addPoints(line: Line, diagonals: Boolean) {
        if (line.pt1.x == line.pt2.x || line.pt1.y == line.pt2.y || diagonals) {
            addPoints(line.pt1, line.pt2)
        }
    }

    fun addPoints(pt1: Point, pt2: Point) {
        var x = pt1.x
        var y = pt1.y
        addPoint(x, y)

        val x2 = pt2.x
        val y2 = pt2.y
        val stepX = step(x, x2)
        val stepY = step(y, y2)
        do {
            x += stepX
            y += stepY
            addPoint(x, y)
        } while (x != x2 || y != y2)
    }

    private fun step(x: Int, x2: Int) = if (x > x2) -1 else if (x < x2) 1 else 0

    fun addPoint(x: Int, y: Int) {
        val point = Point(x, y)
        pointMap[point] = pointMap.getOrDefault(point, 0) + 1
    }

    fun solution() = pointMap.entries.count { it.value >= 2 }
}
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle1()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: ${solution1?.solution()}")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: ${solution2?.solution()}")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle1 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val points = line.split(" -> ")
        val pt1 = points[0].split(",")
        val pt2 = points[1].split(",")
        data.lines.add(Line(Point(pt1[0].toInt(), pt1[1].toInt()), Point(pt2[0].toInt(), pt2[1].toInt())))
    }

    override fun computeSolution(data: Data): Solution {
        val sol = Solution()
        data.lines.forEach {
            sol.addPoints(it, false)
        }
        return sol
    }
    override fun computeSolution2(data: Data): Solution2 {
        val sol = Solution()
        data.lines.forEach {
            sol.addPoints(it, true)
        }
        return sol
    }
}

