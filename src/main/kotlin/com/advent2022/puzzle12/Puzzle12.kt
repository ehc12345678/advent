package com.advent2022.puzzle12

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

typealias Solution = Int
typealias Solution2 = Solution

data class Pos(val r: Int, val c: Int) {
    fun rowInc(inc: Int) = Pos(r + inc, c)
    fun colInc(inc: Int) = Pos(r, c + inc)
}
data class Cell(val pos: Pos, val ch: Char)
class Path(initVisited: ArrayList<Cell>? = ArrayList()) {
    val visited = ArrayList(initVisited ?: emptyList())

    val last: Cell
        get() = visited.last()

    fun add(cell: Cell): Path {
        return Path(visited).also { it.visited.add(cell) }
    }

    fun contains(cell: Cell): Boolean {
        return visited.contains(cell)
    }

    override fun toString(): String {
        return visited.joinToString { it.ch.toString() }
    }
}

class Data {
    val grid: ArrayList<ArrayList<Cell>> = ArrayList()
    var start: Cell? = null
    var end: Cell? = null

    fun value(pos: Pos): Cell? = if (pos.r in grid.indices && pos.c in grid[pos.r].indices) grid[pos.r][pos.c] else null
    fun neighbors(cell: Cell) = neighbors(cell.pos)
    fun neighbors(pos: Pos): List<Cell> {
        return listOfNotNull(
            value(pos.rowInc(-1)),
            value(pos.rowInc(1)),
            value(pos.colInc(-1)),
            value(pos.colInc(1)),
// diagnoals
//            value(r - 1, c - 1),
//            value (r - 1, c + 1),
//            value(r + 1, c - 1),
//            value (r + 1, c + 1)
        )
    }

    fun rows() = grid.size
    fun cols() = grid[0].size
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

    override fun computeSolution(data: Data): Solution {
        val queue = PriorityQueue<Path>(1000) { path1, path2 ->
            val bestPath1 = bestCanDo(path1, data)
            val bestPath2 = bestCanDo(path2, data)
            bestPath1.compareTo(bestPath2)
        }
        val seen = HashSet<Pos>()
        val startPath = Path().also { it.visited.add(data.start!!) }
        queue.add(startPath)
        seen.add(data.start!!.pos)

        var solution: Path? = null
        var tries = 0
        while (queue.isNotEmpty() && solution != null) {
            val head = queue.remove()

            if ((tries++ % 1000) == 0) {
                println("Try $tries with queue size of ${queue.size}")
            }
            if (head.last.pos == data.end!!.pos) {
                solution = head
            } else {
                val neighbors = data.neighbors(head.last)
                val added = HashSet<Pos>()
                neighbors.forEach {
                    if (!head.contains(it) && !seen.contains(it.pos) && canStep(head.last, it)) {
                        queue.add(head.add(it))
                        added.add(it.pos)
                    }
                }
                // add all the positions that we have seen
                seen.addAll(added)
            }
        }
        return solution!!.visited.size - 1
    }

    private fun canStep(last: Cell, next: Cell): Boolean {
        val start = elevation(last.ch)
        val end = elevation(next.ch)
        return end - start <= 1
    }

    private fun elevation(ch: Char): Int {
        return when(ch) {
            'S' -> 0
            'E' -> 27
            else -> ch - 'a' + 1
        }
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }

    private fun bestCanDo(path: Path, data: Data): Int {
        val endPos = data.end!!.pos
        val lastInPath = path.last.pos
        return path.visited.size + abs(endPos.r - lastInPath.r) + abs(endPos.c - lastInPath.c)
    }
}

