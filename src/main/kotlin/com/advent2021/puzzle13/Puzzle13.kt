package com.advent2021.puzzle13

import com.advent2021.base.Base
import com.advent2021.puzzle5.Point
import com.advent2021.puzzle5.toPoint

data class Fold(val fold: Int, val isXFold: Boolean)
class Data(
    val points: HashSet<Point> = HashSet(),
    val folds: ArrayList<Fold> = ArrayList()
) {
    fun height(): Int = points.maxOf { it.y }
    fun width(): Int = points.maxOf { it.x }

    fun printAsString(): String {
        val buf = StringBuffer()
        for (y in 0..height()) {
            for (x in 0..width()) {
                buf.append(if (points.contains(Point(x, y))) { "#" } else { "." })
            }
            buf.append("\n")
        }
        return buf.toString()
    }
}
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle13()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle13 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        when {
            line.startsWith("fold along ") -> {
                val rest = line.substring("fold along ".length)
                val parts = rest.split("=")
                data.folds.add(Fold(parts[1].toInt(), parts[0] == "x"))
            }

            line.isNotEmpty() -> data.points.add(line.toPoint())
        }
    }

    override fun computeSolution(data: Data): Solution {
        val newData = doFold(data, data.folds.first())
        return newData.points.size
    }

    override fun computeSolution2(data: Data): Solution2 {
        var newData = data
        data.folds.forEach { newData = doFold(newData, it) }
        println(newData.printAsString())
        return newData.points.size
    }

    fun doFold(data: Data, fold: Fold): Data {
        val points = HashSet<Point>()
        if (fold.isXFold) {
            data.points.forEach {
                if (it.x < fold.fold) {
                    points.add(it)
                } else {
                    points.add(Point(fold.fold - (it.x - fold.fold), it.y))
                }
            }
        } else {
            data.points.forEach {
                if (it.y < fold.fold) {
                    points.add(it)
                } else {
                    points.add(Point(it.x, fold.fold - (it.y - fold.fold)))
                }
            }
        }
        return Data(points, data.folds)
    }
}

