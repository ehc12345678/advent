package com.advent2021.puzzle20

import com.advent2021.base.Base

class Grid(var lines: ArrayList<String> = ArrayList()) {
    fun add(line: String) = lines.add(line)
}
class Data(
    var enhance: String = "",
    val grid: Grid = Grid(),
    var defaultBit: Boolean = false
) {
    var inGrid: Boolean = false
    fun isEnhanceOn(pos: Int): Boolean = enhance[pos] == '#'
    fun gridOn(row: Int, col: Int): Boolean {
        return if (row in grid.lines.indices && col in grid.lines[0].indices) {
            grid.lines[row][col] == '#'
        } else {
            defaultBit
        }
    }
    fun bin(row: Int, col: Int) = if (gridOn(row, col)) '1' else '0'
    fun posSurroundingNine(row: Int, col: Int): Int {
        var binaryString = ""
        for (r in row - 1 .. row + 1) {
            for (c in col -1 .. col + 1) {
                binaryString += bin(r, c)
            }
        }
        return binaryString.toInt(2)
    }

    fun countPixels(): Int {
        return grid.lines.sumOf { line -> line.count { it == '#' }}
    }

    override fun toString(): String {
        val buf = StringBuffer()
        val numCols = grid.lines.maxOfOrNull { it.length }!!
        for (r in -1 .. grid.lines.size) {
            if (r == grid.lines.size) {
                buf.append("  ")
                for (c in 0 until numCols) { buf.append("-") }
                buf.append('\n')
            }
            for (c in -1 .. numCols) {
                if (c == numCols) buf.append('|')
                buf.append(if (gridOn(r,c)) '#' else '.')
                if (c < 0) buf.append('|')
            }
            buf.append("\n")
            if (r < 0) {
                buf.append("  ")
                for (c in 0 until numCols) { buf.append("-") }
                buf.append('\n')
            }
        }
        return buf.toString()
    }
}
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle20()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle20 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        if (line.isNotEmpty()) {
            if (data.inGrid) {
                data.grid.add(line)
            } else {
                data.enhance += line.trim()
            }
        } else {
            data.inGrid = true
        }
    }

    override fun computeSolution(data: Data): Solution {
        val newData = enhance(data)
        val newData2 = enhance(newData)
        return newData2.countPixels()
    }

    fun enhance(data: Data): Data {
        val newGrid = Grid()
        val lines = data.grid.lines
        val numCols = lines.maxOfOrNull { it.length }

        for (r in -1 .. lines.size) {
            var newLine = ""
            for (c in -1 .. numCols!!) {
                newLine += if (data.isEnhanceOn(data.posSurroundingNine(r, c))) '#' else '.'
            }
            newGrid.add(newLine)
        }
        return Data(data.enhance, newGrid, data.defaultBit xor data.isEnhanceOn(0))
    }

    override fun computeSolution2(data: Data): Solution2 {
        var newData = data
        for (i in 0 until 50) {
            newData = enhance(newData)
        }
        return newData.countPixels()
    }
}

