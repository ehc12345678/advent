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
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

//        val solution2 = puz.solvePuzzle2("inputsTest.txt", Data())
//        println("Solution2: $solution2")
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
        val first = PathElement(Position(0,0), Dir.RIGHT, 0, 1)
        data.addNeighbors(queue, Path(listOf(first), HashSet()), Position(0, 0))

        val endPoint = Position(data.rows() - 1, data.cols() - 1)
        val seen = HashSet<String>()
        while (queue.peek().pos != endPoint) {
            val top = queue.remove()

            data.getNeighbors(top.pos, top)
                .forEach { nextPath ->
                    val key = "${nextPath.key}/${top.last.key}"
                    if (!seen.contains(key)) {
                        seen.add(key)
                        queue.add(top.addElement(nextPath))
                    }
                }
        }
        return queue.peek().score
    }
    override fun computeSolution2(data: Data): Solution2 {
        return computeSolution(data)
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

    fun addNeighbors(queue: PriorityQueue<Path>, path: Path, pos: Position) {
        getNeighbors(pos, path).forEach { queue.add(path.addElement(it)) }
    }

    fun getNeighbors(pos: Position, path: Path): List<PathElement> {
        return Dir.values()
            .filter { dir ->
                value(pos + dir.delta()) != null
            }
            .map { dir ->
                val value = value(pos + dir.delta())
                val numSameDir = if (dir == path.last.direction) {
                    path.last.numSameDir + 1
                } else {
                    1
                }
                PathElement(pos + dir.delta(), dir, value!!, numSameDir)
            }.filter { pathElement ->
                path.canAddElement(pathElement)
            }
    }

    fun rows() = grid.size
    fun cols() = grid[0].size
}

data class Position(val r: Int, val c: Int) {
    operator fun plus(other: Position) = Position(r + other.r, c + other.c)
}

data class PathElement(val pos: Position, val direction: Dir, val number: Int, val numSameDir: Int) {
    val key: String = "$direction $numSameDir $pos"
}

class Path(
    val elements: List<PathElement> = ArrayList(),
    val seen: Set<Position> = HashSet()
) : Comparable<Path> {
    val score : Long = elements.sumOf { it.number.toLong() }

    fun addElement(pathElement: PathElement): Path {
        return Path(elements + pathElement, seen + setOf(pathElement.pos))
    }

    val pos: Position
        get() = elements.last().pos
    val last: PathElement
        get() = elements.last()

    fun canAddElement(pathElement: PathElement): Boolean {
        // cannot add something we have seen
        return if (seen.contains(pathElement.pos)) {
            false
        // if we haven't seen at least 3 items, we cannot possible have 3 in a row
        } else if (pathElement.numSameDir <= 3) {
            true
        // we can add to the path if we don't exceed 3 in the same direction
        } else {
            pathElement.direction != last.direction
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

    fun pathElementsAsStr() = elements.joinToString("->") { it.number.toString() }

    override fun toString(): String {
        return "${score},${length}: ${pathElementsAsStr()}"
    }
}
