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

    fun overlaps(rock: Rock) = atRest.intersect(rock.pieces).isNotEmpty()

    val currentHeight: Long
        get() = atRest.maxOfOrNull { it.y + 1 } ?: 0L
    val maxHeight: Long
        get() = max(currentHeight, fallingRock?.upperLeft?.y ?: 0)
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
        val repeat = detectRepeats(state, 10, 20)
        if (repeat != null) {
            println("Maybe a repeat")
        } else {
            println("Sadness")
        }
        return state.currentHeight
    }

    override fun computeSolution2(data: Data): Solution2 {
//        val state = State()
//        val numTurns = 1,000,000,000,000L
//        for (turn in 0 until numTurns) {
//            doTurn(state, data)
//        }
//        return state.currentHeight
        return 0
    }

    fun doTurn(state: State, data: Data) {
        dropRock(state)

        var x = 0
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
            state.atRest.addAll(state.fallingRock!!.pieces)
            state.fallingRock = null
            printState("Rock falls 1 unit, causing it to come to rest:", state)
        }
    }

    private fun canPlace(rock: Rock, state: State): Boolean {
        val pieces = rock.pieces
        return !(pieces.any { it.y < 0 || it.x !in 0 until 7 } || state.overlaps(rock))
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

    fun detectRepeats(state: State, firstRepeat: Long, floor: Long): Pair<LongRange, LongRange>? {
        var repeat = firstRepeat
        while (repeat < state.maxHeight / 2) {
            val yRangeFirst = floor..floor + repeat
            val yRangeNext = floor + repeat + 1..floor + repeat * 2
            if (yRangeNext.last > state.maxHeight) {
                null
            }
            val firstCells = state.atRest.filter { it.y in yRangeFirst }
            var nextCells = state.atRest.filter { it.y in yRangeNext }
            if (firstCells.size == nextCells.size) {
                nextCells = nextCells.map { it + Point(0, -repeat - 1) }
                if (firstCells == nextCells) {
                    return Pair(yRangeFirst, yRangeNext)
                }
            }
            ++repeat
        }
        return null
    }
}

