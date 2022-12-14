package com.advent2022.puzzle12

import com.advent2021.base.Base

typealias Solution = Int
typealias Solution2 = Solution

data class Pos(val r: Int, val c: Int) {
    fun inc(rInc: Int, cInc: Int) = Pos(rInc + r, cInc + c)
}
data class Cell(val pos: Pos, val ch: Char, var visited: Boolean = false, var shortestDistance: Int = Int.MAX_VALUE)

class Data {
    val grid: ArrayList<ArrayList<Cell>> = ArrayList()
    var start: Cell? = null
    var end: Cell? = null

    fun value(pos: Pos): Cell? = if (pos.r in grid.indices && pos.c in grid[pos.r].indices) grid[pos.r][pos.c] else null
    fun neighbors(cell: Cell) = neighbors(cell.pos)
    fun neighbors(pos: Pos): List<Cell> {
        return listOfNotNull(
            value(pos.inc(-1, 0)),
            value(pos.inc(1, 0)),
            value(pos.inc(0, -1)),
            value(pos.inc(0, 1)),
        )
    }
}

fun main() {
    try {
        val puz = Puzzle12()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle12 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val r = data.grid.size
        data.grid.add(ArrayList(line.toCharArray().mapIndexed { index, ch ->
            val cell = Cell(Pos(r, index), ch)
            if (ch == 'S') {
                data.start = cell
            } else if (ch == 'E') {
                data.end = cell
            }
            cell
        }))
    }

    override fun computeSolution(data: Data): Solution = computeSolutionTo(data, data.start!!.ch)

    fun computeSolutionTo(data: Data, end: Char): Solution {
        val start = data.end!!.also {
            it.shortestDistance = 0
            it.visited = true
        }

        var queue: List<Cell> = ArrayList<Cell>(getNeighbors(start, data))

        var iteration = 0
        var found: Cell? = null
        while (found == null) {
            val allNeighbors = ArrayList<Cell>()
            for (i in queue.indices) {
                val cell = queue[i]
                if (cell.ch == end) {
                    found = cell
                    break
                }
                allNeighbors.addAll(getNeighbors(cell, data))
            }
            queue = allNeighbors.sortedBy { it.shortestDistance }
        }
        return found.shortestDistance
    }

    private fun getNeighbors(cell: Cell, data: Data): List<Cell> {
        val ret = data.neighbors(cell).filter { !it.visited && canStep(cell, it) }
        ret.forEach {
            it.visited = true
            it.shortestDistance = cell.shortestDistance + 1
        }
        return ret
    }

    private fun canStep(last: Cell, next: Cell): Boolean {
        val start = elevation(last.ch)
        val end = elevation(next.ch)
        return start - end <= 1
    }

    private fun elevation(ch: Char): Int {
        return when(ch) {
            'S' -> 0
            'E' -> 27
            else -> ch - 'a' + 1
        }
    }

    override fun computeSolution2(data: Data): Solution2 = computeSolutionTo(data, 'a')
}

