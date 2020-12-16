package com.advent.puzzle16

import java.io.File

fun main() {
    val puzzle = Puzzle16()
    try {
        val data = puzzle.readInputs("inputs.txt")
        val answer = puzzle.puzzleFindAnswerA(data)
        println("Answer A is $answer")

        val answerB = puzzle.puzzleFindAnswerB(data)
        println("Answer B is $answerB")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

typealias Ticket = List<Int>

class NamedRanges(var name: String) {
    var ranges = ArrayList<IntRange>()

    override fun toString(): String {
        return "$name ${ranges}"
    }
}

class Data {
    var validRanges = ArrayList<NamedRanges>()
    var ticket = emptyList<Int>()
    var nearByTickets = ArrayList<Ticket>()
}

class Puzzle16 {
    fun readInputs(filename: String): Data {
        val file = File(filename)
        val lines = file.readLines()
        var i = 0
        val data = Data()
        do {
            val line = lines[i]
            val colon = line.indexOf(':')
            val namedRange = NamedRanges(line.substring(0, colon))

            val split = line.substring(colon + 1).split(" or ")
            split.forEach {
                val rangeStrs = it.split("-").map { it.trim() }
                namedRange.ranges.add(rangeStrs[0].toInt()..rangeStrs[1].toInt())
            }
            data.validRanges.add(namedRange)
            ++i
        } while (lines[i].isNotEmpty())

        assert(lines[i++] == "")
        assert(lines[i++] == "your ticket:")
        data.ticket = lines[i++].split(",").map { it.toInt() }

        assert(lines[i++] == "")
        assert(lines[i++] == "nearby tickets:")

        while (i < lines.size) {
            data.nearByTickets.add(lines[i++].split(",").map { it.toInt() })
        }

        return data
    }

    fun puzzleFindAnswerA(data: Data): Int {
        var answer = 0
        data.nearByTickets.forEach {
            answer += findNotInValid(it, data.validRanges)
        }
        return answer
    }

    private fun findNotInValid(ticket: Ticket, validRanges: ArrayList<NamedRanges>) : Int {
        val thisTicket = ticket.fold(0) { acc, it -> acc + (if (inValidRanges(it, validRanges)) 0 else it) }
        return thisTicket
    }

    private fun inValidRanges(num: Int, validRanges: ArrayList<NamedRanges>) : Boolean {
        return validRanges.find { namedRange ->
            inValidRange(namedRange, num)
        } != null
    }

    private fun inValidRange(namedRange: NamedRanges, num: Int) = namedRange.ranges.find { range -> range.contains(num) } != null

    fun puzzleFindAnswerB(data: Data): Long {
        val validTickets = listOf(data.ticket) + data.nearByTickets.filter { findNotInValid(it, data.validRanges) == 0 }
        val possible = data.validRanges.map { it.name }.toSet()
        val columnPossibles = Array(possible.size) { HashSet(possible) }

        validTickets.forEach { ticket ->
            ticket.forEachIndexed { colIndex, number ->
                val columnPossible = columnPossibles[colIndex]
                data.validRanges.forEach {
                    if (columnPossible.contains(it.name) && !inValidRange(it, number)) {
                        removeItem(columnPossible, it.name, columnPossibles)
                    }
                }
            }
        }

        reducePossibleColumns(columnPossibles)

        val columnSolutions = columnPossibles.map {
            if (it.size == 1) {
                it.first()
            } else {
                ""
            }
        }
        println("${data.ticket}")
        println("Columns $columnSolutions")

        // check the solutions
        val mapRanges = data.validRanges.map { it.name to it }.toMap()
        validTickets.forEach { ticket ->
            ticket.forEachIndexed { colIndex, number ->
                val colName = columnSolutions[colIndex]
                val range = mapRanges[colName]
                if (range != null) {
                    if (!inValidRange(range, number)) {
                        println("We have a problem!")
                    }
                }
            }
        }

        var answer = 1L
        data.ticket.forEachIndexed { colIndex, number ->
            val colName = columnSolutions[colIndex]
            if (colName.contains("departure")) {
                println("Found $colName with $number at $colIndex")
                answer *= number
            }
        }
        return answer
    }

    // This goes through an uses logic to reduce the column possibilities.  If an item only appears in one set, then
    // it must be the one that is picked.  For instance, if you have (a,b,c), (a, b), (d,f)... you can say that
    // column one is definitely c because it is the only place c appears.  You can continue doing that until you
    // nothing changes, which is the most reduced it can get
    private fun reducePossibleColumns(columnPossibles: Array<java.util.HashSet<String>>) {
        var changed: Boolean

        do {
            changed = false
            columnPossibles.forEach { hashSet ->
                if (hashSet.size > 1) {
                    val solution = hashSet.find { column ->
                        // if the column is only here, that is the answer
                        columnPossibles.none { other -> hashSet != other && other.contains(column) }
                    }
                    if (solution != null) {
                        hashSet.removeAll(hashSet.filter { it != solution })
                        changed = true
                    }
                }
            }
        } while (changed)
    }

    private fun removeItem(columnPossible: java.util.HashSet<String>, column: String,
                           columnPossibles: Array<java.util.HashSet<String>>) {
        columnPossible.remove(column)
        // if we reach a point where we have only one possibility, all the other columns cannot be that
        if (columnPossible.size == 1) {
            reducePossibles(columnPossibles, columnPossible.first())
        }
    }

    // this removes the column from all the other places it finds (making sure not to remove it from the one
    // in which it definitely is (the one with only one possibility)
    private fun reducePossibles(columnPossibles: Array<java.util.HashSet<String>>, column: String) {
        columnPossibles.forEach { columnPossible ->
            if (columnPossible.size != 1 && columnPossible.contains(column)) {
                removeItem(columnPossible, column, columnPossibles)
            }
        }
    }
}