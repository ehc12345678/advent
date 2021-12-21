package com.advent2021.puzzle19

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet
import kotlin.math.abs
import kotlin.math.max

data class Point3D(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Point3D): Point3D = Point3D(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Point3D): Point3D = Point3D(x - other.x, y - other.y, z - other.z)
    operator fun times(other: Point3D): Point3D = Point3D(x * other.x, y * other.y, z * other.z)
    operator fun compareTo(other: Point3D): Int = when {
        z != other.z -> { z.compareTo(other.z) }
        y != other.y -> { y.compareTo(other.y) }
        else -> { x.compareTo(x) }
    }
}
data class Scanner(val number: Int) {
    var points: ArrayList<Point3D> = ArrayList()
    var location: Point3D = Point3D(0, 0, 0)
}
typealias Data = ArrayList<Scanner>
typealias Solution = Int
typealias Solution2 = Solution
typealias PointTransform = (pt: Point3D) -> Point3D

var foundScanners = ArrayList<Scanner>()
fun main() {
    try {
        val puz = Puzzle19()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle19 : Base<Data, Solution?, Solution2?>() {
    val transforms: List<Point3D> = ArrayList<Point3D>().also { list ->
        listOf(1, -1).forEach { x ->
            listOf(1, -1).forEach { y ->
                listOf(1, -1).forEach { z ->
                    list.add(Point3D(x, y, z))
                }
            }
        }
    }

    override fun parseLine(line: String, data: Data) {
        when {
            line.startsWith("--- scanner") -> {
                data.add(Scanner(data.size))
            }
            line.isNotBlank() -> {
                val parts = line.split(",").map { it.toInt() }
                data.last().points.add(Point3D(parts[0], parts[1], parts[2]))
            }
        }
    }

    override fun computeSolution(data: Data): Solution {
        val first = data.first()
        val rest = data.filter { it != first }.toMutableList()
        val beacons = LinkedHashSet<Point3D>(first.points)
        foundScanners.add(first)

        while (rest.isNotEmpty()) {
            var match: Scanner? = null
            for (scanner in rest) {
                for (foundScanner in foundScanners) {
                    match = matchDiffs(foundScanner, scanner)
                    if (match != null) {
                        println("Scanner ${scanner.number} was a match for ${foundScanner.number}")
                        break
                    }
                }
                if (match != null) {
                    rest.remove(scanner)
                    foundScanners.add(match)
                    beacons.addAll(match.points)
                    break
                }
            }
            if (match == null) {
                println("Could not find a candidate")
                break
            }
        }
        return beacons.size
    }

    private fun matchDiffs(
        firstScanner: Scanner,
        secondScanner: Scanner
    ): Scanner? {
        val firstStack = createSortedPts(firstScanner.points)
        val pointTransforms: List<PointTransform> = listOf(
            { Point3D(it.x, it.y, it.z) }, { Point3D(it.x, it.z, it.y) },
            { Point3D(it.y, it.x, it.z) }, { Point3D(it.y, it.z, it.x) },
            { Point3D(it.z, it.x, it.y) }, { Point3D(it.z, it.y, it.x) }
        )
        while (firstStack.size >= 12) {
            val firstPt = firstStack.pop()
            val deltasFirst = firstStack.map { it - firstPt }
            for (transform in transforms) {
                for (pointTransform in pointTransforms) {
                    val secondStack = createSortedPts(secondScanner.points.map { pointTransform(it * transform) })
                    while (secondStack.size >= 12) {
                        val secondPt = secondStack.pop()
                        val deltasSecond = secondStack.map { it - secondPt }
                        val intersects = deltasFirst.intersect(deltasSecond)
                        if (intersects.size >= 11) {
                            val firstScannerView = intersects.map { it + firstPt }
                            val secondScannerView = intersects.map { (it + secondPt) * transform }
                            val secondScannerPos = firstScannerView.first() - (secondScannerView.first() * transform)
                            val allSecondBeacons = secondScanner.points.map { pointTransform(it * transform) + secondScannerPos }
                            return Scanner(secondScanner.number).also {
                                it.points = ArrayList(allSecondBeacons)
                                it.location = secondScannerPos
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    private fun createSortedPts(points: List<Point3D>): Stack<Point3D> {
        val comparator = compareBy<Point3D> { it.z }.thenBy { it.y }.thenBy { it.x }
        return Stack<Point3D>().also { it.addAll(points.sortedWith(comparator).reversed()) }
    }

    override fun computeSolution2(data: Data): Solution2 {
        var maxDistance = 0
        for (i in 0 until foundScanners.size - 1) {
            val firstScanner = foundScanners[i]
            for (j in i + 1 until foundScanners.size) {
                if (i != j) {
                    val secondScanner = foundScanners[j]
                    maxDistance = max(maxDistance, manhattanDistance(firstScanner.location, secondScanner.location))
                }
            }
        }
        return maxDistance
    }

    private fun manhattanDistance(pt1: Point3D, pt2: Point3D): Int {
        val diff = pt1 - pt2
        return abs(diff.x) + abs(diff.y) + abs(diff.z)
    }
}

