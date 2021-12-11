package com.advent2021.puzzle11

import com.advent2021.base.Base

data class Point(val row: Int, val col: Int)
class Square(val point: Point, var num: Int) {
    fun increment(): Int = ++num
    fun zeroOut() { num = 0 }
}

typealias Line = ArrayList<Square>
class Data {
    val grid: ArrayList<Line> = ArrayList()
    fun value(r: Int, c: Int): Square? = if (r in grid.indices && c in grid[r].indices) grid[r][c] else null
    fun neighbors(r: Int, c: Int): List<Square> {
        return listOfNotNull(
            value(r - 1, c - 1), value(r - 1, c), value (r - 1, c + 1),
            value(r, c - 1), value(r, c + 1),
            value(r + 1, c - 1), value(r + 1, c), value (r + 1, c + 1)
        )
    }
    fun rows() = grid.size
    fun cols() = grid[0].size

    override fun toString(): String {
        return toString(emptySet())
    }
    fun toString(thisFlashes: Set<Point>): String {
        return grid.joinToString("\n") {
            line -> line.joinToString("") {
                if (thisFlashes.contains(it.point)) "*" else if (it.num > 9) "-" else it.num.toString()
            }
        }
    }
}
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle11()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle11 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val gridLine = Line()
        var col = 0
        for (element in line) {
            gridLine.add(Square(Point(data.rows(), col++), element - '0'))
        }
        data.grid.add(gridLine)
    }

    override fun computeSolution(data: Data): Solution {
        val numSteps = 100
        var answer = 0
        println("Solving for $data")
        for (i in 0 until numSteps) {
            answer += doStep(data)
        }
        return answer
    }

    override fun computeSolution2(data: Data): Solution2 {
        var i = 1
        while (doStep(data) != data.rows() * data.cols()) {
            ++i
        }
        return i
    }


    private fun doStep(data: Data): Int {
        for (r in 0 until data.rows()) {
            for (c in 0 until data.cols()) {
                data.value(r, c)!!.increment()
            }
        }

        val flashes = HashSet<Point>()
        var endConditionReached: Boolean
        do {
            val thisFlashes = HashSet<Point>()
            for (r in 0 until data.rows()) {
                for (c in 0 until data.cols()) {
                    val sq = data.value(r, c)!!
                    if (sq.num > 9 && !flashes.contains(sq.point)) {
                        thisFlashes.add(sq.point)
                    }
                }
            }
//            println(data.toString(thisFlashes))
//            println("----------")

            endConditionReached = thisFlashes.isEmpty()
            if (!endConditionReached) {
                thisFlashes.forEach { point ->
                    data.neighbors(point.row, point.col).forEach { neighbor -> neighbor.increment() }
                }
                flashes.addAll(thisFlashes)
                thisFlashes.clear()
            }
        } while (!endConditionReached)

        flashes.forEach { data.value(it.row, it.col)?.zeroOut() }
        return flashes.size
    }

}

