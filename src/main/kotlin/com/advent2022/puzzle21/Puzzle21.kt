package com.advent2022.puzzle21

import com.advent2021.base.Base

enum class Operator { ADD, MINUS, MULT, DIV }
data class Operation(val leftOperand: String, val rightOperand: String, val op: Operator)

fun opFromString(str: String) = when (str) {
    "+" -> Operator.ADD
    "-" -> Operator.MINUS
    "*" -> Operator.MULT
    "/" -> Operator.DIV
    else -> throw IllegalArgumentException("Unknown operator $str")
}

class Data {
    val resolved = HashMap<String, Long>()
    val unresolved = HashMap<String, Operation>()
}
typealias Solution = Long
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle21()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle21 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val monkey = line.substringBefore(":")
        val rest = line.substringAfter(": ").split(" ")
        if (rest.size == 1) {
            data.resolved[monkey] = rest[0].toLong()
        } else {
            data.unresolved[monkey] = Operation(rest[0], rest[2], opFromString(rest[1]))
        }
    }

    override fun computeSolution(data: Data): Solution {
        while (data.unresolved.isNotEmpty()) {
            val resolvedThisTime = ArrayList<String>()
            data.unresolved.forEach { (key, value) ->
                val left = data.resolved[value.leftOperand]
                val right = data.resolved[value.rightOperand]
                if (left != null && right != null) {
                    val calc = performOp(left, right, value.op)
                    resolvedThisTime.add(key)
                    data.resolved[key]  = calc
                    if (key == "root") {
                        println("Solved!")
                    }
                }
            }

            if (resolvedThisTime.isEmpty()) {
                println("We are stuck!")
            }
            resolvedThisTime.forEach { data.unresolved.remove(it) }
        }
        return data.resolved["root"]!!
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }

    fun performOp(left: Long, right: Long, op: Operator): Long {
        return when(op) {
            Operator.ADD -> left + right
            Operator.MINUS -> left - right
            Operator.MULT -> left * right
            Operator.DIV -> left / right
        }
    }
}

