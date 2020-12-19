package com.advent.puzzle18

import java.io.File
import java.lang.RuntimeException
import java.util.Stack

typealias Exprs = List<Expr>

fun main() {
    val puzzle = Puzzle18()
    try {
        val tokenized = puzzle.readInputs("inputs.txt")
        val answer = puzzle.partA(tokenized)
        println("Answer A is $answer")

        val answerB = puzzle.partB(tokenized)
        println("Answer B is $answerB")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle18 {
    fun readInputs(filename: String): Exprs {
        val file = File(filename)
        val lines = file.readLines()
        return lines.map { parse(it) }
    }

    fun parse(line: String) : Expr {
        val tokens = line
            .replace("(", "( ")
            .replace(")", " )")
            .split(' ')

        val stack = Stack<Expr>()
        val current = Expr()
        stack.push(current)
        tokens.forEach {
            val top = stack.peek()
            when (it) {
                "(" -> {
                    val subExpr = Expr()
                    top.add(subExpr)
                    stack.push(subExpr)
                }
                ")" -> stack.pop()
                "*" -> top.add(Operator(OP.TIMES))
                "+" -> top.add(Operator(OP.PLUS))
                else -> top.add(Term(it.toLong()))
            }
        }
        return current
    }

    fun partA(tokenized: List<Expr>) : Long {
        return tokenized.fold(0) { acc, expr ->
            val thisOne = expr.eval()
            println("Eval of $expr is $thisOne")
            acc + thisOne
        }
    }

    fun partB(tokenized: List<Expr>): Long {
        val groupAdds = tokenized.map { it.groupAdds() }
        return partA(groupAdds)
    }
}

enum class OP(val ch: Char) { PLUS('+'), TIMES('*') }

open class Expr {
    var subExprs = ArrayList<Expr>()
    fun add(expr: Expr) = subExprs.add(expr)

    open fun eval() : Long {
        if (subExprs.isEmpty()) {
            return 0
        }
        var answer = subExprs[0].eval()
        var i = 1
        while (i < subExprs.size) {
            val op = subExprs[i++] as Operator
            val next = subExprs[i++]
            if (op.op == OP.PLUS) {
                answer += next.eval()
            } else {
                answer *= next.eval()
            }
        }
        return answer
    }

    override fun toString(): String {
        return "(${subExprs.joinToString(" ")})"
    }

    fun groupAdds() : Expr {
        if (subExprs.isEmpty()) {
            return this
        }
        val first = subExprs.first().groupAdds()
        if (subExprs.size == 1) {
            return first
        }

        val newExpr = Expr()
        var grouping = Expr()
        grouping.add(first)
        newExpr.add(grouping)

        var i = 1
        while (i < subExprs.size) {
            val op = subExprs[i++] as Operator
            val next = subExprs[i++]

            if (op.op == OP.PLUS) {
                grouping.add(op)
                grouping.add(next.groupAdds())
            } else {
                val subGrouping = Expr()
                subGrouping.add(next.groupAdds())
                newExpr.add(op)
                newExpr.add(subGrouping)
                grouping = subGrouping
            }
        }
        return newExpr
    }

}

class Term(val value: Long) : Expr() {
    override fun toString(): String {
        return value.toString()
    }

    override fun eval(): Long {
        return value
    }
}
class Operator(val op: OP) : Expr() {
    override fun eval(): Long {
        throw RuntimeException("oops")
    }

    override fun toString(): String {
        return "${op.ch}"
    }
}

