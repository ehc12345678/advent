package com.advent2022.puzzle5

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayDeque

typealias CrateStack = ArrayDeque<Char>

data class Instruction(val numCrates: Int, val from: Int, val to: Int)

class Data {
  val stacks = ArrayList<CrateStack>()
  val instructions = ArrayList<Instruction>()
}
typealias Solution = String
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle5()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")
        puz.crateStack = true

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle5 : Base<Data, Solution?, Solution2?>() {
    var crateStack = true
    override fun parseLine(line: String, data: Data) {
        if (line.isBlank()) {
            crateStack = false
        } else if (crateStack) {
            var index = 0
            while (index * 4 + 3 <= line.length) {
                val crate = line.substring(index * 4, index * 4 + 3)
                if (crate.startsWith("[")) {
                    val letter = crate[1]
                    while (index >= data.stacks.size) {
                        data.stacks.add(CrateStack())
                    }
                    data.stacks[index].add(letter)
                }
                index++
            }
        } else {
            val numCrates = line.substringAfter("move ").substringBefore(" ").toInt()
            val from = line.substringAfter(" from ").substringBefore(" ").toInt()
            val to = line.substringAfter(" to ").toInt()
            data.instructions.add(Instruction(numCrates, from, to))
        }
    }

    override fun computeSolution(data: Data): Solution {
        data.instructions.forEach { executeInstruction(data, it) }
        return data.stacks.fold("") { acc, crateStack ->
            if (crateStack.isNotEmpty()) {
                acc + crateStack.first()
            } else {
                acc
            }
        }
    }

    override fun computeSolution2(data: Data): Solution2 {
        data.instructions.forEach { executeInstruction2(data, it) }
        return data.stacks.fold("") { acc, crateStack ->
            if (crateStack.isNotEmpty()) {
                acc + crateStack.first()
            } else {
                acc
            }
        }
    }

    private fun executeInstruction(data: Data, instruction: Instruction) {
        repeat(instruction.numCrates) {
            val pop = data.stacks[instruction.from - 1].removeFirst()
            data.stacks[instruction.to - 1].addFirst(pop)
        }
    }

    private fun executeInstruction2(data: Data, instruction: Instruction) {
        val stackToMove = ArrayList<Char>()
        repeat(instruction.numCrates) {
            val pop = data.stacks[instruction.from - 1].removeFirst()
            stackToMove.add(pop)
        }
        stackToMove.reversed().forEach { data.stacks[instruction.to - 1].addFirst(it) }
    }
}

