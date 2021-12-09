package com.advent2021.puzzle8

import com.advent2021.base.Base
import com.advent2021.puzzle8.Puzzle8.Companion.stringToSetOfChars
import java.lang.IllegalArgumentException

data class Line(
    var inputs: List<String>,
    var answerDigits: List<String>
)
typealias Data = ArrayList<Line>
typealias Solution = Int
typealias Solution2 = Solution

class EncodedDigit(val digit: Int, str: String) {
    val set = stringToSetOfChars(str)
}

fun main() {
    try {
        val puz = Puzzle8()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle8 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val parts = line.split(" | ")
        data.add(
            Line(
                parts[0].split(" ").map { it.toUpperCase() },
                parts[1].split(" ").map { it.toUpperCase() }
            )
        )
    }

    override fun computeSolution(data: Data): Solution {
        return data.sumOf { line ->
            line.answerDigits.sumOf {
                (if (setOf(2, 3, 4, 7).contains(it.length)) 1 else 0).toInt()
            }
        }
    }

    override fun computeSolution2(data: Data): Solution2 {
        return data.sumOf {
            solveOne(it)
        }
    }


    class WorkingData(var line: Line) {
        var possibleChars: MutableMap<Char, MutableSet<Char>> = HashMap()
        var possibleDigits: MutableMap<MutableSet<Char>, MutableSet<Int>> = HashMap()

        override fun toString(): String {
            val buf = StringBuffer()
            buf.append("Chars: ${possibleChars.entries.associate { entry -> entry.key to setToString(entry.value) }}\n")
            buf.append("Digits: ${possibleDigits.entries.associate { entry -> setToString(entry.key) to entry.value }}\n")
            return buf.toString()
        }

        fun setToString(set: Set<Char>): String {
            return set.joinToString("") { it.toString() }
        }
    }

    private fun solveOne(line: Line): Int {
        val possibleChars = HashMap<Char, Set<Char>>()
        for (ch in 'a'..'g') {
            possibleChars[ch.toUpperCase()] = stringToSetOfChars("abcdefg")
        }
        val workingData = WorkingData(line)
        for (ch in 'a'..'g') {
            workingData.possibleChars[ch.toUpperCase()] = stringToSetOfChars("abcdefg")
        }
        workingData.possibleDigits =
            (line.inputs + line.answerDigits).map { stringToSetOfChars(it) }.associateWith { input ->
                DIGIT_MAP.filter { entry ->
                    entry.value.set.size == input.size
                }.map { it.key }.toMutableSet()
            }.toMutableMap()

        line.inputs.forEach {
            consumeInput(it, workingData)
        }
        line.answerDigits.forEach  {
            consumeInput(it, workingData)
        }
        lastReduce(workingData)

        println("Checking ${workingData.line}")
        val buf = StringBuffer()
        line.answerDigits.forEach {
            val possibles = workingData.possibleDigits[stringToSetOfChars(it)]
            if (possibles == null || possibles.size > 1) {
                throw IllegalArgumentException("Couldn't find a number for $it")
            } else if (possibles.size == 0) {
                throw IllegalArgumentException("Oops for $it")
            }
            buf.append(possibles.first())
        }
        return buf.toString().toInt()
    }

    private fun lastReduce(workingData: WorkingData) {
        val ambigiousDigits = workingData.possibleDigits.entries.filter { entry -> entry.value.size > 1 }
        val ambiguousChars = workingData.possibleChars.entries.filter { entry -> entry.value.size > 1 }
        var somethingChanged = false
        while (ambigiousDigits.isNotEmpty()) {
            ambigiousDigits.forEach { entry ->
                println("Reducing ambigious digits for ${workingData.line}")
                HashSet(entry.value).forEach { num ->
                    if (ambigiousDigits.count { it.value.contains(num) } == 1) {
                        somethingChanged = true
                        foundDigit(entry.key, num, workingData)
                    }
                }
            }
            ambiguousChars.forEach { entry ->
                println("Reducing ambigious chars for ${workingData.line}")
                HashSet(entry.value).forEach { ch ->
                    if (ambiguousChars.count { it.value.contains(ch) } == 1) {
                        somethingChanged = true
                        foundChar(entry.key, ch, workingData)
                    }
                }
            }
            if (somethingChanged) {
                lastReduce(workingData)
            } else {
                println("Ut oh")
            }
        }
    }

    private fun consumeInput(input: String, workingData: WorkingData) {
        val possibleDigits = workingData.possibleDigits.filter { entry ->
            entry.key.size == input.length
        }.map { it.value }.toMutableSet()

        if (possibleDigits.size == 1) {
            foundDigit(stringToSetOfChars(input), possibleDigits.first().first(), workingData)
        }
    }

    private fun foundDigit(input: Set<Char>, digit: Int, workingData: WorkingData) {
        // remove the digit from possibilities for everything except the thing we found
        val setOfInts = workingData.possibleDigits[input] ?: return
        setOfInts.removeIf { it != digit }
        workingData.possibleDigits.filter { it.key != input }.forEach {
            val possibilitySet = it.value
            if (possibilitySet.size > 1) {
                possibilitySet.removeIf { num -> num == digit }
                if (possibilitySet.size == 1) {
                    foundDigit(it.key, possibilitySet.first(), workingData)
                }
            }
        }

        input.forEach { ch ->
            val possible = workingData.possibleChars[ch]
            if (possible != null && possible.size > 1) {
                possible.retainAll(DIGIT_MAP[digit]!!.set)
                if (possible.size == 1) {
                    foundChar(ch, possible.first(), workingData)
                }
            }
        }
    }

    private fun foundChar(inputCh: Char, realCh: Char, workingData: WorkingData) {
//        workingData.possibleDigits.forEach { entry ->
//            val key = entry.key
//            if (key.contains(inputCh) && entry.value.size > 1) {
//                entry.value.retainAll { DIGIT_MAP[it]!!.set.contains(realCh) }
//                if (entry.value.size == 1) {
//                    foundDigit(key, entry.value.first(), workingData)
//                }
//            }
//        }

        val newMap = workingData.possibleDigits.entries.associate { entry ->
            val key: MutableSet<Char> = HashSet(entry.key)
            if (key.contains(inputCh)) {
                key.remove(inputCh)
                key.add(realCh)
            }
            val value = entry.value.also { set -> set.retainAll { DIGIT_MAP[it]!!.set.contains(realCh) } }
            key to value
        }.toMutableMap()
        workingData.possibleDigits = newMap

        workingData.possibleChars.forEach { otherCh ->
            if (otherCh.key != inputCh) {
                val possible = workingData.possibleChars[otherCh.key]
                if (possible != null && possible.remove(inputCh) && possible.size == 1) {
                    foundChar(otherCh.key, possible.first(), workingData)
                }
            } else {
                workingData.possibleChars[inputCh]?.retainAll(listOf(realCh))
            }
        }
        workingData.possibleDigits.forEach { entry ->
            val key = entry.key
            if (key.contains(inputCh) && entry.value.size > 1) {
                entry.value.retainAll { DIGIT_MAP[it]!!.set.contains(realCh) }
                if (entry.value.size == 1) {
                    foundDigit(key, entry.value.first(), workingData)
                }
            }
        }
    }

    companion object {
        fun stringToSetOfChars(s: String) = s.toCharArray().toMutableSet()

        val ZERO = EncodedDigit(0, "abcefg")
        val ONE = EncodedDigit(1, "cf")
        val TWO = EncodedDigit(2, "acdeg")
        val THREE=  EncodedDigit(3, "acdfg")
        val FOUR = EncodedDigit(4, "bcfg")
        val FIVE = EncodedDigit(5, "abdfg")
        val SIX = EncodedDigit(6, "abdefg")
        val SEVEN = EncodedDigit(7, "acf")
        val EIGHT = EncodedDigit(8, "abcdefg")
        val NINE = EncodedDigit(9, "abcdfg")
        val DIGIT_MAP: Map<Int, EncodedDigit> =
            listOf(ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE).associateBy { it.digit }
    }
}

