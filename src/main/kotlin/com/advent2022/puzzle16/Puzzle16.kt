package com.advent2022.puzzle16

import com.advent2021.base.Base
import kotlin.math.max

class Valve(val name: String, val flowRate: Int, tunnels: List<String>) {
    val children = ArrayList<String>(tunnels.map { it.trim() })

    override fun toString(): String {
        return "$name=$flowRate"
    }
}
typealias Data = HashMap<String, Valve>
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle16()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputsTest.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

data class State(val time: Int, val valveName: String, val score: Int, val opened: Set<String> = HashSet())
class Puzzle16 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val valve = line.substringAfter("Valve ").substringBefore(" has")
        val flowRate = line.substringAfter("flow rate=").substringBefore(";").toInt()
        val tunnels = line.substringAfter("to valve").substringAfter(" ").split(",")
        data[valve] = Valve(valve, flowRate, tunnels)
    }

    override fun computeSolution(data: Data): Solution {
        val rootState = State(1, "AA", 0)
        val cache = HashMap<Pair<Int, String>, Int>()

        val stack = ArrayDeque<State>()
        stack.add(rootState)

        var best = 0
        while (stack.isNotEmpty()) {
            val current = stack.removeFirst()

            val cacheKey = Pair(current.time, current.valveName)
            if (cache.getOrDefault(cacheKey, -1) >= current.score) {
                // we cannot do any better, so skip it
                continue
            }
            cache[cacheKey] = current.score

            if (current.time == 30) {
                best = max(best, current.score)
                continue
            }

            val valve = data[current.valveName]!!
            if (valve.flowRate > 0 && !current.opened.contains(current.valveName)) {
                val newOpened = current.opened + current.valveName
                val newScore = current.score + newOpened.sumOf { data[it]!!.flowRate }
                stack.add(State(current.time + 1, current.valveName, newScore, newOpened))
            }

            val newScore = current.score + current.opened.sumOf { data[it]!!.flowRate }
            valve.children.forEach { child ->
                stack.add(State(current.time + 1, child, newScore, current.opened))
            }
        }
        return best
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

