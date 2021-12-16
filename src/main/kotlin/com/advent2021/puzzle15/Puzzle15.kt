package com.advent2021.puzzle15

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayList

data class Point(val row: Int, val col: Int)
data class Square(val point: Point, var num: Int)

typealias Line = ArrayList<Square>
class Data {
    val grid: ArrayList<Line> = ArrayList()
    fun value(pt: Point) = value(pt.row, pt.col)
    fun value(r: Int, c: Int): Square? = if (r in grid.indices && c in grid[r].indices) grid[r][c] else null
    fun rows() = grid.size
    fun cols() = grid[0].size
}

data class Score(
    val point: Point,
    val score: Int
) {
    fun compare(other: Score) = Integer.compare(score, other.score)
}

typealias Solution = Int
typealias Solution2 = Int

fun main() {
    try {
        val puz = Puzzle15()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle15 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val gridLine = Line()
        var col = 0
        for (element in line) {
            gridLine.add(Square(Point(data.rows(), col++), element - '0'))
        }
        data.grid.add(gridLine)
    }

    override fun computeSolution(data: Data): Solution = computeImpl(data, data.rows(), data.cols())
    override fun computeSolution2(data: Data): Solution2 = computeImpl(data, data.rows() * 5, data.cols() * 5)

    fun calcWeight(pt: Point, data: Data): Int {
        val row = pt.row % data.rows()
        val col = pt.col % data.cols()
        val weight = data.value(row, col)!!.num + (pt.row / data.rows()) + (pt.col / data.cols())
        return if (weight > 9) weight - 9 else weight  // note: this is NOT mod 10...
    }

    private fun computeImpl(data: Data, rows: Int, cols: Int): Int {
        val queue = PriorityQueue<Score>(1000) { score1, score2 -> score1.compare(score2) }
        val firstScore = Score(Point(0, 0), 0)
        val endPoint = Point(rows - 1, cols - 1)

        queue.add(firstScore)
        val visited = HashSet<Point>()
        val neighbors = listOf(Point(0, 1), Point(0, -1), Point(1, 0), Point(0, 1))
        while (queue.peek().point != endPoint) {
            val top = queue.remove()
            val topPoint = top.point

            if (!visited.contains(topPoint)) {
                visited.add(topPoint)

                for (neighbor in neighbors) {
                    val newPoint = Point(topPoint.row + neighbor.row, topPoint.col + neighbor.col)
                    if (valid(newPoint, endPoint, visited)) {
                        queue.add(Score(newPoint, top.score + calcWeight(newPoint, data)))
                    }
                }
            }
        }
        return queue.peek().score
    }

    private fun valid(newPoint: Point, endPoint: Point, visited: HashSet<Point>): Boolean {
        return newPoint.row >= 0 && newPoint.row <= endPoint.row
                && newPoint.col >= 0 && newPoint.col <= endPoint.col
                && !visited.contains(newPoint)
    }
}

