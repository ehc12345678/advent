package com.advent2021.puzzle8

import com.advent2021.base.Base
import com.advent2021.puzzle8.Puzzle8.Companion.stringToSetOfChars

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
        val allNumbers = (line.inputs + line.answerDigits).map { stringToSetOfChars(it) }.toSet()
        val one = findDistinctByLen(allNumbers, ONE)
        val seven = findDistinctByLen(allNumbers, SEVEN)
        val eight = findDistinctByLen(allNumbers, EIGHT)
        val four = findDistinctByLen(allNumbers, FOUR)

        val allFives = findAllByLen(allNumbers, FIVE)
        val allSixes = findAllByLen(allNumbers, ZERO)

        val three = allFives.find { it.containsAll(seven) }!!
        val twoOrFive = allFives.filter { it != three }
        val bg = four - one
        val five = twoOrFive.find { it.containsAll(bg) }!!
        val two = twoOrFive.find { it != five }!!

        val nine = five + one
        val zeroOrSix = allSixes.filter { it != nine }
        val zero = zeroOrSix.find { it.containsAll(one) }!!
        val six = zeroOrSix.find { it != zero }!!

        val answers = mapOf(
            zero to 0, one to 1, two to 2, three to 3, four to 4,
            five to 5, six to 6, seven to 7, eight to 8, nine to 9)

        val buf = StringBuffer()
        line.answerDigits.forEach {
            if (answers[stringToSetOfChars(it)] == null) {
                val dbg = "${answers.entries.associate { setToString(it.key) to it.value }}"
                throw IllegalArgumentException(it)
            }
            buf.append(answers[stringToSetOfChars(it)]!!)
        }
        return buf.toString().toInt()
    }

    fun setToString(set: Set<Char>): String {
        return ArrayList(set).sorted().joinToString("") { it.toString() }
    }

    private fun findDistinctByLen(allNumbers: Set<MutableSet<Char>>, digit: EncodedDigit) =
        allNumbers.find { it.size == digit.set.size }!!
    private fun findAllByLen(allNumbers: Set<MutableSet<Char>>, digit: EncodedDigit) =
        allNumbers.filter { it.size == digit.set.size }

    companion object {
        fun stringToSetOfChars(s: String) = s.toCharArray().toMutableSet()

        val ONE = EncodedDigit(1, "cf")
        val SEVEN = EncodedDigit(7, "acf")
        val FOUR = EncodedDigit(4, "bcfg")
        val EIGHT = EncodedDigit(8, "abcdefg")

        val THREE=  EncodedDigit(3, "acdfg")  // "acdfg" - "cf" = "adg"
        val TWO = EncodedDigit(2, "acdeg")
        val FIVE = EncodedDigit(5, "abdfg")

        val ZERO = EncodedDigit(0, "abcefg")
        val SIX = EncodedDigit(6, "abdefg")
        val NINE = EncodedDigit(9, "abcdfg")
        val DIGIT_MAP: Map<Int, EncodedDigit> =
            listOf(ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE).associateBy { it.digit }
    }
}

