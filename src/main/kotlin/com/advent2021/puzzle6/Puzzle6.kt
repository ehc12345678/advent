package com.advent2021.puzzle6

import com.advent2021.base.Base
import java.math.BigInteger

typealias Data = ArrayList<Int>
typealias Solution = BigInteger
typealias Solution2 = BigInteger

fun main() {
    try {
        val puz = Puzzle6()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle6 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.addAll(line.split(",").map { it.toInt() })
    }

    override fun computeSolution(data: Data): Solution = solution(data, 80)
    override fun computeSolution2(data: Data): Solution2 = solution(data, 256)

    private fun solution(data: Data, days: Int): BigInteger {
        val cnts = Array(9) { 0.toBigInteger() }
        data.forEach { cnts[it]++ }
        for (i in 0 until days) {
            step1(cnts)
        }
        return cnts.sumOf { it }
    }

    private fun step1(sol: Array<BigInteger>) {
        val zero = sol[0]
        for (i in 1 until sol.size) {
            sol[i - 1] = sol[i]
        }
        sol[sol.size - 1] = zero
        sol[6] += zero
    }
}

