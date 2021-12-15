package com.advent2021.puzzle15

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet

data class Point(val row: Int, val col: Int)
class Square(val point: Point, var num: Int)

typealias Line = ArrayList<Square>
class Data {
    val grid: ArrayList<Line> = ArrayList()
    fun value(r: Int, c: Int): Square? = if (r in grid.indices && c in grid[r].indices) grid[r][c] else null
    fun neighbors(pt: Point): Set<Square> {
        val r = pt.row
        val c = pt.col
        return setOfNotNull(
            value(r - 1, c),
            value(r, c - 1), value(r, c + 1),
            value(r + 1, c)
        )
    }
    fun rows() = grid.size
    fun cols() = grid[0].size
    val endPoint: Point
        get() = Point(rows() - 1, cols() - 1)
}
data class Path(val squares: LinkedHashSet<Square>) {
    fun score() = if (squares.isEmpty()) {
        Integer.MAX_VALUE
    } else {
        squares.sumOf { it.num } - squares.first().num
    }

    val endOfPath: Square
        get() = squares.last()

    override fun toString(): String {
        return squares.joinToString("->") { "${it.point.row},${it.point.col}" }
    }
}
typealias Solution = Int
typealias Solution2 = Int

val NO_PATH = Path(LinkedHashSet())
typealias Working = HashMap<Square, Path>

fun main() {
    try {
        val puz = Puzzle15()
        val solution1 = puz.solvePuzzle("inputsTest.txt", Data())
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
            gridLine.add(
                Square(
                    Point(data.rows(), col++),
                    element - '0'
                )
            )
        }
        data.grid.add(gridLine)
    }

    override fun computeSolution(data: Data): Solution {
        val firstPath = LinkedHashSet<Square>().also { it.add(data.value(0,0)!!) }
        val working = Working()
        val bestPath = dftSol(Path(firstPath), data, working)
        return bestPath.score()
    }

    private fun dftSol(currentPath: Path, data: Data, working: Working): Path {
        val square = currentPath.endOfPath
        if (working.containsKey(square)) {
            return working[square]!!
        }
        if (square.point == data.endPoint) {
            return currentPath
        }

        var bestPath = NO_PATH
        val neighbors = data.neighbors(square.point) - currentPath.squares
        for (neighbor in neighbors) {
            val newPath = Path(LinkedHashSet(currentPath.squares).also { it.add(neighbor) })
            val bestNewPath = dftSol(newPath, data, working)
            val bestPathScore = bestPath.score()
            val bestNewPathScore = bestNewPath.score()
            if (bestNewPathScore < bestPathScore) {
                println("${square.point} Found new bestPath ${bestNewPath} with score $bestNewPathScore")
                bestPath = bestNewPath
            }
        }
        working[square] = bestPath
        return bestPath
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

