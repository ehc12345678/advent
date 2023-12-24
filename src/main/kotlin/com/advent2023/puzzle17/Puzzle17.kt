package com.advent2023.puzzle17

import com.advent2023.base.Base
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

typealias Solution = Long
typealias Solution2 = Solution

typealias PredicateCanAddElement = (PathElement, PathElement) -> Boolean

fun solution1pred(pathElement: PathElement, last: PathElement): Boolean {
    return if (pathElement.numSameDir <= 3) {
        true
        // we can add to the path if we don't exceed 3 in the same direction
    } else {
        pathElement.direction != last.direction
    }
}

fun solution2pred(pathElement: PathElement, last: PathElement): Boolean {
    return if (last.numSameDir < 4) {
        // crucible must move in a straight line for 4 consecutive times
        pathElement.direction == last.direction
    } else if (pathElement.numSameDir <= 10) {
        // we can then only move 10 consecutive times in the same direction
        true
    } else {
        pathElement.direction != last.direction
    }
}


fun main() {
    try {
        val puz = Puzzle17()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
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
        return computeWithPred(data, ::solution1pred)
    }

    override fun computeSolution2(data: Data): Solution2 {
        return computeWithPred(data, ::solution2pred)
    }

    private fun computeWithPred(data: Data, pred: PredicateCanAddElement): Long {
        val queue = PriorityQueue<Path>(1000) { path1, path2 ->
            path1.compareTo(path2)
        }
        queue.add(Path(listOf(PathElement(Position(0, 0), Dir.RIGHT, 0, 1))))
        queue.add(Path(listOf(PathElement(Position(0, 0), Dir.DOWN, 0, 1))))

        val endPoint = Position(data.rows() - 1, data.cols() - 1)
        val seen = HashSet<String>()
        while (queue.peek().pos != endPoint) {
            val top = queue.remove()
            val neighbors = data.getNeighbors(top.pos, top, pred)
            neighbors
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

    fun getNeighbors(pos: Position, path: Path, canAddPredicate: PredicateCanAddElement): List<PathElement> {
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
                path.canAddElement(pathElement, canAddPredicate)
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
    private val elements: List<PathElement> = ArrayList()
) : Comparable<Path> {
    val score : Long = elements.sumOf { it.number.toLong() }

    fun addElement(pathElement: PathElement): Path {
        return Path(elements + pathElement)
    }

    val pos: Position
        get() = elements.last().pos
    val last: PathElement
        get() = elements.last()

    fun canAddElement(pathElement: PathElement, pred: PredicateCanAddElement): Boolean {
        // cannot go directly backwards
        return if (last.direction == opposite(pathElement.direction)) {
            false
        } else {
            pred(pathElement, last)
        }
    }

    private fun opposite(direction: Dir): Dir =
        when(direction) {
            Dir.RIGHT -> Dir.LEFT
            Dir.DOWN -> Dir.UP
            Dir.UP -> Dir.DOWN
            Dir.LEFT -> Dir.RIGHT
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

    private fun pathElementsAsStr() = elements.joinToString("->") { it.number.toString() }

    override fun toString(): String {
        return "${score},${length}: ${pathElementsAsStr()}"
    }
}
