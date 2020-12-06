package com.advent.puzzle5

import java.io.File

fun main() {
    val puzzle = Puzzle5()
    val inputs = puzzle.readInputs("inputs.txt")
    try {
        println(puzzle.lineToId("FBFBBFFRLR"))
        val ids = inputs.map { puzzle.lineToId(it) }.toSet()
        val maxId = ids.max()
        println("Max id = $maxId")

        val seat = puzzle.findYourSeat(ids, 1, maxId!!)
        println("Found your seat here: $seat")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle5 {
    fun readInputs(filename: String): List<String> {
        val file = File(filename)
        return file.readLines()
    }

    fun lineToId(line: String) : Int {
        val row = getBinary(line.subSequence(0..6), 'B', 0, 127)
        val col = getBinary(line.substring(line.length - 3), 'R', 0, 7)
        return (row * 8) + col
    }

    fun getBinary(str: CharSequence, upper: Char, rangeMin: Int, rangeMax: Int) : Int {
        if (str.isEmpty()) {
            return rangeMin
        }
        val midPoint = (rangeMax + rangeMin) / 2
        var nextMin = rangeMin
        var nextMax = rangeMax
        if (str[0] == upper) {
            nextMin = midPoint + 1
        } else {
            nextMax = midPoint
        }
        return getBinary(str.substring(1), upper, nextMin, nextMax)
    }

    fun findYourSeat(ids: Set<Int>, min: Int, max: Int) : Int {
        for (i in min..max) {
            if (!ids.contains(i) && ids.contains(i - 1) && ids.contains(i + 1)) {
                return i
            }
        }
        return -1
    }
}