package com.advent2021.puzzle9

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayList

data class Square(val row: Int, val col: Int, val num: Int)
typealias Line = ArrayList<Square>
class Data {
    val grid: ArrayList<Line> = ArrayList()
    fun value(r: Int, c: Int) = if (r in grid.indices && c in grid[r].indices) grid[r][c] else null
    fun neighbors(r: Int, c: Int): List<Square> {
        return listOfNotNull(
            value(r - 1, c),
            value(r, c - 1), value(r, c + 1),
            value(r + 1, c)
        )
    }
    fun rows() = grid.size
    fun cols() = grid[0].size
}
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle9()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle9 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val gridLine = Line()
        var col = 0
        for (element in line) {
            gridLine.add(Square(data.rows(), col++, element - '0'))
        }
        data.grid.add(gridLine)
    }

    override fun computeSolution(data: Data): Solution {
        return getLowPoints(data).sumOf { it.num + 1 }
    }

    private fun getLowPoints(data: Data): List<Square> {
        val ret = ArrayList<Square>()
        for (r in 0 until data.rows()) {
            for (c in 0 until data.cols()) {
                val value = data.value(r, c)!!
                if (data.neighbors(r, c).map { it.num }.minOrNull()!! > value.num) {
                    ret.add(value)
                }
            }
        }
        return ret
    }

    override fun computeSolution2(data: Data): Solution2 {
        val lowPoints = getLowPoints(data)
        val troughs = lowPoints.map { computeTrough(it, data) }
        return troughs.sortedByDescending { it }.subList(0, 3).reduce { acc, num -> acc * num }
    }

    private fun computeTrough(sq: Square, data: Data): Int {
        val stack = Stack<Square>()
        val visited = HashSet<Square>()
        stack.push(sq)
        while (stack.isNotEmpty()) {
            val top = stack.pop()
            data.neighbors(top.row, top.col).forEach { visitSpoke(top, it, visited, stack, data) }
        }
        return visited.size
    }

    private fun visitSpoke(initalSq: Square, spokeSq: Square?, visited: HashSet<Square>, stack: Stack<Square>,
        data: Data) {
        if (validSq(spokeSq, visited)) {
            visited.add(spokeSq!!)
            stack.push(spokeSq)

            val deltaRow = initalSq.row - spokeSq.row
            val deltaCol = initalSq.col - spokeSq.col
            var row = spokeSq.row + deltaRow
            var col = spokeSq.col + deltaCol
            val compare = initalSq.num.compareTo(spokeSq.num) < 0
            var nextSq = data.value(row, col)
            while (validSq(nextSq, visited) && compare == initalSq.num.compareTo(nextSq!!.num) < 0) {
                visited.add(nextSq)
                stack.push(nextSq)
                row += deltaRow
                col += deltaCol
                nextSq = data.value(row, col)
            }
        }
    }

    private fun validSq(spokeSq: Square?, visited: HashSet<Square>) =
        spokeSq != null && !visited.contains(spokeSq) && spokeSq.num != 9
}

