package com.advent2022.puzzle16

import com.advent2021.base.Base

class Valve(val name: String, val flowRate: Int, tunnels: List<String>) {
    val children = ArrayList<String>(tunnels.map { it.trim() })

    // the best flow rate we can get on this node given the number of steps left
    val bestFlowRate = HashMap<Int, Path>()

    override fun toString(): String {
        return "$name=$flowRate"
    }
}
typealias Data = HashMap<String, Valve>
typealias Solution = Int
typealias Solution2 = Solution

enum class ActionType { MOVE_TO, OPEN }
data class Action(val actionType: ActionType, val step: Int, val valve: Valve) {
    override fun toString(): String {
        return "You ${actionType.toString().toLowerCase()} valve ${valve.name}. (step $step)"
    }

    val score = if (actionType == ActionType.OPEN) (step - 1) * valve.flowRate else 0
}
class Path(val actions: ArrayDeque<Action> = ArrayDeque(), val openedValves: HashSet<String> = HashSet()) {
    val last: Action
        get() = actions.last()

    fun move(valve: Valve, step: Int) = pathWithAction(ActionType.MOVE_TO, valve, step)

    fun open(valve: Valve, step: Int): Path? {
        if (!canOpen(valve) || step <= 0) {
            return null
        }
        return pathWithAction(ActionType.OPEN, valve, step).also { it.openedValves.add(valve.name) }
    }

    private fun pathWithAction(actionType: ActionType, valve: Valve, step: Int) =
        Path(
            ArrayDeque(actions).also { it.add(Action(actionType, step, valve)) },
            HashSet(openedValves)
        )

    fun canOpen(valve: Valve) = valve.flowRate > 0 && !openedValves.contains(valve.name)
    fun add(path: Path): Path {
        return Path(
            ArrayDeque(actions + path.actions),
            HashSet(openedValves + path.openedValves)
        )
    }

    val score = actions.fold(0) { acc, action -> acc + action.score }
}

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
        data[valve] = Valve(valve, flowRate, tunnels)
    }

    override fun computeSolution(data: Data): Solution {
        val start: Valve = data["AA"]!!
        
        val path = Path().move(start, 0)
        val solution = findBestPath(start, path, 30, data)

        val openValves = ArrayList<String>()
        var totalFlowRate = 0
        solution.actions.forEachIndexed { index, action ->
            println("== Minute $index ==")
            if (openValves.isEmpty()) {
                println("No valves are open")
            } else {
                println("Valves $openValves are open, releasing $totalFlowRate pressure.")
            }
            if (action.actionType == ActionType.OPEN) {
                openValves.add(action.valve.name)
                totalFlowRate += action.valve.flowRate
            }
            println(action)
            println()
        }
        return solution.score
    }

    private fun findBestPath(valve: Valve, thisPath: Path, numStepsLeft: Int, data: Data): Path {
        return when (numStepsLeft) {
            0, 1 -> thisPath
            2 -> thisPath.open(valve, numStepsLeft) ?: thisPath.move(valve, numStepsLeft)
            else -> {
                valve.bestFlowRate.getOrPut(numStepsLeft) {
                    var currentBest: Path = thisPath
                    val openPath = thisPath.open(valve, numStepsLeft)
                    if (openPath != null) {
                        currentBest = bestChildrenPath(openPath, valve.children, numStepsLeft - 1, data)
                    }
                    val bestChildrenPath = bestChildrenPath(thisPath, valve.children, numStepsLeft, data)
                    bestPath(currentBest, bestChildrenPath)
                }
            }
        }
    }

    private fun bestChildrenPath(path: Path, children: List<String>, numStepsLeft: Int, data: Data): Path {
        return bestPathFromList(children.map { childName ->
            val child = data[childName]!!
            findBestPath(child, path.move(child, numStepsLeft), numStepsLeft - 1, data)
        })
    }

    private fun bestPathFromList(list: List<Path>) = list.reduce(::bestPath)
    private fun bestPath(path1: Path, path2: Path) = if (path1.score > path2.score) path1 else path2

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

