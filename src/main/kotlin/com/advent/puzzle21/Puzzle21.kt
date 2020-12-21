package com.advent.puzzle21

import java.io.File
import java.lang.IllegalArgumentException

fun main() {
    val puzzle = Puzzle21()
    try {
        val data = puzzle.readInputs("inputs.txt")
        val noPossibleAllergens = data.any.filter { str -> data.possibles.none() { entry -> entry.value.contains(str) } }
        println("A is ${noPossibleAllergens.size}")

        puzzle.reducePossibles(data)
        val answerB = data.solutions.toSortedMap().values.joinToString(separator = ",")
        println(answerB)
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

typealias IngrediantSet = Set<String>
typealias Possibles = HashMap<String, IngrediantSet>
typealias Solution = HashMap<String, String>
class Data {
    var possibles = Possibles()
    var solutions = Solution()
    var any = ArrayList<String>()
}

class Puzzle21 {
    fun readInputs(filename: String): Data {
        val file = File(filename)
        val lines = file.readLines()
        val data = Data()

        lines.forEach { line -> parseLine(line, data) }
        return data
    }

    private fun parseLine(line: String, data: Data) {
        val regex = """(.*) \(contains (.*)\)""".toRegex()
        val match = regex.find(line) ?: throw IllegalArgumentException("Bad data $line")
        val scrambled = match.groups[1]?.value?.split(" ") ?: throw IllegalArgumentException("No group1 $line")
        val ingrediants = match.groups[2]?.value?.split(", ") ?: throw IllegalArgumentException("No group1 $line")

        data.any.addAll(scrambled)

        val scrambledSet = scrambled.toSet()
        ingrediants.forEach { ingrediant ->
            if (data.solutions.containsKey(ingrediant)) {
                // sanity check
                if (!scrambledSet.contains(data.solutions[ingrediant])) {
                    throw IllegalArgumentException("Something went wrong, we didn't find solution")
                }
            } else {
                val existing = data.possibles[ingrediant]
                val newScrambledList =
                    if (existing == null) {
                        scrambledSet
                    } else {
                        existing.intersect(scrambledSet)
                    }
                data.possibles[ingrediant] = newScrambledList
                if (newScrambledList.size == 1) {
                    data.solutions[ingrediant] = newScrambledList.first()
                }
            }
        }
    }

    fun reducePossibles(data: Data) {
        var changed: Boolean = true
        val possibles = Possibles(data.possibles)
        while (changed) {
            val onlyOne = possibles.filter { entry -> entry.value.size == 1 }
            changed = !onlyOne.isEmpty()
            onlyOne.forEach { entry ->
                val solution = entry.value.first()
                data.solutions[entry.key] = solution
                possibles.forEach {
                    possibles[it.key] = it.value - setOf(solution)
                }
                possibles.remove(entry.key)
            }
        }
    }
}
