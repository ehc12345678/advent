package com.advent2022.puzzle19

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

typealias Solution = Int
typealias Solution2 = Solution

enum class Ingrediant { ore, clay, obsidian, geode }
data class CostToMake(var make: Ingrediant, val costs: Map<Ingrediant, Int>)
typealias Recipe = Map<Ingrediant, CostToMake>
typealias Data = ArrayList<Recipe>

var numStates = 0
data class State(
    val recipe: Recipe,
    var numTurns: Int,
    val materials: MutableMap<Ingrediant, Int> = EnumMap(Ingrediant::class.java),
    val robots: MutableMap<Ingrediant, Int> = EnumMap(Ingrediant::class.java)
) {
    init {
       ++numStates
       if (numStates % 1000000 == 0) {
           println("$numStates: $score")
       }
    }

    fun numMaterials(ingrediant: Ingrediant) = materials.getOrDefault(ingrediant, 0)
    fun numRobots(ingrediant: Ingrediant) = robots.getOrDefault(ingrediant, 0)
    val score: Int
        get() = numMaterials(Ingrediant.geode)
    val bestPossible: Int
        get() = leastPossible + ((numTurns * (numTurns - 1)) / 2)

    val leastPossible: Int
        get() = score + (numTurns * numRobots(Ingrediant.geode))
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
        while (bestState.numTurns > 0) {
            val buildARobot = Ingrediant.values().filter { canBuild(it, bestState) }.map { buildRobot(it, bestState) }

            if (buildARobot.isEmpty()) {
                // optimization... if we cannot build a robot, just keep going (no recursion)
                bestState = collectMaterials(bestState)
            } else if (buildARobot.size == 1 && bestState.robots.size == 1) {
                // optimizaiton... if we can build a robot, and we don' thave robots, do that (no recursion)
                bestState = collectMaterials(buildARobot[0])
            } else {
                val buildPossibilities =
                    (buildARobot + bestState)
                        .filter { canPossiblyBeat(it, bestState)}
                        .map { maxForRecipe(collectMaterials(it)) }
                if (buildPossibilities.isEmpty()) {
                    // nothing good can happen from here, so the best state is the one we have
                    break
                }

                val bestNextState = buildPossibilities.reduce { acc, it -> if (acc.score > it.score) acc else it }
                bestState = bestNextState
            }
        }

        return bestState
    }

    // we can build it if we have the materials
    private fun canBuild(ingrediant: Ingrediant, state: State): Boolean {
        val costs = state.recipe[ingrediant]!!.costs
        val haveMaterials = costs.entries.all { cost -> state.materials.getOrDefault(cost.key, 0) >= cost.value }
        return when (state.numTurns) {
            0, 1 -> false
            2 -> ingrediant == Ingrediant.obsidian && haveMaterials
            3 -> ingrediant == Ingrediant.clay || ingrediant == Ingrediant.obsidian && haveMaterials
            else -> haveMaterials
        }
    }

    private fun buildRobot(ingrediant: Ingrediant, state: State): State {
        val costs = state.recipe[ingrediant]!!.costs
        val newState = State(state.recipe, state.numTurns, HashMap(state.materials), HashMap(state.robots))
        costs.forEach { (key, value) ->
            newState.materials[key] = newState.numMaterials(key) - value
        }
        newState.robots[ingrediant] = newState.numRobots(ingrediant) + 1
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

    private fun canPossiblyBeat(newState: State, state: State): Boolean =
        // if we cannot generate enough geodes to possibly beat the best score, then we should give up
        newState.bestPossible > state.leastPossible

    fun log(str: String) {
        if (verbose) {
            println(str)
        }
    }
}

