package com.advent2021.puzzle10

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayList

typealias Data = ArrayList<String>
typealias Solution = Int
typealias Solution2 = Long

fun main() {
    try {
        val puz = Puzzle10()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle10 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line)
    }

    override fun computeSolution(data: Data): Solution {
        val listIllegals = ArrayList<Char>()
        data.forEach { line ->
            val stack = Stack<Char>()
            val ch = getIllegalChar(line, stack)
            if (ch != null) {
                listIllegals.add(ch)
            }
        }
        val points = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
        return listIllegals.sumOf { points[it]!! }
    }

    private fun getIllegalChar(line: String, stack: Stack<Char>): Char? {
        val openChars = mapOf(')' to '(', ']' to '[', '}' to '{', '>' to '<')
        for (ch in line) {
            when (ch) {
                '<','[','{', '(' -> stack.push(ch)
                else -> {
                    if (stack.isEmpty() || stack.pop() != openChars[ch]) {
                        return ch
                    }
                }
            }
        }
        return null
    }

    override fun computeSolution2(data: Data): Solution2 {
        val answers = ArrayList<Solution2>()
        data.forEach { line ->
            val stack = Stack<Char>()
            val ch = getIllegalChar(line, stack)
            if (ch == null) {
                answers.add(calculateIncomplete(stack))
            }
        }
        answers.sort()
        return answers[answers.size / 2]
    }

    private fun calculateIncomplete(stack: Stack<Char>): Solution2 {
        var score: Solution2 = 0
        val points = mapOf('(' to 1, '[' to 2, '{' to 3, '<' to 4)
        while (stack.isNotEmpty()) {
            val pop = stack.pop()
            score *= 5
            score += points[pop]!!
        }
        return score
    }


}

