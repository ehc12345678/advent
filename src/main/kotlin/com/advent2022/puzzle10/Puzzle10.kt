package com.advent2022.puzzle10

import com.advent2021.base.Base

class Cpu(var x: Int = 1, var tick: Int = 1) {
    var part1Acc: Long = 0
    val part2Acc = ArrayList<String>()

    fun doTick() {
        if (((tick - 20) % 40) == 0) {
            part1Acc += (x * tick)
        }
        
        val posInRow = (tick - 1) % 40
        if (posInRow == 0) {
            part2Acc.add("")
        }

        var str = part2Acc.removeLast()
        str += if (x in (posInRow - 1)..(posInRow + 1)) "#" else " "
        part2Acc.add(str)
        ++tick
    }

    fun addX(newX: Int) {
        doTick()
        doTick()
        x += newX
    }

    fun noop() {
        doTick()
    }
}
typealias Data = ArrayList<Int?>
typealias Solution = Long
typealias Solution2 = String

fun main() {
    try {
        val puz = Puzzle10()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: \n$solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle10 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val parts = line.split(" ")
        if (parts[0] == "addx") {
            data.add(parts[1].toInt())
        } else {
            data.add(null)
        }
    }

    override fun computeSolution(data: Data): Solution {
        return runInstuctions(data).part1Acc
    }

    override fun computeSolution2(data: Data): Solution2 {
        val cpu = runInstuctions(data)
        return cpu.part2Acc.joinToString("\n")
    }

    private fun runInstuctions(data: Data): Cpu {
        val cpu = Cpu()
        data.forEach {
            if (it == null) {
                cpu.noop()
            } else {
                cpu.addX(it)
            }
        }
        return cpu
    }
}

