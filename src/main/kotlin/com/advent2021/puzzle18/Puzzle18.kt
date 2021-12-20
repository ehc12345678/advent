package com.advent2021.puzzle18

import com.advent2021.base.Base
import kotlin.collections.ArrayList
import kotlin.math.max

abstract class Term(
    var parent: Term?
) {
    val depth: Int = if (parent == null) 1 else parent!!.depth + 1
    open fun add(t: Term) { throw IllegalArgumentException("Cannot add to this term") }
    open fun magnitude() : Long = 0
    open fun replace(term: Term, newTerm: Term): Unit = throw IllegalArgumentException("Cannot replace")
    open fun children(): List<Term> = emptyList()
    abstract fun copy(parent: Term?): Term
}
class PairTerm(parent: Term?): Term(parent) {
    private var pair: Pair<Term?,Term?> = Pair(null, null)
    var first: Term?
        get() = pair.first
        set(t) {
            pair = Pair(setParent(t), pair.second)
        }
    var second: Term?
        get() = pair.second
        set(t) {
            pair = Pair(pair.first, setParent(t))
        }
    override fun add(t: Term) {
        when {
            first == null -> first = t
            second == null -> second = t
            else -> throw IllegalArgumentException("Invalid state")
        }
    }

    override fun toString(): String = "[$first,$second]"
    override fun magnitude(): Long = ((first?.magnitude() ?: 0) * 3) + ((second?.magnitude() ?: 0) * 2)
    override fun replace(term: Term, newTerm: Term) {
        when {
            first == term -> first = newTerm
            second == term -> second = newTerm
            else -> throw IllegalArgumentException("Could not find replacement")
        }
    }

    private fun setParent(t: Term?): Term? = t?.also { it.parent = this }
    override fun children(): List<Term> = listOfNotNull(first, second)
    override fun copy(parent: Term?): Term = PairTerm(parent).also {
        it.first = first?.copy(it)
        it.second = second?.copy(it)
    }
}
class Number(parent: Term?, val number: Int): Term(parent) {
    override fun toString(): String = number.toString()
    override fun magnitude(): Long = number.toLong()
    override fun copy(parent: Term?): Term = Number(parent, number)
}

fun String.toTerm() = consumeTerm(null, State(this, 0))

data class State(val s: String, var pos: Int) {
    fun nextCh() = s[pos++]
    fun thisCh() = s[pos]
}

fun consumeTerm(parent: Term?, state: State): Term {
    val ch = state.thisCh()
    return when (ch) {
        '[' -> {
            state.nextCh()
            consumePair(parent, state)
        }
        ',' -> throw IllegalArgumentException("Unexpected comma")
        else -> {
            consumeNumber(parent, state)
        }
    }
}

fun consumePair(parent: Term?, state: State): PairTerm {
    val pairTerm = PairTerm(parent)
    pairTerm.first = consumeTerm(pairTerm, state)
    if (state.nextCh() != ',') {
        throw IllegalArgumentException("Expected comma")
    }
    pairTerm.second = consumeTerm(pairTerm, state)
    if (state.nextCh() != ']') {
        throw IllegalArgumentException("Expected end brace")
    }
    return pairTerm
}

fun consumeNumber(parent: Term?, state: State): Number = Number(parent, state.nextCh() - '0')

typealias Data = ArrayList<Term>
typealias Solution = Long
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle18()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle18 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val term = line.toTerm()
        data.add(term)
    }

    override fun computeSolution(data: Data): Solution {
        val sum = data.reduce { acc, it -> addAndReduce(acc, it) }
        return sum.magnitude()
    }

    private fun addAndReduce(currentSum: Term, term: Term): Term {
        val sum = PairTerm(null)
        sum.add(currentSum.copy(null))
        sum.add(term.copy(null))
        return reduce(sum)
    }

    private fun reduce(term: Term): Term {
        var isReduced = false
        while (!isReduced)  {
            isReduced = true
            val explode = findFirst(term) { it is PairTerm && it.depth >= 4 }
            if (explode != null) {
                explodeTerm(term, explode as PairTerm)
                isReduced = false
            } else {
                val split = findFirst(term) { it is Number && it.number >= 10 }
                if (split != null) {
                    splitTerm(split as Number)
                    isReduced = false
                }
            }
        }
        return term
    }

    private fun childrenBefore(term: Term, findTerm: Term, working: ArrayList<Term>): Boolean {
        var found = term == findTerm
        if (!found) {
            working.add(term)
            for (kid in term.children()) {
                found = found || childrenBefore(kid, findTerm, working)
            }
        }
        return found
    }

    data class FoundAfterState(var found: Boolean)
    private fun childrenAfter(term: Term, afterTerm: Term, working: ArrayList<Term>,
                              foundAfter: FoundAfterState = FoundAfterState(false)): List<Term> {
        for (kid in term.children()) {
            if (!foundAfter.found && kid == afterTerm) {
                foundAfter.found = true
            } else {
                if (foundAfter.found) {
                    working.add(kid)
                }
                childrenAfter(kid, afterTerm, working, foundAfter)
            }
        }
        return working
    }

    private fun findFirst(term: Term, predicate: (term: Term) -> Boolean): Term? {
        if (predicate(term)) {
            return term
        }
        for (child in term.children()) {
            val first = findFirst(child, predicate)
            if (first != null) {
                return first
            }
        }
        return null
    }

    private fun findBefore(term: Term, beforeTerm: Term, predicate: (term: Term) -> Boolean): Term? {
        val kids = ArrayList<Term>()
        childrenBefore(term, beforeTerm, kids)
        return kids.lastOrNull { predicate(it) }
    }

    private fun findAfter(term: Term, afterTerm: Term, predicate: (term: Term) -> Boolean): Term? {
        val childrenAfter = childrenAfter(term, afterTerm, ArrayList())
        return childrenAfter.firstOrNull { predicate(it) }
    }

    private fun explodeTerm(overallTerm: Term, pair: PairTerm) {
        val numberBeforePair = findBefore(overallTerm, pair) { it is Number } as Number?
        if (numberBeforePair != null) {
            val oldNumber = pair.first as Number?
            val newNumber = oldNumber!!.number + numberBeforePair.number
            numberBeforePair.parent?.replace(numberBeforePair, Number(numberBeforePair.parent, newNumber))
        }

        val numberAfterPair = findAfter(overallTerm, pair) { it is Number } as Number?
        if (numberAfterPair != null) {
            val oldNumber = pair.second as Number?
            val newNumber = oldNumber!!.number + numberAfterPair.number
            numberAfterPair.parent?.replace(numberAfterPair, Number(numberAfterPair.parent, newNumber))
        }
        pair.parent?.replace(pair, Number(pair.parent, 0))
    }

    private fun splitTerm(term: Number) {
        val newTerm = PairTerm(term.parent)
        newTerm.first = Number(newTerm, term.number / 2)
        newTerm.second = Number(newTerm, (term.number + 1) / 2)
        term.parent?.replace(term, newTerm)
    }

    override fun computeSolution2(data: Data): Solution2 {
        var highest: Solution2 = 0
        for (i in 0 until data.size - 1) {
            for (j in i + 1 until data.size) {
                if (i != j) {
                    highest = max(addAndReduce(data[i], data[j]).magnitude(), highest)
                    highest = max(addAndReduce(data[j], data[i]).magnitude(), highest)
                }
            }
        }
        return highest
    }
}

