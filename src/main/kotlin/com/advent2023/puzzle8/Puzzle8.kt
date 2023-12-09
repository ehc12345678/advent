package com.advent2023.puzzle8

import com.advent2023.base.Base
import java.math.BigDecimal

typealias Solution = Int
typealias Solution2 = BigDecimal

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

class Node(val key: String, val left: String, val right: String) {
    companion object {
        fun fromStr(str: String): Node {
            val regex = Regex("""(.*) = \((.*), (.*)\)""")
            val match = regex.find(str)!!
            val (key, left, right) = match.destructured
            return Node(key, left, right)
        }
    }
    override fun toString(): String = "$key = ($left, $right)"
}

class Data {
    var instructions: List<Char> = emptyList()
    val nodes = HashMap<String, Node>()

    fun addNode(node: Node) {
        nodes[node.key] = node
    }

    fun node(key: String) = nodes[key]!!

    fun numStepsToReach(src: String, dest: String): Int {
        var count = 0
        var node = node(src)
        while (node.key != dest) {
            val newNode = runInstructions(node)
            node = newNode
            count += instructions.size
        }
        return count
    }

    fun runInstructions(srcNode: Node): Node {
        return instructions.fold(srcNode) { n, left_or_right ->
            node(if (left_or_right == 'L') n.left else n.right)
        }
    }

    fun findPathLength(start: String, nodes_map: Map<String, String>): Int {
        val path = ArrayList<String>()
        val set = HashSet<String>()

        var node = start
        while (!set.contains(node)) {
            set.add(node)
            path.add(node)
            val dest = nodes_map[node]!!
            node = dest
        }

        return path.indexOfFirst { value -> value.endsWith('Z') }
    }
}

class Puzzle8 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        if (data.instructions.isEmpty()) {
            data.instructions = line.toCharArray().toList()
        } else if (line.isNotEmpty()) {
            data.addNode(Node.fromStr(line))
        }
    }

    override fun computeSolution(data: Data): Solution {
        return data.numStepsToReach("AAA", "ZZZ")
    }
    
    override fun computeSolution2(data: Data): Solution2 {
        val startNodes = data.nodes.keys.filter { key -> key.endsWith('A') }
        val nodesMap = data.nodes.keys.associateWith {
            key -> data.runInstructions(data.node(key)).key
        }

        val allZs = startNodes.map { start -> data.findPathLength(start, nodesMap) }
        val multiple = allZs.fold(1L) { acc, i -> acc * i }
        return BigDecimal.valueOf(multiple).times(data.instructions.size.toBigDecimal())
    }
}

