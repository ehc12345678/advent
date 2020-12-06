package com.advent.puzzle1

import java.io.File

fun main() {
    val findSums2020 = FindSums2020()
    try {
        val sum = 2020
        val inputs = findSums2020.readInputs("inputs.txt")
        val pair = findSumOperands(inputs, sum)
        if (pair != null) {
            println("Found a pair that adds to $sum: ${pair.first} and ${pair.second}, product is (${pair.first * pair.second})")
        }

        val triple = findSumThreeOperands(inputs, sum)
        if (triple != null) {
            println("Found a trips that adds to $sum: $triple, products is (${triple.reduce {acc, it -> acc * it }})")
        }
    } catch (ex: Exception) {
        print(ex)
    }
}

private fun findSumOperands(inputs: Set<Int>, sum: Int) : Pair<Int, Int>? {
    inputs.forEach {
        val diff = sum - it
        if (inputs.contains(diff)) {
            return Pair(it, diff)
        }
    }
    return null
}

private fun findSumThreeOperands(inputs: Set<Int>, sum: Int) : Set<Int>? {
    inputs.forEach {
        val diff = sum - it;
        val otherPair = findSumOperands(inputs, diff)
        if (otherPair != null) {
            return setOf(it, otherPair.first, otherPair.second)
        }
    }
    return null
}

class FindSums2020 {
    fun readInputs(filename: String) : Set<Int> {
        val file = File(filename)
        return file.readLines().map { it.toInt() }.toSet()
    }
}