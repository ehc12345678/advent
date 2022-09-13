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
        val puz = Puzzle4()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle4 : Base<Data, Solution?, Solution2?>() {
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

/**
 * Bingo is played on a set of boards each consisting of a 5x5 grid of numbers. Numbers are chosen at random, and the chosen number is marked on all boards on which it appears. (Numbers may not appear on all boards.) If all numbers in any row or any column of a board are marked, that board wins. (Diagonals don't count.)

The submarine has a bingo subsystem to help passengers (currently, you and the giant squid) pass the time. It automatically generates a random order in which to draw numbers and a random set of boards (your puzzle input). For example:

7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
8  2 23  4 24
21  9 14 16  7
6 10  3 18  5
1 12 20 15 19

3 15  0  2 22
9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
2  0 12  3  7
After the first five numbers are drawn (7, 4, 9, 5, and 11), there are no winners, but the boards are marked as follows (shown here adjacent to each other to save space):

22 13 17 11  0         3 15  0  2 22        14 21 17 24  4
8  2 23  4 24         9 18 13 17  5        10 16 15  9 19
21  9 14 16  7        19  8  7 25 23        18  8 23 26 20
6 10  3 18  5        20 11 10 24  4        22 11 13  6  5
1 12 20 15 19        14 21 16 12  6         2  0 12  3  7
After the next six numbers are drawn (17, 23, 2, 0, 14, and 21), there are still no winners:

22 13 17 11  0         3 15  0  2 22        14 21 17 24  4
8  2 23  4 24         9 18 13 17  5        10 16 15  9 19
21  9 14 16  7        19  8  7 25 23        18  8 23 26 20
6 10  3 18  5        20 11 10 24  4        22 11 13  6  5
1 12 20 15 19        14 21 16 12  6         2  0 12  3  7
Finally, 24 is drawn:

22 13 17 11  0         3 15  0  2 22        14 21 17 24  4
8  2 23  4 24         9 18 13 17  5        10 16 15  9 19
21  9 14 16  7        19  8  7 25 23        18  8 23 26 20
6 10  3 18  5        20 11 10 24  4        22 11 13  6  5
1 12 20 15 19        14 21 16 12  6         2  0 12  3  7
At this point, the third board wins because it has at least one complete row or column of marked numbers (in this case, the entire top row is marked: 14 21 17 24 4).

The score of the winning board can now be calculated. Start by finding the sum of all unmarked numbers on that board; in this case, the sum is 188. Then, multiply that sum by the number that was just called when the board won, 24, to get the final score, 188 * 24 = 4512.

To guarantee victory against the giant squid, figure out which board will win first. What will your final score be if you choose that board?
*/
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

    /**
On the other hand, it might be wise to try a different strategy: let the giant squid win.

You aren't sure how many bingo boards a giant squid could play at once, so rather than waste time counting its arms, the safe thing to do is to figure out which board will win last and choose that one. That way, no matter which boards it picks, it will win for sure.

In the above example, the second board is the last to win, which happens after 13 is eventually called and its middle column is completely marked. If you were to keep playing until this point, the second board would have a sum of unmarked numbers equal to 148 for a final score of 148 * 13 = 1924.

Figure out which board will win last. Once it wins, what would its final score be?
     */
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