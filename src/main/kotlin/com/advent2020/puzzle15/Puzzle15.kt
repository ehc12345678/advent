package com.advent.advent2020.puzzle15

fun main() {
    val puzzle = Puzzle15()
    try {
        val answer : Int = puzzle.puzzleFindNth(listOf(2, 1, 10, 11, 0, 6), 2020)
        println("For 2020, answer is $answer")

        val answer2 = puzzle.puzzleFindNth(listOf(2, 1, 10, 11, 0, 6), 30000000)
        println("For 2020, answer is $answer2")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

typealias NumberMap = HashMap<Int, Int>
class Puzzle15 {
    fun puzzleFindNth(inputs: List<Int>, n: Int): Int {
        var i = 1
        val map = NumberMap() // number to index
        var next = 0
        inputs.forEach {
            next = say(i++, it, map)
        }
        for (x in i until n) {
            next = say(x, next, map)
        }
        return next
    }

    fun say(index: Int, number: Int, map: NumberMap) : Int {
        val gap = index - map.getOrDefault(number, index)
        map[number] = index
        return gap
    }

}