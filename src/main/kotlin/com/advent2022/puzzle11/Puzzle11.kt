package com.advent2022.puzzle11

import com.advent2021.base.Base
import java.math.BigInteger

typealias ItemOp = (old: BigInteger) -> BigInteger
class Monkey {
    var numItemsInspected = 0

    val items = ArrayList<BigInteger>()
    var op: ItemOp = { it }
    var divisibleBy = 1L
    var trueIndex = 0
    var falseIndex = 0

    fun inspectItem(item: BigInteger, data: Data, worryReduction: Boolean = true) {
        var afterOp = op(item)
        if (worryReduction) {
            afterOp /= BigInteger.valueOf(3)
        }
        ++numItemsInspected

        val index = if ((afterOp % divisibleBy.toBigInteger()) == BigInteger.ZERO) trueIndex else falseIndex
        data.monkies[index].items.add(afterOp)
    }

}
class Data {
    val monkies = ArrayList<Monkey>()

    fun addMonkey() {
        monkies.add(Monkey())
    }

    fun last() = monkies.last()
}
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle11()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputsTest.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle11 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val trimmed = line.trim()
        when {
            trimmed.startsWith("Monkey") -> data.addMonkey()
            trimmed.startsWith("Starting items:") -> {
                val items = trimmed.substringAfter("Starting items: ").split(", ").map { it.toBigInteger() }
                data.last().items.addAll(items)
            }
            trimmed.startsWith("Operation:") -> {
                val parts = trimmed.substringAfter("new = old ").split(" ")
                data.last().op = when (parts[0]) {
                    "*" -> if (parts[1] == "old") { item -> item * item } else { item -> item * parts[1].toBigInteger() }
                    "+" -> if (parts[1] == "old") { item -> item + item } else { item -> item + parts[1].toBigInteger() }
                    else -> throw IllegalArgumentException("Don't know op ${parts[0]}")
                }
            }
            trimmed.startsWith("Test:") -> {
                data.last().divisibleBy = trimmed.substringAfter("divisible by ").toLong()
            }
            trimmed.startsWith("If true: ") -> {
                data.last().trueIndex = trimmed.substringAfter("throw to monkey ").toInt()
            }
            trimmed.startsWith("If false: ") -> {
                data.last().falseIndex = trimmed.substringAfter("throw to monkey ").toInt()
            }
        }
    }

    override fun computeSolution(data: Data): Solution {
        return compute(data, true, 20)
    }
    
    override fun computeSolution2(data: Data): Solution2 {
        return compute(data, false, 10000)
    }

    private fun compute(data: Data, worryReduction: Boolean, numTimes: Int): Int {
        repeat(numTimes) {
            if ((it % 1000) == 0) {
                println("== After round $it")
                data.monkies.forEachIndexed { index, monkey -> println("Monkey $index inspected ${monkey.numItemsInspected} times.")}
                println()
            }
            data.monkies.forEach { monkey ->
                doRoundWithMonkey(data, monkey, worryReduction)
            }
        }
        val sorted = data.monkies.sortedByDescending { it.numItemsInspected }
        return sorted[0].numItemsInspected * sorted[1].numItemsInspected
    }

    private fun doRoundWithMonkey(data: Data, monkey: Monkey, worryReduction: Boolean) {
        while (monkey.items.isNotEmpty()) {
            val item = monkey.items.removeFirst()
            monkey.inspectItem(item, data, worryReduction)
        }
    }
}

