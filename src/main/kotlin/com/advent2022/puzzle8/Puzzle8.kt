package com.advent2022.puzzle8

import com.advent2021.base.Base
import java.lang.Integer.max

data class Cell(
    val num: Int,
    val r: Int,
    val c: Int,
    var visible: Boolean = false
)
class Data {
    var grid = ArrayList<List<Cell>>()

    fun addRow(row: String) {
        grid.add(row.toCharArray().mapIndexed { index, it -> Cell(it - '0', grid.size, index) })
    }

    val numRows: Int
        get() = grid.size
    val numCols: Int
        get() = if (grid.isEmpty()) 0 else grid.first().size
    fun cell(r: Int, c: Int) = grid[r][c]
    fun cellOrNull(r: Int, c: Int) = if (hasCell(r, c)) cell(r, c) else null
    fun hasCell(r: Int, c: Int) = r in 0 until numRows && c in 0 until numCols
    val allCells: List<Cell>
        get() = grid.flatten()

    fun scenicScore(cell: Cell): Int {
        return canSeeLeft(cell) * canSeeUp(cell) * canSeeRight(cell) * canSeeDown(cell)
    }

    private fun canSee(cell: Cell, fn: (cell: Cell) -> Cell?): Int {
        var nextCell: Cell? = fn(cell)
        var ret = 0
        while (nextCell != null) {
            ++ret
            if (nextCell.num < cell.num) {
                nextCell = fn(nextCell)
            } else {
                break
            }
        }
        return ret
    }

    private fun canSeeLeft(cell: Cell): Int = canSee(cell) { cellOrNull(it.r, it.c - 1) }
    private fun canSeeRight(cell: Cell): Int  = canSee(cell) { cellOrNull(it.r, it.c + 1) }
    private fun canSeeUp(cell: Cell): Int = canSee(cell) { cellOrNull(it.r - 1, it.c) }
    private fun canSeeDown(cell: Cell): Int  = canSee(cell) { cellOrNull(it.r + 1, it.c) }
}
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle8()
        val solution1 = puz.solvePuzzle("inputsTest.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}
fun nullMax(a: Int?, b: Int?): Int? = if (a == null) b else if (b == null) a else max(a, b)
fun visible(num: Int, max: Int?) = num > (max ?: -1)

class Puzzle8 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.addRow(line)
    }

    override fun computeSolution(data: Data): Solution {
        computeVisible(data)
        return data.grid.sumOf { row -> row.count { it.visible } }
    }

    private fun computeVisible(data: Data) {
        for (r in 0 until data.numRows) {
            // forward
            var maxLeft: Int? = null
            for (c in 0 until data.numCols) {
                val cell = data.cell(r, c)
                cell.visible = cell.visible || visible(cell.num, maxLeft)
                maxLeft = nullMax(cell.num, maxLeft)
            }

            // backwards
            var maxRight: Int? = null
            for (c in data.numCols - 1 downTo 0) {
                val cell = data.cell(r, c)
                cell.visible = cell.visible || visible(cell.num, maxRight)
                maxRight = nullMax(cell.num, maxRight)
            }

        }
        for (c in 0 until data.numCols) {
            // down
            var maxUp: Int? = null
            for (r in 0 until data.numRows) {
                val cell = data.cell(r, c)
                cell.visible = cell.visible || visible(cell.num, maxUp)
                maxUp = nullMax(cell.num, maxUp)
            }

            // up
            var maxDown: Int? = null
            for (r in data.numRows - 1 downTo 0) {
                val cell = data.cell(r, c)
                cell.visible = cell.visible || visible(cell.num, maxDown)
                maxDown = nullMax(cell.num, maxDown)
            }
        }
    }

    override fun computeSolution2(data: Data): Solution2 {
        computeVisible(data)
        val solution = data.allCells.maxOf { data.scenicScore(it) }
//        data.allCells.forEach { println("$it ${data.scenicScore(it)}") }
//        val cell = data.allCells.find { it.scenicScore == solution }
//        println(cell)
        return solution
    }
}

