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
            val time = current.time
            val name = current.valveName
            val score = current.score
            val opened = current.opened

            val cacheKey = Pair(time, name)
            if (cache.getOrDefault(cacheKey, -1) >= score) {
                // we cannot do any better, so skip it
                continue
            }
            cache[cacheKey] = score

            if (time == 30) {
                best = max(best, score)
                continue
            }

            val valve = data[name]!!
            if (valve.flowRate > 0 && !opened.contains(name)) {
                val newOpened = opened + name
                val newScore = score + newOpened.sumOf { data[it]!!.flowRate }
                stack.add(State(time + 1, name, newScore, newOpened))
            }

            val newScore = score + opened.sumOf { data[it]!!.flowRate }
            valve.children.forEach { child ->
                stack.add(State(time + 1, child, newScore, opened))
            }
        }
        return best
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

