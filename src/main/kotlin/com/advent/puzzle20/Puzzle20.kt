package com.advent.puzzle20

import java.io.File
import java.lang.RuntimeException

enum class Match { NORMAL, ROTATE, FLIP }
data class BoundaryMatch(val tile: Tile, val boundary: Boundary, val match: Match)
class Boundary(var str: String) {
    val matches = ArrayList<BoundaryMatch>()
    fun isEdge() = matches.size == 0
    fun addMatch(tile: Tile, boundary: Boundary, match: Match) {
        matches.add(BoundaryMatch(tile, boundary, match))
    }

    override fun equals(other: Any?): Boolean {
        return (other as Boundary).str == str
    }

    val reversed: String
        get() = str.reversed()

    fun reverseStr() {
        str = str.reversed()
    }
}

typealias Puzzle = ArrayList<ArrayList<Tile>>

class Tile(var id: Int, var contents: ArrayList<String> = ArrayList()) {
    private var boundaries = ArrayList<Boundary>()

    fun isEdge() = allBoundaries().count { it.isEdge() } >= 1
    fun isCorner() = allBoundaries().count { it.isEdge() } >= 2
    fun north() = allBoundaries()[0]
    fun east() = allBoundaries()[1]
    fun south() = allBoundaries()[2]
    fun west() = allBoundaries()[3]

    fun allBoundaries() : List<Boundary> {
        if (boundaries.isEmpty()) {
            val north = contents.first()
            val south = contents.last()
            var west = ""
            var east = ""
            contents.forEach { str ->
                west += str.first()
                east += str.last()
            }

            boundaries.add(Boundary(north))
            boundaries.add(Boundary(east))
            boundaries.add(Boundary(south))
            boundaries.add(Boundary(west))
        }
        return boundaries
    }

    fun rotate() {
        west().reverseStr()
        east().reverseStr()
        boundaries = ArrayList(listOf(west(), north(), east(), south()))

        //first change the dimensions vertical length for horizontal length
        //and viceversa
        val n = contents.size
        val newArray : Array<CharArray> = Array(n) { CharArray(n) }
        for (i in 0 until n) {
            for (j in 0 until n) {
                newArray[i][j] = contents[n - j - 1][i]
            }
        }
        contents = ArrayList(newArray.map { String(it) })
    }

    fun flipNorthSouth() {
        east().reverseStr()
        west().reverseStr()
        boundaries = ArrayList(listOf(south(), east(), north(), west()))
        contents.reverse()
    }

    override fun toString(): String {
        return contents.joinToString("\n") { it }
    }
}

class Data(val tiles: ArrayList<Tile> = ArrayList()) {
    override fun toString(): String {
        return tiles.joinToString("\n") { it.toString() }
    }
}

fun main() {
    val puzzle = Puzzle20()
    try {
        val data = puzzle.readInputs("inputs.txt")
        val answer = puzzle.partA(data)
        println("Part A: $answer")

        val answer2 = puzzle.partB(data)
        println("Part B: $answer2")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}


class Puzzle20 {
    fun readInputs(filename: String): Data {
        val file = File(filename)
        val lines = file.readLines()
        val data = Data()

        var tile = Tile(0)
        lines.forEach { line ->
            if (line.startsWith("Tile ")) {
                tile = Tile(line.substringAfter("Tile ").substringBefore(':').toInt())
                data.tiles.add(tile)
            } else if (line.isNotEmpty()) {
                tile.contents.add(line)
            }
        }
        return data
    }

    fun partA(data: Data): Long {
        println("Overall size: ${data.tiles.size}")

        calculateBoundaries(data)
        val edges = data.tiles.filter { it.isEdge() }
        println("edges: ${edges.size}")

        val corners = data.tiles.filter { it.isCorner() }
        println("corners: ${corners.map { it.id }}")
        return corners.fold(1L) { acc, it -> acc * it.id }
    }

    private fun calculateBoundaries(data: Data) {
        for (x in data.tiles.indices) {
            for (y in x + 1 until data.tiles.size) {
                val tile1 = data.tiles[x]
                val tile2 = data.tiles[y]
                tile1.allBoundaries().forEach() { boundary1 ->
                    tile2.allBoundaries().forEach() { boundary2 ->
                        if (boundary1.str == boundary2.str || boundary1.reversed == boundary2.reversed) {
                            addMatch(tile1, tile2, boundary1, boundary2, Match.NORMAL)
                        } else if (boundary1.reversed == boundary2.str || boundary1.str == boundary2.reversed) {
                            addMatch(tile1, tile2, boundary1, boundary2, Match.FLIP)
                        }
                    }
                }
            }
        }
    }

    private fun addMatch(tile1: Tile, tile2: Tile, boundary1: Boundary, boundary2: Boundary, match: Match) {
        boundary1.addMatch(tile2, boundary2, match)
        boundary2.addMatch(tile1, boundary1, match)
    }

    fun partB(data: Data): Int {
        val corners = data.tiles.filter { it.isCorner() }
        println("corners: ${corners.map { it.id }}")

        val puzzle = assemblePuzzle(data, corners)
        val str = makeStringFromPuzzle(puzzle)
        val numberSeaMonsters = findSeaMonsters(str)
        return numberSeaMonsters
    }

    fun rotateFlip(tile: Tile, pred: (tile: Tile) -> Boolean) : Tile {
        var ret = rotateUntil(tile, pred)
        if (!pred(tile)) {
            tile.flipNorthSouth()
            ret = rotateUntil(tile, pred)
        }
        return ret
    }

    fun rotateUntil(tile: Tile, pred: (tile: Tile) -> Boolean) : Tile {
        for (i in 0 until 4) {
            if (pred(tile)) {
                break
            }
            tile.rotate()
        }
        return tile
    }

    fun assemblePuzzle(data: Data, corners: List<Tile>): Puzzle {
        val puzzle = Puzzle()
        val upperLeft = rotateFlip(corners.first()) { it.north().isEdge() && it.west().isEdge() }
        val n = data.tiles[0].contents.size

        val firstRow = fillRow(upperLeft, n) ?: throw RuntimeException("Could not fill first row")
        puzzle.add(firstRow)
        for (x in 1 until n) {
            val first = findNextSouth(puzzle[x - 1][0].south()) ?: throw RuntimeException("Could not find first at row ${x}")
            val row = fillRow(first, n) ?: throw RuntimeException("Could not fill row $x")
            puzzle.add(row)
        }
        return puzzle
    }

    private fun fillRow(first: Tile, n: Int) : ArrayList<Tile>?{
        val row = ArrayList<Tile>()
        row.add(first)
        var next = first
        for (x in 1 until n) {
            next = findNextEast(next.east()) ?: return null
            row.add(next)
        }
        return row
    }

    private fun findNextEast(east: Boundary) : Tile? {
        val matchTiles = east.matches
        if (matchTiles.size == 0) {
            return null
        }
        val matchTile = matchTiles[0].tile
        return rotateFlip(matchTile) { east.str == matchTile.west().str }
    }

    private fun findNextSouth(south: Boundary) : Tile? {
        val matchTiles = south.matches
        if (matchTiles.size == 0) {
            return null
        }
        val matchTile = matchTiles[0].tile
        return rotateFlip(matchTile) { south.str == matchTile.north().str }
    }

    fun makeStringFromPuzzle(puzzle: Puzzle) : List<String> {
        // avoid edges
        val n = puzzle.size
        val ret = ArrayList<String>()
        for (i in 1 until n - 1) {
            val tileRow = puzzle[i]
            for (index in 0 until n) {
                var str = ""
                for (j in 1 until n - 1) {
                    val tile: Tile = tileRow[j]
                    str += tile.contents[index]
                }
                ret.add(str)
            }
        }
        return ret
    }

    private fun findSeaMonsters(strs: List<String>) : Int {
        val tile = Tile(0, ArrayList(strs))
        rotateFlip(tile) { findSeaMonstersInList(it.contents) != 0 }
        return findSeaMonstersInList(tile.contents)
    }

    private fun findSeaMonstersInList(strs: List<String>): Int {
        val seaMonster =
            listOf(
                "                  # ",
                "#    ##    ##    ###",
                " #  #  #  #  #  #   "
            )

        var matches = 0
        for (i in 1 until strs.size - 1) {
            var middleIndex = 0
            do {
                middleIndex = findSingleLine(strs[i], seaMonster[1], middleIndex)
                if (middleIndex > 0) {
                    if (matchAtIndex(strs[i + 1], seaMonster[2], middleIndex)) {
                        if (matchAtIndex(strs[i - 1], seaMonster[0], middleIndex)) {
                            ++matches
                        }
                    }
                    ++middleIndex
                }
            } while (middleIndex >= 0)
        }
        return matches
    }

    private fun findSingleLine(str: String, seaMonsterLine: String, start: Int) : Int {
        for (y in start until str.length) {
            if (y + seaMonsterLine.length >= str.length) {
                return -1
            }

            if (matchAtIndex(str, seaMonsterLine, y)) {
                return y
            }
        }
        return -1
    }

    private fun matchAtIndex(str: String, seaMonsterLine: String, index: Int): Boolean {
        var found = true
        val substr = str.substring(index, index+seaMonsterLine.length)
        for (x in seaMonsterLine.indices) {
            if (seaMonsterLine[x] == '#' && substr[x] != '#') {
                found = false
                break
            }
        }
        return found
    }
}