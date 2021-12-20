package com.advent2021.puzzle19

import com.advent2021.base.Base
import kotlin.math.abs

data class Point3D(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Point3D): Point3D = Point3D(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Point3D): Point3D = Point3D(x - other.x, y - other.y, z - other.z)
    operator fun times(other: Point3D): Point3D = Point3D(x * other.x, y * other.y, z * other.z)
    fun absolute(): Point3D = Point3D(abs(x), abs(y), abs(z))
}
data class Scanner(val number: Int) {
    var points: ArrayList<Point3D> = ArrayList()
}
typealias Data = ArrayList<Scanner>
typealias Solution = Int
typealias Solution2 = Solution
typealias PointTransform = (pt: Point3D) -> Point3D

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
        //val rest = ArrayList(data.subList(1, data.size))
        val rest = ArrayList(listOf(data[1], data[4], data[2], data[3]))

        val beacons = LinkedHashSet<Point3D>(first.points)
        val foundScanners = HashMap<Scanner, Point3D>()
        foundScanners[first] = Point3D(0, 0, 0)
        while (rest.isNotEmpty()) {
            for (scanner in rest) {
                var foundIt = false
                for (foundScanner in foundScanners.keys) {
                    val match = matchDiffs(foundScanner, scanner)
                    if (match != null) {
                        val offset = foundScanners[foundScanner]!!
                        beacons.addAll(match.first.map { it + offset })
                        foundScanners[scanner] = offset + match.second
                        foundIt = true
                        break
                    }
                }
                if (foundIt) {
                    rest.remove(scanner)
                    break
                } else {
                    println("Could not find a scanner")
                }
            }
        }
        println(beacons.sortedBy { it.x }.joinToString("\n"))
        return beacons.size
    }

    private fun matchDiffs(
        firstScanner: Scanner,
        secondScanner: Scanner
    ): Pair<List<Point3D>, Point3D>? {
        val firstPoints = LinkedHashSet<Point3D>(firstScanner.points.sortedByDescending { it.z })
        val pointTransforms: List<PointTransform> = listOf(
            { Point3D(it.x, it.y, it.z) }, { Point3D(it.x, it.z, it.y) },
            { Point3D(it.y, it.x, it.z) }, { Point3D(it.y, it.z, it.x) },
            { Point3D(it.z, it.x, it.y) }, { Point3D(it.z, it.y, it.x) }
        )
        for (firstPt in firstScanner.points) {
            val deltasFirst = firstPoints.map { it - firstPt }
            for (transform in transforms) {
                for (pointTransform in pointTransforms) {
                    val secondPoints = LinkedHashSet<Point3D>(
                        secondScanner.points.sortedByDescending { it.z }.map { pointTransform(it * transform) })
                    for (secondPt in secondPoints) {
                        val deltasSecond = secondPoints.map { it - secondPt }
                        val intersects = deltasFirst.intersect(deltasSecond)
                        if (intersects.size >= 12) {
                            val firstScannerView = intersects.map { it + firstPt }
                            val secondScannerView = intersects.map { (it + secondPt) * transform }
                            //val secondScannerPos = firstScannerView.first() - (secondScannerView.first() * transform)
                            val secondScannerPos = findConsistentDiff(firstScannerView, secondScannerView)
                            val allSecondBecons = secondPoints.map { it * transform }
                            return Pair(ArrayList(allSecondBecons), secondScannerPos)
                        }
                    }
                }
            }
        }
        return null
    }

    // TODO: there is a bug in this code that gets the signs wrong
    private fun findConsistentDiff(firstScannerView: List<Point3D>, secondScannerView: List<Point3D>): Point3D {
        for (transform in transforms) {
            val diff = firstScannerView.first() + (secondScannerView.first() * transform)
            val nextDiff = firstScannerView[1] + (secondScannerView[1] * transform)
            if (diff == nextDiff) {
                return diff
            }
        }
        throw IllegalArgumentException("No consistent diff")
    }
    
    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

