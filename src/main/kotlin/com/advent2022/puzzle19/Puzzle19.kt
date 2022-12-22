package com.advent2022.puzzle19

import com.advent2021.base.Base

typealias Solution = Int
typealias Solution2 = Solution

enum class Ingrediant { ore, clay, obsidian, geode }
data class CostToMake(var make: Ingrediant, val costs: Map<Ingrediant, Int>)
typealias Recipe = Map<Ingrediant, CostToMake>
typealias Data = ArrayList<Recipe>

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
        val repipeValues = data.mapIndexed { index, map -> (index + 1) * maxForRecipe(map, numTurns) }
        return repipeValues.sum()
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }

    private fun maxForRecipe(recipe: Recipe, numTurns: Int): Int {
        val currentMaterials = HashMap<Ingrediant, Int>()
        val currentRobots = HashMap<Ingrediant, Int>()

        val clayBot = Ingrediant.ore
        currentRobots[clayBot] = 1

        for (turn in 1 ..numTurns) {
            log("== Minute $turn ==")
            val robot = maybeBuildRobot(currentMaterials, recipe, numTurns - turn, currentRobots)
            collectMaterials(currentMaterials, currentRobots, true)
            if (robot != null) {
                currentRobots[robot] = currentRobots.getOrDefault(robot, 0) + 1
                log("The new ${robot}-collecting robot is ready; you now have ${currentRobots[robot]} of them.")
            }
            log("")
        }
        return currentMaterials[Ingrediant.geode] ?: 0
    }

    private fun maybeBuildRobot(
        currentMaterials: HashMap<Ingrediant, Int>,
        recipe: Recipe,
        turnsLeft: Int,
        currentRobots: HashMap<Ingrediant, Int>
    ): Ingrediant? {
        for (ingrediant in listOf(Ingrediant.geode, Ingrediant.obsidian, Ingrediant.clay, Ingrediant.ore)) {
            if (canBuild(recipe, currentMaterials, ingrediant)) {
                if (shouldBuild(recipe, ingrediant, currentMaterials, currentRobots, turnsLeft)) {
                    return buildRobot(recipe, currentMaterials, ingrediant, true)
                }
            }
        }
        return null
    }

    // we can build it if we have the materials
    private fun canBuild(recipe: Recipe, currentMaterials: HashMap<Ingrediant, Int>, ingrediant: Ingrediant): Boolean {
        val costs = recipe[ingrediant]!!.costs
        return costs.entries.all { cost ->
            currentMaterials.getOrDefault(cost.key, 0) >= cost.value
        }
    }

    private fun buildRobot(recipe: Recipe, currentMaterials: HashMap<Ingrediant, Int>, ingrediant: Ingrediant,
                           forReal: Boolean): Ingrediant {
        val costs = recipe[ingrediant]!!.costs
        costs.forEach { (key, value) ->
            currentMaterials[key] = currentMaterials[key]!! - value
        }
        val costsStr = costs.entries.joinToString(" and ") { (key, value) -> "$value $key" }
        if (forReal) {
            log("Spend $costsStr to start building an $ingrediant-collecting robot.")
        }
        return ingrediant
    }

    private fun collectMaterials(currentMaterials: HashMap<Ingrediant, Int>, currentRobots: HashMap<Ingrediant, Int>,
                                 forReal: Boolean = false) {
        currentRobots.forEach { (key, value) ->
            currentMaterials[key] = currentMaterials.getOrDefault(key, 0) + value
            if (forReal) {
                log("$value $key-collecting robots collect $value $key; you now have ${currentMaterials[key]} $key.")
            }
        }
    }

    private fun shouldBuild(recipe: Recipe, ingrediant: Ingrediant, currentMaterials: HashMap<Ingrediant, Int>,
                            currentRobots: HashMap<Ingrediant, Int>, turnsLeft: Int): Boolean {
        val nextLevel = when (ingrediant) {
            Ingrediant.obsidian -> Ingrediant.geode
            Ingrediant.clay -> Ingrediant.obsidian
            Ingrediant.ore -> Ingrediant.clay
            else -> return true
        }

        val buildIt = HashMap(currentMaterials)
        val dontBuildIt = HashMap(currentMaterials)
        buildRobot(recipe, buildIt, ingrediant, false)

        var turn = turnsLeft
        while (turn-- > 0) {
            collectMaterials(dontBuildIt, currentRobots)
            if (canBuild(recipe, dontBuildIt, nextLevel)) {
                // if we can build the next level up before we can build another of these, say no
                return false
            }

            collectMaterials(buildIt, currentRobots)
            if (canBuild(recipe, buildIt, ingrediant)) {
                // we can build two of this type before the next one is built, so do it
                return true
            }
        }
        return true
    }

    fun log(str: String) {
        if (verbose) {
            println(str)
        }
    }
}

