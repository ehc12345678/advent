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
        resolveAllWeCan(data)
        return data.resolved["root"]!!
    }

    private fun resolveAllWeCan(data: Data) {
        while (data.unresolved.isNotEmpty()) {
            val resolvedThisTime = ArrayList<String>()
            data.unresolved.forEach { (key, value) ->
                val left = data.resolved[value.leftOperand]
                val right = data.resolved[value.rightOperand]
                if (left != null && right != null) {
                    val calc = performOp(left, right, value.op)
                    resolvedThisTime.add(key)
                    data.resolved[key] = calc
                    if (key == "root") {
                        println("Solved!")
                    }
                }
            }

            if (resolvedThisTime.isEmpty()) {
                break
            }
            resolvedThisTime.forEach { data.unresolved.remove(it) }
        }
    }

    override fun computeSolution2(data: Data): Solution2 {
        data.resolved.remove("humn")
        resolveAllWeCan(data)

        val root = data.unresolved["root"]!!
        var currentTotal = data.resolved[root.leftOperand] ?: data.resolved[root.rightOperand]
        var unresolved = if (data.resolved.containsKey(root.leftOperand)) root.rightOperand else root.leftOperand

        // as an example, we can have
        //    root: xnzx = 123213
        // so, our goal is to make xnzx equal to that number
        while (data.unresolved.containsKey(unresolved)) {
            val solveIt = data.unresolved.remove(unresolved)!!

            // to continue our example, we have
            //     xnzx = abcd + 1232
            // or
            //     xnzx = 1232 + abcd

            var inverseOp = inverseOp(solveIt.op)
            val resolvedLeft = data.resolved[solveIt.leftOperand]
            val resolvedRight = data.resolved[solveIt.rightOperand]
            if (resolvedLeft != null) {
                //     1232 + abcd = 123213      abcd = 123213 - 1232
                //     1232 * abcd = 123213       abcd = 123213 / 1232
                //     1232 / abcd = 123213       1/abcd = 123213 / 1232  abcd = 1232 / 123213
                //     1232 - abcd = 123213       abcd = -123213 + 1232
                val left = when (solveIt.op) {
                    Operator.ADD, Operator.MULT -> currentTotal!!
                    Operator.MINUS -> -currentTotal!!
                    Operator.DIV -> resolvedLeft
                }
                val right = when (solveIt.op) {
                    Operator.ADD, Operator.MULT -> resolvedLeft
                    Operator.MINUS -> resolvedLeft
                    Operator.DIV -> {
                        inverseOp = Operator.DIV
                        currentTotal
                    }
                }
                currentTotal = performOp(left, right!!, inverseOp)
                unresolved = solveIt.rightOperand
            } else {
                //     abcd + 1232 = 123213      abcd = 123213 - 1232
                //     abcd * 1232 = 123213       abcd = 123213 / 1232
                //     abcd / 1232 = 123213       abcd = 123213 / 1232
                //     abcd - 1232 = 123213       abcd = 123213 + 1232
                currentTotal = performOp(currentTotal!!, resolvedRight!!, inverseOp)
                unresolved = solveIt.leftOperand
            }
        }

        return currentTotal!!
    }

    fun performOp(left: Long, right: Long, op: Operator): Long {
        return when(op) {
            Operator.ADD -> left + right
            Operator.MINUS -> left - right
            Operator.MULT -> left * right
            Operator.DIV -> left / right
        }
    }

    fun inverseOp(op: Operator): Operator {
        return when(op) {
            Operator.ADD -> Operator.MINUS
            Operator.MINUS -> Operator.ADD
            Operator.MULT -> Operator.DIV
            Operator.DIV -> Operator.MULT
        }
    }
}

