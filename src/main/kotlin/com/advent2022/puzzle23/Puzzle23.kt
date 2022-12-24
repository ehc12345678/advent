package com.advent2022.puzzle23

import com.advent2021.base.Base

typealias Solution = Int
typealias Solution2 = Solution

data class Pos(var x: Int, var y: Int) {
    operator fun plus(p: Pos) = Pos(x + p.x, y + p.y)
    fun N() = this + Pos(0, -1)
    fun S() = this + Pos(0, 1)
    fun E() = this + Pos(1, 0)
    fun W() = this + Pos(-1, 0)

    fun NE() = this + Pos(1, -1)
    fun NW() = this + Pos(-1, -1)
    fun SE() = this + Pos(1, 1)
    fun SW() = this + Pos(-1, 1)

    fun inDirection(compass: Compass) = when (compass) {
        Compass.N -> N()
        Compass.S -> S()
        Compass.W -> W()
        Compass.E -> E()
    }
}
enum class Compass { N, S, W, E }
class Data {
    var rows: Int = 0
    val elves = HashSet<Pos>()
    fun findNeighbors(elf: Pos): List<Pos> =
        neighborsIn(elf.N(), elf.NE(), elf.NW(), elf.E(), elf.S(), elf.SE(), elf.SW(), elf.W())

    fun neighbors(compass: Compass, elf: Pos) = when (compass) {
        Compass.N -> neighborsIn(elf.N(), elf.NW(), elf.NE())
        Compass.S -> neighborsIn(elf.S(), elf.SW(), elf.SE())
        Compass.W -> neighborsIn(elf.W(), elf.NW(), elf.SW())
        Compass.E -> neighborsIn(elf.E(), elf.NE(), elf.SE())
    }
    fun neighborsIn(vararg pos: Pos): List<Pos> {
        return pos.filter { elves.contains(it) }
    }

    val minX: Int
        get() = elves.minOf { it.x }
    val maxX: Int
        get() = elves.maxOf { it.x }
    val minY: Int
        get() = elves.minOf { it.y }
    val maxY: Int
        get() = elves.maxOf { it.y }

}

fun main() {
    try {
        val puz = Puzzle23()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle23 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val y = data.rows++
        line.toCharArray().forEachIndexed { index, c ->
            if (c == '#') {
                data.elves.add(Pos(index, y))
            }
        }
    }

    override fun computeSolution(data: Data): Solution {
        // start and end position
        val dirs = mutableListOf(Compass.N, Compass.S, Compass.W, Compass.E)
        var round = 0
        while (doOneRound(data, dirs) && round < 10) {
            // do a round... go nuts
            // printState(data)
            round++
        }
        val minX = data.minX
        val maxX = data.maxX
        val minY = data.minY
        val maxY = data.maxY
        val area = (maxX - minX + 1) * (maxY - minY + 1)

        // empty spots are the total area minus the elves
        return area - data.elves.size
    }

    private fun doOneRound(
        data: Data,
        dirs: MutableList<Compass>
    ): Boolean {
        val moves = ArrayList<Pair<Pos, Pos>>()
        data.elves.forEach { elf ->
            if (data.findNeighbors(elf).isNotEmpty()) {
                val dir = dirs.find { data.neighbors(it, elf).isEmpty() }
                if (dir != null) {
                    moves.add(Pair(elf, elf.inDirection(dir)))
                }
            }
        }

        if (moves.isEmpty()) {
            return false
        }

        // do the moves
        while (moves.isNotEmpty()) {
            val move = moves.removeLast()
            val otherMove = moves.find { it.second == move.second }

            // can only move if no one else does
            if (otherMove == null) {
                data.elves.remove(move.first)
                data.elves.add(move.second)
            } else {
                moves.remove(otherMove)
            }
        }

        // rotate!
        dirs.add(dirs.removeFirst())
        return true
    }

    override fun computeSolution2(data: Data): Solution2 {
        // start and end position
        val dirs = mutableListOf(Compass.N, Compass.S, Compass.W, Compass.E)
        var round = 0
        while (doOneRound(data, dirs)) {
            // do a round... go nuts
            // printState(data)
            round++
        }

        // empty spots are the total area minus the elves
        return round + 1
    }

    fun printState(data: Data) {
        for (y in data.minY..data.maxY) {
            for (x in data.minX..data.maxX) {
                if (data.elves.contains(Pos(x, y))) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
        println()
        println()
    }
}

