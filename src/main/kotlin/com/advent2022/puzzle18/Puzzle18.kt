package com.advent2022.puzzle18

import com.advent2021.base.Base
import kotlin.math.max
import kotlin.math.min

data class Point3d(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Point3d): Point3d = Point3d(x + other.x, y + other.y, z + other.z)
}
data class Plane(val pointLowerLeft: Point3d, val pointUpperRight: Point3d)

typealias Data = HashSet<Point3d>
typealias Solution = Int
typealias Solution2 = Solution

fun String.toPoint3d(): Point3d {
    val parts = split(",").map { it.toInt() }
    return Point3d(parts[0], parts[1], parts[2])
}

class State2(val allSeenPlanes: Set<Plane>, val data: Data) {
    val boundMin = data.reduce { acc, point3d ->
        Point3d(min(acc.x, point3d.x), min(acc.y, point3d.y), min(acc.z, point3d.z))
    } + Point3d(-1, -1, -1)
    val boundMax = data.reduce { acc, point3d ->
        Point3d(max(acc.x, point3d.x), max(acc.y, point3d.y), max(acc.z, point3d.z))
    } + Point3d(1, 1, 1)

    val seenWater = HashSet<Point3d>()
    val touchedByWater = HashSet<Plane>()
}

fun main() {
    try {
        val puz = Puzzle18()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle18 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line.toPoint3d())
    }

    override fun computeSolution(data: Data): Solution {
        return planesCanSee(data).size
    }

    override fun computeSolution2(data: Data): Solution2 {
        val state = State2(planesCanSee(data), data)
        var spreadingWater = HashSet<Point3d>()
        addWater(state, spreadingWater, state.boundMin)

        while (spreadingWater.isNotEmpty()) {
            spreadingWater = spreadWater(spreadingWater, state)
        }

        return state.touchedByWater.size
    }

    private fun planesCanSee(data: Data): HashSet<Plane> {
        val planesCanSee = HashSet<Plane>()

        data.forEach { pt ->
            val thisPlanes = getCubePlanes(pt)
            val occluded = thisPlanes.intersect(planesCanSee)

            planesCanSee.removeAll(occluded)
            planesCanSee.addAll(thisPlanes - occluded)
        }
        return planesCanSee
    }

    fun getCubePlanes(pt: Point3d): Set<Plane> {
        return setOf(
            Plane(pt,                    pt + Point3d(0, 1, 1)),
            Plane(pt + Point3d(0, 0, 1), pt + Point3d(1, 1, 1)),
            Plane(pt,                    pt + Point3d(1, 1, 0)),
            Plane(pt + Point3d(1, 0, 0), pt + Point3d(1, 1, 1)),
            Plane(pt + Point3d(0, 1, 0), pt + Point3d(1, 1, 1)),
            Plane(pt,                    pt + Point3d(1, 0, 1))
        )
    }

    fun addWater(state: State2, spreadingWater: HashSet<Point3d>, pt: Point3d) {
        if (canAddWater(state, spreadingWater, pt)) {
            spreadingWater.add(pt)

            val waterPlanes = getCubePlanes(pt)
            val intercect = state.allSeenPlanes.intersect(waterPlanes)
            state.touchedByWater.addAll(intercect)
            state.seenWater.add(pt)
        }
    }

    fun spreadWater(spreadingWater: HashSet<Point3d>, state: State2): HashSet<Point3d> {
        var ret = HashSet<Point3d>()
        spreadingWater.forEach { pt ->
            getNeighbors(pt).forEach {
                addWater(state, ret, it)
            }
        }
        return ret
    }

    fun canAddWater(state: State2, spreadingWater: HashSet<Point3d>, pt: Point3d): Boolean {
        if (pt.x in state.boundMin.x .. state.boundMax.x
            && pt.y in state.boundMin.y .. state.boundMax.y
            && pt.z in state.boundMin.z .. state.boundMax.z) {
            return !(spreadingWater.contains(pt) || state.seenWater.contains(pt) || state.data.contains(pt))
        }
        return false
    }

    fun getNeighbors(pt: Point3d): List<Point3d> {
        // no diagnols
        return listOf(
            pt + Point3d(-1,0,0), // left
            pt + Point3d(0,-1,0), // top
            pt + Point3d(0,0,-1), // back
            pt + Point3d(1,0,0),  // right
            pt + Point3d(0,1,0),  // bottom
            pt + Point3d(0,0,1),  // top
        )
    }
}

