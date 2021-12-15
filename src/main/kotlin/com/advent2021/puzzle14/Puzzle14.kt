package com.advent2021.puzzle14

import com.advent2021.base.Base
import java.lang.IllegalArgumentException
import kotlin.collections.HashMap

class Data(
    var start: String? = null,
    var transforms: HashMap<String, Char> = HashMap()
)
typealias Solution = Long
typealias Solution2 = Long
typealias CharCount = HashMap<Char, Long>

data class CountsKey(val pair: String, val numSteps: Int)
typealias Working = HashMap<CountsKey, CharCount>

fun main() {
    try {
        val puz = Puzzle14()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle14 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        when {
            data.start == null -> data.start = line
            line.isNotEmpty() -> {
                val parts = line.split(" -> ")
                data.transforms[parts[0]] = parts[1].get(0)
            }
        }
    }

    override fun computeSolution(data: Data): Solution {
        var working = data.start!!
        for (step in 0 until 10) {
            working = doStep(working, data)
        }
        return calcMaxMinusMin(working)
    }

    private fun doStep(working: String, data: Data): String {
        val newString = StringBuffer()
        for (i in 0 until working.length - 1) {
            val pair = working[i].toString() + working[i + 1]
            newString.append(working[i])
            val match = data.transforms[pair]
            if (match != null) {
                newString.append(match)
            }
        }
        newString.append(working.last())
        return newString.toString()
    }

    private fun calcMaxMinusMin(str: String): Long = calcMaxMinusMin(getCharCounts(str))

    private fun calcMaxMinusMin(counts: CharCount): Long =
        counts.entries.maxOf { it.value } - counts.entries.minOf { it.value }

    private fun getCharCounts(str: String): CharCount {
        val counts = HashMap<Char, Long>()
        for (ch in str) {
            counts[ch] = counts.getOrDefault(ch, 0) + 1
        }
        return counts
    }

    override fun computeSolution2(data: Data): Solution2 {
        val working = Working()
        val start = data.start!!

        val numSteps = 40
        var charCounts = getCounts(pairToStr(start[0], start[1]), working, numSteps, data)
        for (i in 1 until start.length - 1) {
            val newCounts = getCounts(pairToStr(start[i], start[i + 1]), working, numSteps, data)
            charCounts = combineCounts(charCounts, newCounts, start[i])
        }
        return calcMaxMinusMin(charCounts)
    }

    private fun getCounts(pair: String, working: Working, numSteps: Int, data: Data): CharCount {
        val key = CountsKey(pair, numSteps)
        var charCount = working[key]
        if (charCount == null) {
            if (numSteps == 0) {
                charCount = getCharCounts(key.pair)
            } else {
                val ch = data.transforms[pair] ?: throw IllegalArgumentException("Couldn't find $pair")
                val count1 = getCounts(pairToStr(pair[0], ch), working, numSteps - 1, data)
                val count2 = getCounts(pairToStr(ch, pair[1]), working, numSteps - 1, data)
                charCount = combineCounts(count1, count2, ch)
            }
            working[key] = charCount
        }
        return charCount
    }

    private fun combineCounts(count1: CharCount, count2: CharCount, ch: Char): CharCount {
        val ret = CharCount(count1)
        for (count2Ch in count2) {
            val newCh = count2Ch.key
            var newCount = ret.getOrDefault(newCh, 0) + count2Ch.value
            if (newCh == ch) {
                newCount -= 1
            }
            ret[newCh] = newCount
        }
        return ret
    }

    private fun pairToStr(ch1: Char, ch2: Char) = ch1.toString() + ch2
}

