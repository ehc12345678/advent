package com.advent2021.puzzle15

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet

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
data class Path(
    val squares: LinkedHashSet<Square>
) {
    val score: Int = if (squares.isEmpty()) {
        Integer.MAX_VALUE
    } else {
        squares.sumOf { it.num } - squares.first().num
    }

    val endOfPath: Square = squares.last()
    val priority: Float = score / squares.size.toFloat()

    override fun toString(): String {
        return squares.joinToString("->") { "${it.point.row},${it.point.col}" }
    }
}

typealias Solution = Int
typealias Solution2 = Int

fun main() {
    try {
        val puz = Puzzle15()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

//        val solution2 = puz.solvePuzzle2("inputsTest.txt", Data())
//        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle15 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val gridLine = Line()
        var col = 0
        for (element in line) {
            gridLine.add(
                Square(
                    Point(data.rows(), col++),
                    element - '0'
                )
            )
        }
        data.grid.add(gridLine)
    }

    override fun computeSolution(data: Data): Solution = computeImpl(data, data.rows(), data.cols())
    override fun computeSolution2(data: Data): Solution2 = computeImpl(data, data.rows() * 5, data.cols() * 5)

    fun calcWeight(pt: Point, data: Data): Int {
        val row = pt.row % data.rows()
        val col = pt.col % data.cols()
        return data.value(row, col)!!.num + (pt.row / data.rows()) + (pt.col / data.cols())
    }

    private fun computeImpl(data: Data, rows: Int, cols: Int): Int {
        val queue = PriorityQueue<Path>(1000) { path1, path2 ->
            if (path1.priority < path2.priority) -1 else 1
        }
        val firstPath = LinkedHashSet<Square>().also { it.add(data.value(0,0)!!) }
        val endPoint = Point(rows - 1, cols - 1)

        queue.add(Path(firstPath))

        val solutions = ArrayList<Path>()
        while (solutions.size < 1000) {
            val top = queue.remove()
            val topSquare = top.endOfPath
            if (topSquare.point == endPoint) {
                solutions.add(top)
            }

            val nextRow = Point(topSquare.point.row + 1, topSquare.point.col)
            if (nextRow.row < rows) {
                val nextRowPath = LinkedHashSet<Square>(top.squares).also {
                    it.add(Square(nextRow, calcWeight(nextRow, data)))
                }
                queue.add(Path(nextRowPath))
            }
            val nextCol = Point(topSquare.point.row, topSquare.point.col + 1)
            if (nextRow.col < cols) {
                val nextColPath = LinkedHashSet<Square>(top.squares).also {
                    it.add(Square(nextCol, calcWeight(nextCol, data)))
                }
                queue.add(Path(nextColPath))
            }
        }
        return solutions.minOfOrNull { it.score }!!
    }
}

