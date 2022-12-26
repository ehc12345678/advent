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

    fun canOpen(valve: Valve) = valve.flowRate > 0 && !opened.contains(valveName)
    fun totalFlow(data: Data) = opened.totalFlow(data)
}

data class DftState(val time: Int, val valveName: String, val helper: Boolean, val opened: Set<String> = HashSet()) {
    fun canOpen(valve: Valve) = valve.flowRate > 0 && !opened.contains(valveName)
    fun totalFlow(data: Data) = opened.totalFlow(data) * (time - 1)
}
typealias DftCache = HashMap<DftState, Int>

fun Set<String>.totalFlow(data: Data) = sumOf { data[it]!!.flowRate }

class Puzzle16 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val valve = line.substringAfter("Valve ").substringBefore(" has")
        val flowRate = line.substringAfter("flow rate=").substringBefore(";").toInt()
        val tunnels = line.substringAfter("to valve").substringAfter(" ").split(",")
        data[valve] = Valve(valve, flowRate, tunnels)
    }

    override fun computeSolution(data: Data): Solution {
//        val rootState = State(CacheKey(1, "AA", "AA"), 0)
//        return solve(data, rootState, 30, false).score
        val rootState = DftState(30, "AA")
        return dftSolve(rootState, DftCache(), data, false)
    }

    override fun computeSolution2(data: Data): Solution2 {
        val rootState = State(CacheKey(1, "AA", "AA"), 0)
        val firstState = solve(data, rootState, 30, false)
        val secondState = solve(data, State(CacheKey(1, "AA", "AA"), firstState.score, firstState.opened), 26, false)
        return secondState.score
    }

    private fun solve(data: Data, rootState: State, numTurns: Int, elephantHelper: Boolean): State {
        val cache = HashMap<CacheKey, Int>()

        val stack = ArrayDeque<State>()
        stack.add(rootState)

        var best: State? = rootState
        while (stack.isNotEmpty()) {
            val current = stack.removeFirst()

            if (cache.getOrDefault(current.key, -1) >= current.score) {
                // if we have seen this state before and that time we were already better, we cannot do better
                continue
            }
            cache[current.key] = current.score

            if (best == null || current.score > best.score) {
                best = current
            }

            if (current.time < numTurns) {
                val valve = data[current.valveName]!!
                if (current.canOpen(valve)) {
                    val newOpened = current.opened + valve.name
                    val newScore = current.score + newOpened.totalFlow(data)
                    stack.add(State(CacheKey(current.time + 1, valve.name, current.elephantValve), newScore, newOpened))
                }

                val newScore = current.score + current.totalFlow(data)
                valve.children.forEach { child ->
                    stack.add(State(CacheKey(current.time + 1, child, current.elephantValve), newScore, current.opened))
                }

                if (elephantHelper) {
                    val elephantValve = data[current.elephantValve]!!
                    if (current.canOpen(elephantValve)) {
                        val newOpened = current.opened + elephantValve.name
                        val newElephantScore = current.score + newOpened.totalFlow(data)
                        val state = State(CacheKey(current.time + 1, current.valveName, elephantValve.name), newElephantScore, newOpened)
                        stack.add(state)
                    }

                    val newElephantScore = current.score + current.totalFlow(data)
                    valve.children.forEach { child ->
                        stack.add(State(CacheKey(current.time + 1, current.valveName, child), newElephantScore, current.opened))
                    }
                }
            }
        }
        return best!!
    }

    fun dftSolve(state: DftState, cache: DftCache, data: Data, elephantHelper: Boolean): Int {
        if (state.time == 0 && elephantHelper) {
            val beginState = DftState(26, "AA", false, state.opened)
            return dftSolve(beginState, cache, data, false)
        }
        return cache.getOrPut(state) {
            val valve = data[state.valveName]!!
            var best = 0
            if (state.canOpen(valve)) {
                // open the valve
                val openValveState = DftState(state.time - 1, valve.name, elephantHelper, state.opened + valve.name)
                best = openValveState.totalFlow(data) + dftSolve(openValveState, cache, data, elephantHelper)
            }
            // don't open the valve, go to neighbors
            val bestChild = valve.children.maxOf { child ->
                val moveState = DftState(state.time - 1, child, elephantHelper, state.opened)
                dftSolve(moveState, cache, data, elephantHelper)
            }
            best = max(best, bestChild)
            cache[state] = best
            return best
        }
    }
}

