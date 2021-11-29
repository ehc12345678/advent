package com.advent.advent2020.puzzle9

import com.advent.advent2020.puzzle8.Instruction
import java.io.File
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.util.Stack

fun main() {
    val puzzle = Puzzle9()
    try {
        val nums = puzzle.readInputs("inputs.txt")
        val first = puzzle.findFirst(25, 25, nums)
        println("First is $first")

        val second = puzzle.findSecond(first, nums)
        println("Second is $second")

    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle9 {
    fun readInputs(filename: String): List<BigDecimal> {
        val file = File(filename)
        val lines = file.readLines().map { it.toBigDecimal() }
        return lines
    }

    fun findFirst(index: Int, lastN: Int, nums: List<BigDecimal>): Pair<BigDecimal, Int> {
        if (index + lastN > nums.size) {
            throw IllegalArgumentException("Not found")
        }
        val first = index - lastN
        val last = first + lastN
        val thisSet = HashSet(nums.subList(first, last))
        val find = findSumOperands(thisSet, nums[index])
        if (find == null) {
            return Pair(nums[index], index)
        }
        return findFirst(index + 1, lastN, nums)
    }

    private fun findSumOperands(inputs: Set<BigDecimal>, sum: BigDecimal) : Pair<BigDecimal, BigDecimal>? {
        inputs.forEach {
            val diff = sum - it
            if (inputs.contains(diff) && diff != it) {
                return Pair(it, diff)
            }
        }
        return null
    }

    fun findSecond(first: Pair<BigDecimal, Int>, nums: List<BigDecimal>): BigDecimal? {
        val target = first.first
        var index = first.second
        var findConsecutive: BigDecimal?
        do {
            findConsecutive = tryConsecutive(nums, index - 1, target)
            --index
        } while (findConsecutive == null)

        return findConsecutive
    }

    fun tryConsecutive(num: List<BigDecimal>, index: Int, target: BigDecimal) : BigDecimal? {
        var sum = target

        var i = index
        val list = ArrayList<BigDecimal>()
        while (i > 0 && sum > BigDecimal(0)) {
            sum -= num[i]
            list.add(num[i])
            --i
        }
        if (sum == BigDecimal.ZERO) {
            return list.minOrNull()?.add(list.maxOrNull())
        } else {
            return null
        }
    }
}
