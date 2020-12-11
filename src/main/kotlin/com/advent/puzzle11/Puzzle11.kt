package com.advent.puzzle11

import java.io.File

typealias State = List<String>

fun main() {
    val puzzle = Puzzle11()
    try {
        val state = puzzle.readInputs("inputs.txt")
        val stable = puzzle.findStableState(state) { puzzle.findNextState(it) }
        println(puzzle.countOccupied(stable))

        val stable2 = puzzle.findStableState(state) { puzzle.findNextState2(it) }
        println(puzzle.countOccupied(stable2))
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle11 {
    fun readInputs(filename: String): State {
        val file = File(filename)
        val lines = file.readLines()
        return lines
    }

    fun findStableState(state: State, f: (State) -> State) : State {
        var thisState = state
        var nextState = state

        var found = false
        while (!found) {
            thisState = nextState
            nextState = f(thisState)
            found = nextState == thisState
        }

        return thisState
    }

    fun findNextState(state: State) : State {
        val newState = ArrayList<String>()
        for (x in state.indices) {
            val str = state[x]
            var newStr = ""
            for (y in str.indices) {
                val adjacentSeats = countAdjacentSeats(x, y, state)
                val newCh =
                    when (str[y]) {
                        'L' -> if (adjacentSeats == 0) '#' else 'L'
                        '#' -> if (adjacentSeats >= 4) 'L' else '#'
                        else -> str[y]
                    }
                newStr += newCh
            }
            newState.add(newStr)
        }
        return newState
    }

    fun findNextState2(state: State) : State {
        val newState = ArrayList<String>()
        for (x in state.indices) {
            val str = state[x]
            var newStr = ""
            for (y in str.indices) {
                val adjacentSeats = countSeenSeats(x, y, state)
                val newCh =
                    when (str[y]) {
                        'L' -> if (adjacentSeats == 0) '#' else 'L'
                        '#' -> if (adjacentSeats >= 5) 'L' else '#'
                        else -> str[y]
                    }
                newStr += newCh
            }
            newState.add(newStr)
        }
        return newState
    }

    private fun countSeenSeats(x: Int, y: Int, state: State): Int {
        return occupiedInLine(x, y, 0, 1, state) +
            occupiedInLine(x, y, 1, 0, state) +
            occupiedInLine(x, y, 0, -1, state) +
            occupiedInLine(x, y, -1, 0, state) +
            occupiedInLine(x, y, 1, 1, state) +
            occupiedInLine(x, y, 1, -1, state) +
            occupiedInLine(x, y, -1, 1, state) +
            occupiedInLine(x, y, -1, -1, state)
    }

    private fun occupiedInLine(x: Int, y: Int, xInc: Int, yInc: Int, state: List<String>): Int {
        val maxX = state.size
        val maxY = state[0].length
        var testX = x + xInc
        var testY = y + yInc
        while (testX in 0 until maxX && testY in 0 until maxY) {
            val chair = chair(testX, testY, state)
            if (chair == 'L') {
                return 0
            } else if (chair == '#') {
                return 1
            }
            testX += xInc
            testY += yInc
        }
        return 0
    }

    private fun countAdjacentSeats(x: Int, y: Int, state: State): Int {
        return occupied(x - 1, y, state) +
            occupied(x - 1, y - 1, state) +
            occupied( x - 1, y + 1, state) +
            occupied(x, y - 1, state) +
            occupied( x, y + 1, state) +
            occupied(x + 1, y - 1, state) +
            occupied( x + 1, y, state) +
            occupied( x + 1, y + 1, state)
    }

    private fun chair(x: Int, y: Int, state: State) : Char {
        return if (x in state.indices && y in state[x].indices) {
            state[x][y]
        } else {
            ' '
        }
    }

    private fun occupied(x: Int, y: Int, state: State): Int {
        return if (chair(x, y, state) == '#') return 1 else 0
    }

    fun countOccupied(state: State) : Int {
        var ret = 0
        for (x in state.indices) {
            val str = state[x]
            for (y in str.indices) {
                ret += occupied(x, y, state)
            }
        }
        return ret
    }
}
