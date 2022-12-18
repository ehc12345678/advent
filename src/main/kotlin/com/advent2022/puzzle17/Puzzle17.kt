package com.advent2022.puzzle17

import com.advent2021.base.Base
import kotlin.math.max

data class Point(val x: Long, var y: Long) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)
}
class RockShape(str: String) {
    val pieces: Set<Point>
    val height: Int
    init {
        val lines = str.split("\n").mapIndexed { row, line ->
            line.mapIndexed { col, ch ->
                if (ch == '#') Point(col.toLong(), row.toLong()) else null
            }.filterNotNull()
        }
        height = lines.size
        pieces = lines.flatten().toSet()
    }
}
class Rock(val shape: RockShape, val upperLeft: Point) {
    val pieces: Set<Point>
        get() = shape.pieces.map { upperLeft + Point(it.x, -it.y) }.toSet()
}

val LINE = RockShape("####")
val CROSS = RockShape(" # \n###\n # ")
val ELBOW = RockShape("  #\n  #\n###")
val DOWN_LINE = RockShape("#\n#\n#\n#")
val BLOCK = RockShape("##\n##")
val ALL_SHAPES = listOf(LINE, CROSS, ELBOW, DOWN_LINE, BLOCK)

enum class DIR { LEFT, RIGHT }
typealias Data = ArrayList<DIR>
typealias Solution = Long
typealias Solution2 = Long

class State {
    var fallingRock: Rock? = null
    val atRest = HashSet<Point>()
    var currentShape = 0
    var pushPosition = 0
    var floor = 0
    var currentHeight = 0L
    var maxHeight = 0L

    fun overlaps(rock: Rock) = atRest.intersect(rock.pieces).isNotEmpty()
    fun addAll(rock: Rock) {
        atRest.addAll(rock.pieces)
        currentHeight = max(currentHeight, rock.pieces.maxOf { it.y + 1 })
        maxHeight = max(currentHeight, fallingRock?.upperLeft?.y ?: 0)
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
    val verbose = false
    override fun parseLine(line: String, data: Data) {
        data.addAll(line.map { if (it == '<') DIR.LEFT else DIR.RIGHT })
    }

    override fun computeSolution(data: Data): Solution {
        val state = State()
        val numTurns = 2022
        for (turn in 0 until numTurns) {
            doTurn(state, data)
        }
        return state.currentHeight
    }

    override fun computeSolution2(data: Data): Solution2 {
        val state = State()
        var numTurns = 0L
        var findTheRepeat: Pair<Int, Int>? = null
        val added = ArrayList<Long>()
        var lastHeight = 0L
        while (findTheRepeat == null) {
            doTurn(state, data)
            added.add(state.currentHeight - lastHeight)
            lastHeight = state.currentHeight
            ++numTurns

            if ((numTurns % 5000L) == 0L) {
                findTheRepeat = findRepeat(added, 500)
                println("Find the floor at $numTurns is $findTheRepeat")
            }
        }

        val totalTurnsToCalc = 1000000000000L
        val repeatStart = findTheRepeat.first
        val repeatLen = findTheRepeat.second
        val repeats = (totalTurnsToCalc - repeatStart) / repeatLen.toLong()

        val firstList = added.subList(0, repeatStart)
        val repeatList = added.subList(repeatStart, repeatStart + repeatLen)

        val leftOver = (totalTurnsToCalc - repeatStart - repeats * repeatLen).toInt()
        val endList = added.subList(repeatStart, repeatStart + leftOver)

        return firstList.sum() + (repeats * repeatList.sum()) + endList.sum()
    }

    fun doTurn(state: State, data: Data) {
        dropRock(state)
        while (state.fallingRock != null) {
            pushFalling(data[state.pushPosition], state)
            state.pushPosition = (state.pushPosition + 1) % data.size
            doFall(state)
        }
    }

    fun dropRock(state: State) {
        if (state.fallingRock == null) {
            val current = ALL_SHAPES[(state.currentShape++ % ALL_SHAPES.size)]
            val pos = Point(2, state.maxHeight + current.height + 2)
            state.fallingRock = Rock(current, pos)
            printState("Rock begins falling at $pos:", state)
        }
    }

    fun pushFalling(dir: DIR, state: State) {
        val nudge = if (dir == DIR.LEFT) Point(-1, 0) else Point(1, 0)
        val rock = state.fallingRock!!
        val rockPush = Rock(rock.shape, rock.upperLeft + nudge)
        if (canPlace(rockPush, state)) {
            state.fallingRock = rockPush
            printState("Jet of gas pushes rock $dir", state)
        } else {
            printState("Jet of gas pushes rock $dir, but nothing happens", state)
        }
    }

    private fun doFall(state: State) {
        val nudge = Point(0, -1)
        val rock = state.fallingRock!!
        val rockPush = Rock(rock.shape, rock.upperLeft + nudge)
        if (canPlace(rockPush, state)) {
            state.fallingRock = rockPush
            printState("Rock falls 1 unit:", state)
        } else {
            state.addAll(state.fallingRock!!)
            state.fallingRock = null
            printState("Rock falls 1 unit, causing it to come to rest:", state)
        }
    }

    private fun canPlace(rock: Rock, state: State): Boolean {
        val pieces = rock.pieces
        return !(pieces.any { it.y < state.floor || it.x !in 0 until 7 } || state.overlaps(rock))
    }

    fun printState(label: String, state: State) {
        if (!verbose) return
        
        println(label)
        val maxHeight = state.maxHeight
        for (y in maxHeight downTo 0L) {
            print("|")
            for (x in 0 until 7) {
                val pos = Point(x.toLong(), y)
                if (state.fallingRock?.pieces?.contains(pos) == true) {
                    print("@")
                } else if (state.atRest.contains(pos)) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println("|")
        }
        println()
    }

    fun findRepeat(added: ArrayList<Long>, tryLen: Int): Pair<Int, Int> {
        var len = tryLen
        while (len < added.size / 2) {
            var start = 0
            while (start + len * 2 <= added.size) {
                if (compareSubLists(added, start, len + start, len + start, start + len * 2)) {
                    return Pair(start, len)
                }
                ++start
            }
            ++len
        }
        return Pair(-1, len)
    }

    fun compareSubLists(added: ArrayList<Long>, low1: Int, high1: Int, low2: Int, high2: Int): Boolean {
        if (high1 - low1 != high2 - low2) {
            throw IllegalArgumentException()
        }
        var i1 = low1
        var i2 = low2
        while (i1 < high1) {
            if (added[i1++] != added[i2++]) {
                return false
            }
        }
        return true
    }
}

