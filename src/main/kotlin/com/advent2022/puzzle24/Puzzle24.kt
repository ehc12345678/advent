package com.advent2022.puzzle24

import com.advent2021.base.Base
import java.util.*
import kotlin.math.abs

typealias Solution = Int
typealias Solution2 = Solution

data class Pos(var x: Int, var y: Int) {
    operator fun plus(p: Pos) = Pos(x + p.x, y + p.y)
    operator fun minus(p: Pos) = Pos(x - p.x, y - p.y)
    fun N() = this + Pos(0, -1)
    fun S() = this + Pos(0, 1)
    fun E() = this + Pos(1, 0)
    fun W() = this + Pos(-1, 0)

    fun inDirection(compass: Compass) = when (compass) {
        Compass.N -> N()
        Compass.S -> S()
        Compass.W -> W()
        Compass.E -> E()
    }
}
enum class Compass { N, S, W, E }
data class Blizzard(val pos: Pos, val compass: Compass) {
    fun posAtTurn(turn: Int, data: Data): Pos {
        val xMod = data.xRange.last
        val yMod = data.yRange.last

        var x = pos.x
        var y = pos.y
        when (compass) {
            Compass.N -> y -= (turn % yMod)
            Compass.S -> y += (turn % yMod)
            Compass.W -> x -= (turn % xMod)
            Compass.E -> x += (turn % xMod)
        }
        if (x < 1) {
            x += xMod
        }
        if (x > xMod) {
            x -= xMod
        }
        if (y < 1) {
            y += yMod
        }
        if (y > yMod) {
            y -= yMod
        }
        return Pos(x, y)
    }
}
class Data {
    var yPos = 0
    val blizzards = ArrayList<Blizzard>()
    val entrance = Pos(1,0)
    var exitPos: Pos? = null
    var xRange: IntRange = IntRange.EMPTY
    var yRange: IntRange = IntRange.EMPTY

    var exit: Pos
        get() = exitPos!!
        set(pos) {
            exitPos = pos
            xRange = 1..pos.x
            yRange = 1..pos.y - 1
        }

    fun addBlizzard(x: Int, y: Int, compass: Compass) {
        blizzards.add(Blizzard(Pos(x, y), compass))
    }

//    fun wrap(pos: Pos): Pos {
//        return when {
//            pos.x < xRange.first -> Pos(xRange.last, pos.y)
//            pos.x > xRange.last -> Pos(xRange.first, pos.y)
//            pos.y < yRange.first -> Pos(pos.x, yRange.last)
//            pos.y > yRange.last -> Pos(pos.x, yRange.first)
//            else -> pos
//        }
//    }

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

data class State(val pos: Pos, val endPos: Pos, val minute: Int = 0,
                 val distance: Int = manhattanDistance(pos, endPos),
                 val score: Int = minute + distance) {
    fun compare(other: State): Int {
        var cmp = -score.compareTo(other.score)
        if (cmp == 0) {
            // favor the one that is further along
            cmp = -distance.compareTo(other.distance)
        }
        return cmp
    }
    fun wait() = State(pos, endPos, minute + 1)
    fun moveTo(neighbor: Pos) = State(neighbor, endPos, minute + 1)
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

        val initial = State(data.entrance, exit, 0)
        val queue = PriorityQueue<State>(1000) { score1, score2 -> -score1.compare(score2) }
        queue.add(initial)

        val seen = HashSet<State>()

        var iteration = 0
        while (queue.isNotEmpty() && queue.peek().pos != exit) {
            val top = queue.remove()

            val next = nextStates(top, data)
            next.forEach { nextState ->
                // look one ahead.. if there are no states for that one, don't add it
                // and add the best looking ones
                if (!seen.contains(nextState)) {
                    seen.add(nextState)

                    val afterNext = nextStates(nextState, data)
                    if (afterNext.isNotEmpty()) {
                        queue.add(nextState)
                        val decentNeightbors = afterNext.filter { it.distance < top.distance }
                        decentNeightbors.forEach { nextNextState ->
                            if (!seen.contains(nextNextState)) {
                                queue.add(nextNextState)
                            }
                        }
                    }
                }
            }

            ++iteration
            if ((iteration % 1000) == 0) {
                println("Iteration $iteration top=${top} queue=${queue.size}")
            }
        }
        return queue.peek().minute
    }
    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }

    fun nextStates(state: State, data: Data): List<State> {
        val points = data.blizzards.map { it.posAtTurn(state.minute + 1, data) }.toSet()
        val neighbors = data.neighbors(state.pos).filter { !points.contains(it) }

        val ret = neighbors.map { neighbor -> state.moveTo(neighbor) }
        if (!points.contains(state.pos)) {
            return ret + state.wait()
        }
        return ret
    }
}

