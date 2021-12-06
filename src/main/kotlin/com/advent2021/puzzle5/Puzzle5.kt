package com.advent2021.puzzle5

import com.advent2021.base.Base

data class Point(val x: Int, val y: Int) {
    constructor(array: List<Int>) : this(array[0], array[1])
}
data class Line(val pt1: Point, val pt2: Point) {
    constructor(pts: List<Point>) : this(pts[0], pts[1])
}
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

        val stepX = step(x, pt2.x)
        val stepY = step(y, pt2.y)
        do {
            x += stepX
            y += stepY
            addPoint(x, y)
        } while (x != pt2.x || y != pt2.y)
    }

    private fun step(x: Int, x2: Int) = if (x > x2) -1 else if (x < x2) 1 else 0

    fun addPoint(x: Int, y: Int) = Point(x, y).also { pt -> pointMap[pt] = pointMap.getOrDefault(pt, 0) + 1 }

    fun solution() = pointMap.entries.count { it.value >= 2 }
}
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle5()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: ${solution1?.solution()}")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: ${solution2?.solution()}")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

fun String.toPoint() = Point(split(",").map { it.toInt() })
fun String.toLine() = Line(split(" -> ").map { it.toPoint() })

class Puzzle5 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.lines.add(line.toLine())
    }

    override fun computeSolution(data: Data): Solution = computeSolution(data, false)
    override fun computeSolution2(data: Data): Solution = computeSolution(data, true)

    private fun computeSolution(data: Data, diagonals: Boolean): Solution {
        return Solution().also { sol ->
            data.lines.forEach {
                sol.addPoints(it, diagonals)
            }
        }
    }

}

