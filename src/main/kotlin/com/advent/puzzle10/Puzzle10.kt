package com.advent.puzzle9

import java.io.File

data class Counts(
    var count1: Int,
    var count2: Int,
    var count3: Int
)

fun main() {
    val puzzle = Puzzle10()
    try {
        val nums = puzzle.readInputs("inputs.txt")
        nums.add(nums.last() + 3)
        nums.forEach { println(it) }
        val all = HashSet(nums)
        val counts = Counts(0, 0, 0)
        puzzle.findPath(0, all, counts)
        println("Value is ${counts.count1.toLong() * counts.count3.toLong()}")

    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle10 {
    fun readInputs(filename: String): ArrayList<Int> {
        val file = File(filename)
        val lines = file.readLines().map { it.toInt() }.sorted()
        return ArrayList(lines)
    }

    fun findPath(current: Int, all: HashSet<Int>, counts: Counts): Boolean {
        if (all.isEmpty()) {
            return true
        }
        val plusOne = current + 1
        val plusTwo = current + 2
        val plusThree = current + 3
        var found = false
        if (all.remove(plusOne)) {
            counts.count1++
            found = findPath(plusOne, all, counts)
        }
        if (!found && all.remove(plusTwo)) {
            counts.count2++
            found = findPath(plusTwo, all, counts)
        }
        if (!found && all.remove(plusThree)) {
            counts.count3++
            found = findPath(plusThree, all, counts)
        }
        return found
    }

}
