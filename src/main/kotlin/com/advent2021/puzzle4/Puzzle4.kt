package com.advent2021.puzzle4

import com.advent2021.base.Base
import java.io.File
import kotlin.collections.ArrayList

data class Square(val row: Int, val col: Int, val number: Int, var checked: Boolean = false)
typealias Row = ArrayList<Square>
class Board() {
    constructor(board: Board): this() {
        board.squares.forEach { row ->
            row.forEach { sq -> addSquare(sq.row, sq.col, sq.number, sq.checked) }
        }
    }

    val squares = ArrayList<Row>()
    val squareMap = HashMap<Int, Square>()

    fun addSquare(r: Int, c: Int, num: Int, checked: Boolean = false) {
        if (r >= size) {
            squares.add(Row())
        }
        val square = Square(r, c, num, checked)
        row(r).add(square)
        squareMap[num] = square
    }

    fun square(r: Int, c: Int) = squares[r][c]
    fun row(r: Int) = squares[r]
    fun row(sq: Square) = row(sq.row)
    fun col(c: Int) = squares.map { it[c] }
    fun col(sq: Square) = col(sq.col)
    fun diagl(sq: Square) =
        if (sq.row != sq.col) {
            null
        } else {
            squares.mapIndexed { index, row -> square(row[index].row, index) }
        }

    fun diagr(sq: Square) =
        if (sq.row + sq.col != size + 1) {
            null
        } else {
            squares.mapIndexed { index, row -> square(size - row[index].row - 1, index) }
        }
    val size: Int
        get() = squares.size

    fun bingo(squares: List<Square>?) = squares?.all { it.checked } == true

    fun drawNumber(num: Int): Boolean {
        val square = squareMap[num] ?: return false
        square.checked = true
        val rowBingo = bingo(row(square))
        val colBingo = bingo(col(square))
        val diaglBingo = bingo(diagl(square))
        val diagrBingo = bingo(diagr(square))
        val bingo = rowBingo || colBingo || diaglBingo || diagrBingo
        return bingo
    }

    fun sumUnmarked(): Int = squares.sumOf { row -> row.sumOf { if (it.checked) 0 else it.number } }
}

class Data {
    val boards = ArrayList<Board>()
    var numbers: List<Int> = ArrayList()
}
typealias Solution = Int
typealias Solution2 = Int

fun main() {
    try {
        val puz = Puzzle3()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle3 : Base<Data, Solution?, Solution2?>() {
    override fun readInput(filename: String, data: Data, parseLineFunc: (String, Data) -> Unit): Data {
        val file = File(filename)
        val lines = file.readLines()
        data.numbers = lines[0].split(",").map { it.toInt() }
        var row = 0
        for (i in 1 until lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) {
                data.boards.add(Board())
                row = 0
            } else {
                line.split("\\s+".toRegex()).forEachIndexed { col, s ->
                    data.boards.last().addSquare(row, col, s.toInt())
                }
                ++row
            }
        }
        return data
    }

    override fun parseLine(line: String, data: Data) = Unit

    override fun computeSolution(data: Data): Solution {
        data.numbers.forEach { number ->
            data.boards.forEach { board ->
                if (board.drawNumber(number)) {
                    return number * (board.sumUnmarked())
                }
            }
        }
        return -1
    }

    override fun computeSolution2(data: Data): Solution2 {
        var numBoards = 0
        data.numbers.forEach { number ->
            data.boards.forEach { board ->
                if (board.drawNumber(number)) {
                    ++numBoards
                    if (numBoards >= data.boards.size) {
                        return number * board.sumUnmarked()
                    }
                }
            }
        }
        return -1
    }

}