package com.advent2021.puzzle25

import com.advent2021.base.Base

typealias Line = ArrayList<Char>
class Data(val lines: ArrayList<Line> = ArrayList()) {
    constructor(numRows: Int, numCols: Int): this() {
        for (r in 0 until numRows) {
            var line = ""
            for (c in 0 until numCols) {
                line += '.'
            }
            add(line)
        }
    }
    fun getChar(row: Int, col: Int): Char {
        return lines[getRealRow(row)][getRealCol(col)]
    }
    fun setChar(row: Int, col: Int, ch: Char) {
        lines[getRealRow(row)][getRealCol(col)] = ch
    }

    private fun getRealCol(col: Int): Int {
        val c = if (col < 0) {
            cols() + col
        } else {
            col % cols()
        }
        return c
    }

    private fun getRealRow(row: Int): Int {
        val r = if (row < 0) {
            rows() + row
        } else {
            row % rows()
        }
        return r
    }

    fun add(line: String) {
        lines.add(Line(line.toCharArray().toList()))
    }
    fun isOpen(row: Int, col: Int): Boolean = getChar(row, col) == '.'

    fun rows() = lines.size
    fun cols() = lines[0].size
    override fun equals(other: Any?): Boolean {
        return (other as Data).lines == lines
    }

    override fun toString(): String {
        val buf = StringBuffer()
        for (line in lines) {
            for (ch in line) {
                buf.append(ch)
            }
            buf.append("\n")
        }
        return buf.toString()
    }
}
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle25()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle25 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line)
    }

    override fun computeSolution(data: Data): Solution {
        var prevData = data
        var newData = doStep(prevData)

        var steps = 1
        while (prevData != newData) {
            prevData = newData
            newData = doStep(newData)
            ++steps
        }
        return steps
    }
    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }

    fun doStep(data: Data): Data {
        return doStepB(doStepA(data))
    }

    fun doStepA(data: Data): Data {
        val newData = Data(data.rows(), data.cols())
        for (r in 0 until data.rows()) {
            var c = 0
            while (c < data.cols()) {
                val ch = data.getChar(r, c)
                if (ch == '>' && data.isOpen(r, c + 1)) {
                    newData.setChar(r, c++, '.')
                }
                newData.setChar(r, c++, ch)
            }
        }
        return newData
    }

    fun doStepB(data: Data): Data {
        val newData = Data(data.rows(), data.cols())
        for (c in 0 until data.cols()) {
            var r = 0
            while (r < data.rows()) {
                val ch = data.getChar(r, c)
                if (ch == 'v' && data.isOpen(r + 1, c)) {
                    newData.setChar(r++, c, '.')
                }
                newData.setChar(r++, c, ch)
            }
        }
        return newData
    }
}

