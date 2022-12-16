package com.advent2022.puzzle16

import com.advent2021.base.Base
import kotlin.math.max

class Node(val name: String, val flowRate: Int, tunnels: List<String>) {
    val children = ArrayList<String>(tunnels.map { it.trim() })

    // the best flow rate we can get on this node given the number of steps left
    val bestFlowRate = HashMap<Int, Int>()

    override fun toString(): String {
        return "$name=$flowRate"
    }
}
typealias Data = HashMap<String, Node>
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle16()
        val solution1 = puz.solvePuzzle("inputsTest.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputsTest.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle16 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val valve = line.substringAfter("Valve ").substringBefore(" has")
        val flowRate = line.substringAfter("flow rate=").substringBefore(";").toInt()
        val tunnels = line.substringAfter("to valve").substringAfter(" ").split(",")
        data[valve] = Node(valve, flowRate, tunnels)
    }

    override fun computeSolution(data: Data): Solution {
        val node: Node = data["AA"]!!
        val solution = findBestPath(node, setOf(node.name), 30, data)
        return solution
    }

    private fun findBestPath(node: Node, openValves: Set<String>, numStepsLeft: Int, data: Data): Solution {
        return when (numStepsLeft) {
            0 -> 0
            1 -> {
                if (openValves.contains(node.name)) {
                    0
                } else {
                    node.flowRate
                }
            }
            else -> {
                node.bestFlowRate.getOrPut(numStepsLeft) {
                    val dontOpen = findChildrenBest(node, openValves, numStepsLeft - 1, data)
                    if (openValves.contains(node)) {
                        dontOpen
                    } else {
                        val openTheValve = (numStepsLeft - 1) * node.flowRate +
                                findChildrenBest(node, openValves + node.name, numStepsLeft - 2, data)
                        max(openTheValve, dontOpen)
                    }
                }
            }
        }
    }

    private fun findChildrenBest(node: Node, openValves: Set<String>, numStepsLeft: Int, data: Data): Int {
        if (node.children.isEmpty()) {
            return 0
        }
        return node.children.maxOf {
            val child = data[it]!!
            findBestPath(child, openValves, numStepsLeft, data)
        }
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

