package com.advent.advent2020.puzzle14

import java.io.File
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.util.Stack

fun main() {
    val puzzle = Puzzle14()
    try {
        val inputs = puzzle.readInputs("inputs.txt")
        val sum = puzzle.run(inputs)
        println("Sum is $sum")

        val sum2 = puzzle.run2(inputs)
        println("Sum2 is $sum2")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle14 {
    fun readInputs(filename: String): List<String> {
        val file = File(filename)
        return file.readLines()
    }

    fun run(inputs: List<String>) : BigInteger {
        var mask = ""
        val numberMap = HashMap<String, Long>() // memory address to contents
        inputs.forEach {
            if (it.startsWith("mask = ")) {
                mask = it.substring("mask = ".length)
            } else {
                val matchResult = """mem\[(\d*)] = (\d*)""".toRegex().find(it) ?: throw IllegalArgumentException("Bad value $it")
                val memAddress = matchResult.groups[1]!!.value
                val value = matchResult.groups[2]!!.value.toLong()
                val newValue = applyMask(value, mask)
                numberMap[memAddress] = newValue
            }
        }
        return numberMap.values.fold(BigInteger.ZERO) { acc, it -> acc + BigInteger.valueOf(it) }
    }

    fun run2(inputs: List<String>) : BigInteger {
        var mask = ""
        val numberMap = HashMap<String, Long>() // memory address to contents
        inputs.forEach {
            if (it.startsWith("mask = ")) {
                mask = it.substring("mask = ".length)
            } else {
                val matchResult = """mem\[(\d*)] = (\d*)""".toRegex().find(it) ?: throw IllegalArgumentException("Bad value $it")
                val memAddress = matchResult.groups[1]!!.value
                val value = matchResult.groups[2]!!.value.toLong()
                applyMask2(value, mask, numberMap, memAddress.toLong())
            }
        }
        return numberMap.values.fold(BigInteger.ZERO) { acc, it -> acc + BigInteger.valueOf(it) }
    }

    private fun applyMask2(value: Long, mask: String, numberMap: java.util.HashMap<String, Long>, memAddress: Long) {
        val modMemAddress = applyMask2(memAddress, mask)
        val stackWithMasks = Stack<String>()
        stackWithMasks.push(modMemAddress)

        val allMemoryAddresses = HashSet<String>()
        while (!stackWithMasks.isEmpty()) {
            val top = stackWithMasks.pop()
            if (top.contains('X')) {
                val index = top.indexOf('X')
                val withZero = top.substring(0 until index) + "0" + top.substring(index + 1)
                val withOne = top.substring(0 until index) + "1" + top.substring(index + 1)
                stackWithMasks.push(withZero)
                stackWithMasks.push(withOne)
            } else {
                allMemoryAddresses.add(top)
            }
        }

        allMemoryAddresses.forEach {
            numberMap[fromStrToBinary(it).toString()] = value
        }
    }

    fun applyMask(value: Long, mask: String) : Long {
        var newValue = value
        for (i in mask.indices) {
            val bitMask = 1L shl i
            when (mask[mask.length - i - 1]) {
                '0' -> {
                    newValue = newValue and bitMask.inv()
                }
                '1' -> {
                    newValue = newValue or bitMask
                }
            }
        }
        return newValue
    }

    fun applyMask2(value: Long, mask: String) : String {
        var newValue = ""
        val existing = toBinaryStr(value)
        for (i in mask.indices) {
            when (mask[i]) {
                '0', -> {
                    newValue += existing[i]
                }
                '1' -> {
                    newValue += 1
                }
                'X' -> {
                    newValue += 'X'
                }
            }
        }
        return newValue
    }

    fun toBinaryStr(x: Long): String {
        val result = StringBuilder()
        val len = 36
        for (i in len - 1 downTo 0) {
            val mask = 1L shl i
            result.append(if (x and mask != 0L) 1 else 0)
        }
        return result.toString()
    }

    fun fromStrToBinary(str: String): Long {
        var result = 0L
        for (i in str.indices) {
            if (str[i] == '1') {
                val mask = 1L shl (str.length - i - 1)
                result = result or mask
            }
        }
        return result
    }
}
