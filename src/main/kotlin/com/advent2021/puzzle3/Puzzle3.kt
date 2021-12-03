package com.advent2021.puzzle3

import com.advent2021.base.Base
import kotlin.collections.ArrayList

typealias Line = UInt
typealias Data = ArrayList<Line>
data class Solution(
    val gamma: UInt,
    val epsilon: UInt
) {
    fun solution(): UInt = gamma * epsilon
}
typealias Solution2 = UInt

fun main() {
    try {
        val puz = Puzzle3()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: ${solution1?.solution()}")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
class Puzzle3 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line.toUInt(2))
    }

    data class Counts(
        var cnt: Int = 0,
        var position: Int = 0
    )

    override fun computeSolution(data: Data): Solution {
        val counts = extractCounts(data)
        var gamma = 0U
        var epsilon = 0U
        for (i in 0 until BIT_LEN) {
            if (counts[i].cnt >= 0) {
                gamma = gamma or maskBit(i)
            } else {
                epsilon = epsilon or maskBit(i)
            }
        }
        return Solution(gamma, epsilon)
    }

    private fun extractCounts(data: Data): Array<Counts> {
        val counts = Array(BIT_LEN) { Counts() }

        data.forEach { line ->
            for (i in 0 until BIT_LEN) {
                counts[i].position = i
                counts[i].cnt += if (checkBit(line, i)) 1 else -1
            }
        }
        return counts
    }

    override fun computeSolution2(data: Data): Solution2 {
        val oxygen = searchFor(data) { it.gamma }
        val co2 = searchFor(data) { it.epsilon }
        return oxygen * co2
    }

    private fun maskBit(bit: Int) = 0x1U shl (BIT_LEN - bit - 1)
    private fun checkBit(u: UInt, bit: Int) = (maskBit(bit) and u) != 0U

    private fun searchFor(data: Data, solFunc: (sol: Solution) -> UInt): UInt {
        var newData: Data = ArrayList(data)
        var index = 0
        while (newData.size > 1) {
            val solution = computeSolution(newData)
            val mask = solFunc(solution)
            newData = Data(newData.filter { checkBit(mask, index) == checkBit(it, index) })
            ++index
        }
        return newData[0]
    }

    companion object {
        const val BIT_LEN = 12
    }

}