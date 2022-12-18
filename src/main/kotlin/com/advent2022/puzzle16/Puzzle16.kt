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

data class CacheKey(val time: Int, val valveName: String, val elephantValve: String)
data class State(
    val key: CacheKey,
    val score: Int,
    val opened: Set<String> = HashSet()
) {
    val time: Int
        get() = key.time
    val valveName: String
        get() = key.valveName
    val elephantValve: String
        get() = key.elephantValve
}

class Puzzle16 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val valve = line.substringAfter("Valve ").substringBefore(" has")
        val flowRate = line.substringAfter("flow rate=").substringBefore(";").toInt()
        val tunnels = line.substringAfter("to valve").substringAfter(" ").split(",")
        data[valve] = Valve(valve, flowRate, tunnels)
    }

    override fun computeSolution(data: Data): Solution {
        return solve(data, 30, false)
    }

    override fun computeSolution2(data: Data): Solution2 {
        return solve(data, 26, true)
    }

    private fun solve(data: Data, numTurns: Int, elephantHelper: Boolean): Int {
        val rootState = State(CacheKey(1, "AA", "AA"), 0)
        val cache = HashMap<CacheKey, Int>()

        val stack = ArrayDeque<State>()
        stack.add(rootState)

        var best = 0
        while (stack.isNotEmpty()) {
            val current = stack.removeFirst()

            if (cache.getOrDefault(current.key, -1) >= current.score) {
                // if we have seen this valve before and that time we were already better, we cannot do better
                continue
            }
            cache[current.key] = current.score

            best = max(best, current.score)
            if (current.time < numTurns) {
                val valve = data[current.valveName]!!
                if (valve.flowRate > 0 && !current.opened.contains(current.valveName)) {
                    val newOpened = current.opened + current.valveName
                    val newScore = current.score + newOpened.sumOf { data[it]!!.flowRate }
                    stack.add(State(CacheKey(current.time + 1, current.valveName, current.elephantValve), newScore, newOpened))
                }

                val newScore = current.score + current.opened.sumOf { data[it]!!.flowRate }
                valve.children.forEach { child ->
                    stack.add(State(CacheKey(current.time + 1, child, current.elephantValve), newScore, current.opened))
                }

                if (elephantHelper) {
                    val elephantValve = data[current.elephantValve]!!
                    if (elephantValve.flowRate > 0 && !current.opened.contains(current.elephantValve)) {
                        val newOpened = current.opened + current.elephantValve
                        val newScore = current.score + newOpened.sumOf { data[it]!!.flowRate }
                        stack.add(State(CacheKey(current.time + 1, current.valveName, current.elephantValve), newScore, newOpened))
                    }

                    val newElephantScore = current.score + current.opened.sumOf { data[it]!!.flowRate }
                    valve.children.forEach { child ->
                        stack.add(State(CacheKey(current.time + 1, current.valveName, child), newElephantScore, current.opened))
                    }
                }
            }
        }
        return best
    }
}

