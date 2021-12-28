package com.advent2021.puzzle24

import com.advent2021.base.Base

class Data(
    val instructions: ArrayList<Instruction> = ArrayList(),
    val symbolTable: SymbolTable = SymbolTable()
) {
    init {
        ('w'..'z').forEach { symbolTable.assignVar(it.toString(), Number(0)) }
    }
}
typealias Solution = String
typealias Solution2 = Solution


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
        val symbolTable = data.symbolTable
        val parts = line.split(" ")
        val dest = symbolTable.getVar(parts[1])
        val value = if (parts.size <= 2) {
            null
        } else {
            val num = parts[2].toIntOrNull()
            if (num != null) {
                Number(parts[2].toInt())
            } else {
                symbolTable.getVar(parts[2])
            }
        }

        val instruction = when (parts[0]) {
            "inp" -> {
                InputInstruction(dest, symbolTable.createInputVar())
            }
            "add" -> {
                Add(dest, value!!)
            }
            "div" -> {
                Divide(dest, value!!)
            }
            "mul" -> {
                Multiply(dest, value!!)
            }
            "mod" -> {
                Mod(dest, value!!)
            }
            "eql" -> {
                Equal(dest, value!!)
            }
            else -> throw IllegalArgumentException("Did not recognize $parts")
        }
        data.instructions.add(instruction)
    }

    override fun computeSolution(data: Data): Solution {
        val symbolTable = SymbolTable()

        for (instruction in data.instructions) {
            instruction.execute(symbolTable)
        }
        // terminates with z = 0
        Equal(symbolTable.getVar("z"), Number(0)).execute(symbolTable)

        // after all the instructions, the max num is the maximum of each digit of the input
        var solution = ""
        for (inputVar in symbolTable.inputs) {
            solution += inputVar.maxCouldBe()
        }
        return solution
    }
    override fun computeSolution2(data: Data): Solution2 {
        var solution = ""
        return solution
    }
}

