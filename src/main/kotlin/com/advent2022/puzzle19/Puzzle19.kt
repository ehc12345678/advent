package com.advent2022.puzzle19

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

typealias Solution = Int
typealias Solution2 = Solution

enum class Ingrediant(val rank: Int) { ore(1), clay(2), obsidian(3), geode(4) }
data class CostToMake(var make: Ingrediant, val costs: Map<Ingrediant, Int>)
typealias Recipe = Map<Ingrediant, CostToMake>
typealias Data = ArrayList<Recipe>

fun Recipe.costs(ingrediant: Ingrediant) = this[ingrediant]!!.costs
fun Recipe.twiceCosts(ingrediant: Ingrediant) =
    costs(ingrediant).map { (ingrediant, cost) -> ingrediant to cost * 2 }.toMap()

data class Tracker(var iteration: Int, var savings: Int = 0) {
    fun inc() {
        iteration++
        if (iteration % 1000000 == 0) {
            println("$iteration with savings=$savings")
        }
    }

    fun save() {
        savings++
    }
}
var gTracker = Tracker(0)

data class State(
    val recipe: Recipe,
    var numTurns: Int,
    val materials: MutableMap<Ingrediant, Int> = EnumMap(Ingrediant::class.java),
    val robots: MutableMap<Ingrediant, Int> = EnumMap(Ingrediant::class.java),
    var score: Int = 0
) {
    fun numMaterials(ingrediant: Ingrediant) = materials.getOrDefault(ingrediant, 0)
    fun numRobots(ingrediant: Ingrediant) = robots.getOrDefault(ingrediant, 0)

    fun buildRobot(ingrediant: Ingrediant) {
        if (ingrediant == Ingrediant.geode) {
            score += (numTurns - 1)
        }
        robots[ingrediant] = numRobots(ingrediant) + 1
    }

    fun payCost(ingrediant: Ingrediant, cost: Int) {
        materials[ingrediant] = numMaterials(ingrediant) - cost
    }

    fun payCosts(costs: Map<Ingrediant, Int>) {
        costs.forEach { (ingrediant, cost) -> payCost(ingrediant, cost) }
    }

    val bestPossible: Int
        get() = score + ((numTurns * (numTurns - 1)) / 2)

    // we know this is the best we can do, so just resolve to the best
    fun toLeastPossible(): State = State(recipe, 0, materials, robots, score)
}

fun String.toCosts(): Map<Ingrediant, Int> {
    return split(" and ").map {
        val parts = it.split(" ")
        Ingrediant.valueOf(parts[1]) to parts[0].toInt()
    }.toMap()
}

fun main() {
    try {
        val puz = Puzzle19()
        val solution1 = puz.solvePuzzle("inputsTest.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle19 : Base<Data, Solution?, Solution2?>() {
    var verbose = true

    override fun parseLine(line: String, data: Data) {
        val recipe = Ingrediant.values().map {
            CostToMake(it, line.substringAfter("Each $it robot costs ").substringBefore(".").toCosts())
        }.associateBy { it.make }

        data.add(recipe)
    }

    override fun computeSolution(data: Data): Solution {
        val numTurns = 24
        val initialRobots = mutableMapOf(Ingrediant.ore to 1)
        val recipeMax = data.map {
            maxForRecipe(State(it, numTurns, robots = initialRobots))
        }
        val repipeValues = recipeMax.mapIndexed { index, it -> (index + 1) * it.score }
        return repipeValues.sum()
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }

    private fun maxForRecipe(state: State): State {
        var bestState = state
        gTracker.inc()
        while (bestState.numTurns > 0) {
            val dontBuild = collectMaterials(bestState)
            val buildableRobots = Ingrediant.values()
                .filter { ingrediant -> canBuild(bestState, bestState.recipe.costs(ingrediant)) }
            val build = buildableRobots
                .filter { ingrediant -> shouldBuild(ingrediant, bestState) }
                .map { ingrediant -> buildRobot(ingrediant, bestState) }
                .map { collectMaterials(it) }

            val allPossibilities = build + dontBuild

            val filtered = allPossibilities.filter { canPossiblyBeat(it, bestState) }
            if (filtered.isEmpty()) {
                // optimization.. we cannot best the best state, so we are already at the best
                bestState = bestState.toLeastPossible()
                gTracker.save()
            }

            bestState = if (filtered.size == 1) {
                // if there is only one possibilitiy, iterate, don't recurse
                filtered[0]
            } else {
                val recurse =  filtered.map { maxForRecipe(it) }
                val bestNextState = recurse.reduce { acc, it ->
                    if (acc.score > it.score) acc else it
                }
                bestNextState
            }
        }

        return bestState
    }

    // we can build it if we have the materials
    private fun canBuild(state: State, costs: Map<Ingrediant, Int>): Boolean {
        return costs.entries.all { (ingrediant, cost) -> state.numMaterials(ingrediant) >= cost }
    }

    private fun shouldBuild(ingrediant: Ingrediant, state: State): Boolean {
        // if we ever have twice as many materials as we need, then we didn't build it last time, so don't build it now
        if (canBuild(state, state.recipe.twiceCosts(ingrediant))) {
            gTracker.save()
            return false
        }

        val ret = when (state.numTurns) {
            0 -> false
            1 -> ingrediant == Ingrediant.geode
            2 -> TURN_BEFORE_LAST.contains(ingrediant)
            else -> true
        }
        if (!ret) {
            gTracker.save()
        }
        return ret
    }

    private fun filterOutDontBuild(dontBuild: State, buildable: List<Ingrediant>): Boolean {
        return buildable.isNotEmpty() // if we cannot build anything, sure, don't build
    }

    private fun buildRobot(ingrediant: Ingrediant, state: State): State {
        val newState = State(state.recipe, state.numTurns, HashMap(state.materials), HashMap(state.robots))
        newState.payCosts(state.recipe[ingrediant]!!.costs)
        newState.buildRobot(ingrediant)
        return newState
    }

    private fun collectMaterials(state: State): State {
        // optimization... we reuse the state here for mutation... *caution*
        state.numTurns--
        state.robots.forEach { (key, value) ->
            state.materials[key] = state.numMaterials(key) + value
        }
        return state
    }

    private fun canPossiblyBeat(newState: State, state: State): Boolean {
        // if we cannot generate enough geodes to possibly beat the best score, then we should give up
        if (newState.bestPossible < state.score) {
            gTracker.save()
            return false
        }
        return true
    }

    fun log(str: String) {
        if (verbose) {
            println(str)
        }
    }

    companion object {
        val TURN_BEFORE_LAST = setOf(Ingrediant.ore, Ingrediant.obsidian, Ingrediant.geode)
    }
}

