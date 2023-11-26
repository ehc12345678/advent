package com.advent2019.puzzle2

import com.advent2019.base.Base

typealias Data = ArrayList<Int>
typealias Solution = Int
typealias Solution2 = Solution

// https://adventofcode.com/2019/day/1
fun main() {
    try {
        val puz = Puzzle2()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle2 : Base<Data, Solution?, Solution2?>() {
    override fun readInput(filename: String, data: Data, parseLineFunc: (String, Data) -> Unit): Data {
        val readInput = super.readInput(filename, data, parseLineFunc)
        data[1] = 12
        data[2] = 2
        return readInput
    }

    override fun parseLine(line: String, data: Data) {
        data.addAll(line.split(",").map { it.toInt() })
    }

    override fun computeSolution(data: Data): Solution {
        var position = 0
        while (data[position] != 99) {
            val operand1 = data[data[position + 1]]
            val operand2 = data[data[position + 2]]
            val store = data[position + 3]
            val answer = if (data[position] == 1) {
                operand1 + operand2
            } else {
                operand1 * operand2
            }
            data[store] = answer
            position += 4
        }
        return data[0]
    }

    override fun computeSolution2(data: Data): Solution2 {
        for (noun in 0..99) {
            for (verb in 0..99) {
                val thisData = Data(data)
                thisData[1] = noun
                thisData[2] = verb

                val answer = computeSolution(thisData)
                if (answer == 19690720) {
                    return 100 * noun + verb
                }
            }
        }
        return 0
    }
}

