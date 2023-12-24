package com.advent2023.puzzle17

import com.advent2023.base.Base
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

typealias Solution = Long
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle17()
        val solution1 = puz.solvePuzzle("inputsTest.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputsTest.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle17 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.addRow(line)
    }

    override fun computeSolution(data: Data): Solution {
        val queue = PriorityQueue<Path>(1000) { path1, path2 ->
            path1.compareTo(path2)
        }
        data.addNeighbors(queue, Path(), Position(0, 0))

        val endPoint = Position(data.rows() - 1, data.cols() - 1)
        val seen = HashMap<Position, Long>()
        val solutions = ArrayList<Path>()
//        while (queue.peek().pos != endPoint) {
        while (queue.isNotEmpty()) {
            val top = queue.remove()

            // only try the new path if we have not found a path to this position with a better score
            if (seen.getOrDefault(top.pos, Long.MAX_VALUE) > top.score) {
                seen[top.pos] = top.score
                if (top.pos == endPoint) {
                    solutions.add(top)
                } else {
                    data.addNeighbors(queue, top, top.pos)
                }
            }
        }
//        return queue.peek().score
        return solutions.minOf { it.score }
    }
    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

enum class Dir {
    RIGHT {
        override fun delta(): Position = Position(0, 1)
    },
    DOWN {
        override fun delta(): Position = Position(1, 0)
    },
    UP {
        override fun delta(): Position = Position(-1, 0)
    },
    LEFT {
        override fun delta(): Position = Position(0, -1)
    };

    abstract fun delta(): Position
}
class Data {
    val grid: ArrayList<List<Int>> = ArrayList()

    fun addRow(str: String) {
        grid.add(str.toCharArray().map { ch -> ch.toString().toInt() })
    }
    fun value(pos: Position): Int? = value(pos.r, pos.c)
    fun value(r: Int, c: Int): Int? = if (r in grid.indices && c in grid[r].indices) grid[r][c] else null

    private fun addElementIfPossible(queue: PriorityQueue<Path>, pos: Position, direction: Dir, path: Path) {
        val newPos = pos + direction.delta()
        val value = value(newPos)
        if (value != null && path.canAddElement(newPos, direction)) {
            queue.add(path.addElement(newPos, direction, value))
        }
    }

    fun addNeighbors(queue: PriorityQueue<Path>, path: Path, pos: Position) {
        Dir.values().forEach { dir ->
            addElementIfPossible(queue, pos, dir, path)
        }
    }

    fun rows() = grid.size
    fun cols() = grid[0].size
}

data class Position(val r: Int, val c: Int) {
    operator fun plus(other: Position) = Position(r + other.r, c + other.c)
}

data class PathElement(val pos: Position, val direction: Dir, val number: Int)
class Path(
    val elements: List<PathElement> = ArrayList(),
    val seen: Set<Position> = HashSet()
) : Comparable<Path> {
    val score : Long = elements.sumOf { it.number.toLong() }

    fun addElement(pos: Position, dir: Dir, num: Int): Path {
        return Path(elements + PathElement(pos, dir, num), seen + setOf(pos))
    }

    val pos: Position
        get() = elements.last().pos

    fun canAddElement(pos: Position, dir: Dir): Boolean {
        // cannot add something we have seen
        return if (seen.contains(pos)) {
            false
        // if we haven't seen at least 3 items, we cannot possible have 3 in a row
        } else if (elements.size < 3) {
            true
        // we can add to the path if we don't exceed 3 in the same direction
        } else {
            !(1.. 3).all { elements[elements.size - it].direction == dir }
        }
    }

    val length: Int
        get() = elements.size

    override fun compareTo(other: Path): Int {
        // favor lower scores with longer paths
        var ret = score.compareTo(other.score)
        if (ret == 0) {
            ret = other.length.compareTo(length)
        }
        return ret
    }

    override fun toString(): String {
        return "${score},${length}: ${elements.joinToString("->") { it.number.toString() }}"
    }
}
