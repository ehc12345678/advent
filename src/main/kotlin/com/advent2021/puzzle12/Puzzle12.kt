package com.advent2021.puzzle12

import com.advent2021.base.Base
import org.springframework.util.LinkedMultiValueMap
import java.util.*

typealias Data = LinkedMultiValueMap<String, String>
data class Path(var items: List<String>, val visited: Set<String>, var visitedSmallCave: String? = null) {
    val ended: Boolean
        get() = last == "end"
    val last: String
        get() = items.last()

    override fun toString(): String = items.joinToString(",") { it }
}
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle12()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle12 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val nodes = line.split("-")
        val first = nodes[0]
        val second = nodes[1]

        data.add(first, second)
        if (second != "end") {
            data.add(second, first)
        }
    }

    override fun computeSolution(data: Data): Solution = computeImpl(data, false)
    override fun computeSolution2(data: Data): Solution2 = computeImpl(data, true)


    private fun computeImpl(data: Data, allowOneSmallCaveVisit: Boolean): Int {
        val finishedPaths: ArrayList<Path> = ArrayList()
        val unfinished = Stack<Path>()
        unfinished.add(Path(listOf("start"), setOf("start")))
        do {
            val top = unfinished.pop()
            val connected = data[top.last]!!
            for (next in connected) {
                if (allowVisitCave(top, next, allowOneSmallCaveVisit)) {
                    // only allow one small cave visit
                    val visitedSmallCave = top.visitedSmallCave ?:
                        if (top.visited.contains(next) && next == next.toLowerCase()) next else null
                    val newPath = Path(top.items + next, top.visited + next, visitedSmallCave)

                    if (newPath.ended) {
                        finishedPaths.add(newPath)
                    } else {
                        unfinished.push(newPath)
                    }
                }
            }
        } while (unfinished.isNotEmpty())

        return finishedPaths.size
    }

    private fun allowVisitCave(path: Path, cave: String, allowOneSmallCaveVisit: Boolean): Boolean {
        return when {
            // upper case caves can be revisited
            !(cave == cave.toLowerCase()) -> true

            // start cave can never be revisited
            cave == "start" -> false

            // if we have never seen this cave, we are ok
            !path.visited.contains(cave) -> true

            // if we have seen this cave and we are not allowed at least one small cave visit, not ok
            !allowOneSmallCaveVisit -> false

            // if we have seen a small cave already, bail
            path.visitedSmallCave != null -> false

            // otherwise we are good, but only this once
            else -> true
        }
    }

}

