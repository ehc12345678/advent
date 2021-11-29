package com.advent.advent2020.puzzle3

import java.io.File
import java.math.BigDecimal

fun main() {
    val puzzle3 = Puzzle3()
    val inputs = puzzle3.readInputs("inputs.txt")
    println("Num trees: ${puzzle3.findTrees(3, 1, inputs)}")
    println("Num trees together: ${puzzle3.findMultiples(inputs)}")
}

class Puzzle3 {
    fun readInputs(filename: String): List<List<Boolean>> {
        val file = File(filename)
        val lines = file.readLines().map { parseLine(it) }
        return lines
    }

    fun parseLine(line: String) : List<Boolean> {
        val ret = ArrayList<Boolean>()
        line.trim().forEach { ret.add(it == '#') }
        return ret
    }

    fun findTrees(left: Int, down: Int, tree: List<List<Boolean>>) : Int {
        var x = left
        var y = down
        var numTrees = 0
        val width = tree[0].size
        while (y < tree.size) {
            if (tree[y][x % width]) {
                ++numTrees
            }
            x += left
            y += down
        }
        return numTrees
    }

    fun findMultiples(tree: List<List<Boolean>>) : BigDecimal {
        val find = listOf(
            findTrees(1, 1, tree),
            findTrees(3, 1, tree),
            findTrees(5, 1, tree),
            findTrees(7, 1, tree),
            findTrees(1, 2, tree)
        )
        var answer = BigDecimal(1)
        find.forEach {
            answer = answer.multiply(BigDecimal(it))
        }
        return answer
    }

}