package com.advent.puzzle25

import java.io.File

typealias Item = String
typealias Data = List<Item>

fun main() {
    val puzzle = Puzzle25()
    try {
        val data = puzzle.readInputs("inputs.txt")
        val answerA = puzzle.solutionA(data)
        println("Answer A is $answerA")

        val answerB = puzzle.solutionB(data)
        println("Answer B is $answerB")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle25 {
    fun readInputs(filename: String): Data {
        val file = File(filename)
        val lines = file.readLines()
        val data = lines.map { parseLine(it) }
        return data
    }

    fun parseLine(line: String) : Item {
        return line
    }

    fun solutionA(data: Data) : Long {
        return 0
    }

    fun solutionB(data: Data) : Long {
        return 0
    }
}