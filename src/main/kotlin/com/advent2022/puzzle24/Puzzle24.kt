package com.advent2022.puzzle24

import com.advent2021.base.Base
import com.advent2022.puzzle23.Compass
import com.advent2022.puzzle23.Pos
import java.util.*
import kotlin.math.abs
import kotlin.math.min

typealias Solution = Int
typealias Solution2 = Solution

data class Blizzard(var pos: Pos, val compass: Compass) {
    fun move(data: Data): Blizzard {
        val newPos = data.wrap(pos.inDirection(compass))
        return Blizzard(newPos, compass)
    }
}
class Data {
    var yPos = 0
    val blizzards = ArrayList<Blizzard>()
    val entrance = Pos(1,0)
    var exit: Pos? = null

    val xRange: IntRange
        get() = 1..exit!!.x
    val yRange: IntRange
        get() = 1..(exit!!.y - 1)

    fun addBlizzard(x: Int, y: Int, compass: Compass) {
        blizzards.add(Blizzard(Pos(x, y), compass))
    }

    fun wrap(pos: Pos): Pos {
        return when {
            pos.x < xRange.first -> Pos(xRange.last, pos.y)
            pos.x > xRange.last -> Pos(xRange.first, pos.y)
            pos.y < yRange.first -> Pos(pos.x, yRange.last)
            pos.y > yRange.last -> Pos(pos.x, yRange.first)
            else -> pos
        }
    }

    fun neighbors(pos: Pos): List<Pos> {
        return Compass.values().map { pos.inDirection(it) }.filter {
            it == exit || (it.x in xRange && it.y in yRange)
        }
    }
}

private fun manhattanDistance(pt1: Pos, pt2: Pos): Int {
    val diff = pt1 - pt2
    return abs(diff.x) + abs(diff.y)
}

data class State(val pos: Pos, val endPos: Pos, val blizzards: List<Blizzard> = ArrayList(), val minute: Int = 0) {
    fun blizzardsMove(blizzards: List<Blizzard>, data: Data): List<Blizzard> {
        return blizzards.map { blizzard -> blizzard.move(data) }
    }
    fun compare(other: State): Int {
        var cmp = -score.compareTo(other.score)
        if (cmp == 0) {
            // favor the one that is further along
            cmp = minute.compareTo(other.minute)
        }
        return cmp
    }
    fun wait(newBlizzards: List<Blizzard>) = State(pos, endPos, newBlizzards, minute + 1)
    fun moveTo(neighbor: Pos, newBlizzards: List<Blizzard>) = State(neighbor, endPos, newBlizzards, minute + 1)

    val score: Int
        get() = minute + manhattanDistance(pos, endPos)
}

fun main() {
    try {
        val puz = Puzzle24()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle24 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        line.toCharArray().forEachIndexed { x, ch ->
            when (ch) {
                '>' -> data.addBlizzard(x, data.yPos, Compass.E)
                '<' -> data.addBlizzard(x, data.yPos, Compass.W)
                '^' -> data.addBlizzard(x, data.yPos, Compass.N)
                'v' -> data.addBlizzard(x, data.yPos, Compass.S)
            }
        }
        data.yPos++
    }

    override fun computeSolution(data: Data): Solution {
        val exit = Pos(data.blizzards.maxOf { it.pos.x }, data.blizzards.maxOf { it.pos.y + 1 })
        data.exit = exit

        val initial = State(data.entrance, exit, data.blizzards, 0)
        val queue = PriorityQueue<State>(1000) { score1, score2 -> -score1.compare(score2) }
        queue.add(initial)

        var iteration = 0
        while (queue.peek().pos != exit) {
            val top = queue.remove()
            val topPos = top.pos

            val blizzards =  top.blizzardsMove(ArrayList(top.blizzards), data)
            val points = blizzards.map { it.pos }.toSet()

            // cannot step into a blizzard
            val neighbors = data.neighbors(topPos).filter { !points.contains(it) }
            neighbors.forEach { neighbor ->
                queue.add(top.moveTo(neighbor, blizzards))
            }

            // cannot wait if the blizzard is going to kill us
            if (!points.contains(topPos)) {
                queue.add(top.wait(blizzards))
            }

            ++iteration
            if ((iteration % 1000) == 0) {
                println("Iteration $iteration top=$topPos score=${top.score} queue=${queue.size}")
            }
        }
        return queue.peek().minute
    }
    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

