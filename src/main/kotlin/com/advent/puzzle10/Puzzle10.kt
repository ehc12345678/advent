package com.advent.puzzle9

import java.io.File

data class Counts(
    var count1: Long,
    var count2: Long,
    var count3: Long
)

fun main() {
    val puzzle = Puzzle10()
    try {
        val nums = puzzle.readInputs("inputs.txt")
        val goal = nums.last() + 3
        nums.add(goal)
        nums.forEach { println(it) }
        val counts = Counts(0, 0, 0)
        puzzle.findPath(0, HashSet(nums), counts)
        println("Value is ${counts.count1 * counts.count3}")

        val allCount = puzzle.findAllPath2(nums, goal)
        println("All count is $allCount")

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

    fun findAllPath2(nums: List<Int>, goal: Int): Long {
        val map = HashMap<Int, Long>()
        map[goal] = 1L
        for (index in nums.size - 2 downTo 0) {
            val num = nums[index]
            map[num] =
                map.getOrDefault(num + 1, 0) +
                map.getOrDefault(num + 2, 0) +
                map.getOrDefault(num + 3, 0)
        }
        val num = 0
        return map.getOrDefault(num + 1, 0) +
            map.getOrDefault(num + 2, 0) +
            map.getOrDefault(num + 3, 0)
    }

}
