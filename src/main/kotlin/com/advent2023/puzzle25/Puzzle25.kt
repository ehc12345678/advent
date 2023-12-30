package com.advent2023.puzzle25

import com.advent2023.base.Base

import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph

typealias Data = SimpleWeightedGraph<String, DefaultWeightedEdge>
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle25()
        val solution1 = puz.solvePuzzle("inputs.txt", Data(DefaultWeightedEdge::class.java))
        println("Solution1: $solution1")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle25 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val (src, dests) = line.split(":").map { it.trim() }
        data.addVertex(src)
        dests.split(" ").forEach { dest ->
            data.addVertex(dest)
            data.addEdge(src, dest)
        }
    }

    override fun computeSolution(data: Data): Solution {
        val minCut = StoerWagnerMinimumCut(data).minCut().size
        val otherCut = data.vertexSet().size - minCut
        return minCut * otherCut
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

