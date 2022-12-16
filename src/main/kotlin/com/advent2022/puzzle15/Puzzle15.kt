package com.advent2022.puzzle15

import com.advent2021.base.Base
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Pos(val x: Int, val y: Int) {
    operator fun minus(other: Pos): Pos = Pos(x - other.x, y - other.y)
}
data class SensorBeacon(val sensor: Pos, val beacon: Pos)

typealias Data = ArrayList<SensorBeacon>
typealias Solution = Int
typealias Solution2 = Long

fun main() {
    try {
        val puz = Puzzle15()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle15 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val sensorPos = toPos(line.substringAfter("Sensor at ").substringBefore(":"))
        val beaconPos = toPos(line.substringAfter("closest beacon is at "))
        data.add(SensorBeacon(sensorPos, beaconPos))
    }

    override fun computeSolution(data: Data): Solution {
        val gridY = 2000000
        val ranges = findNotPossibleBeacons(data, emptyList(), gridY)
        var answer = ranges.sumOf { it.count() }
        val beaconsOnLine =
            data.map { it.beacon }.filter { pos -> pos.y == gridY && ranges.any { it.contains(pos.x) } }.toSet()
        answer -= beaconsOnLine.size
        return answer
    }


    override fun computeSolution2(data: Data): Solution2 {
        val maxCoordinate = 4000000

        for (y in 0..maxCoordinate) {
            val ranges = findNotPossibleBeacons(data, emptyList(), y)
            val clipped = simplifyAndClipRanges(ranges, 0, maxCoordinate)
            if (clipped.size > 1) {
                val x = clipped.first().last + 1
                return (x * 4000000L) + y
            }
        }
        return 0
    }

    private fun findNotPossibleBeacons(
        data: Data,
        ranges: List<IntRange>,
        gridY: Int
    ): List<IntRange> {
        var ret = ranges
        data.forEach {
            val range = getRangeThatCannotBeBeaconAtY(gridY, it.sensor, it.beacon)
            ret = addRange(range, ret)
        }
        return ret
    }

    private fun getRangeThatCannotBeBeaconAtY(gridY: Int, sensor: Pos, beacon: Pos): IntRange {
        val sensorBeaconDiff = manhanttanDistance(sensor, beacon)
        val intercept = manhanttanDistance(Pos(sensor.x, gridY), sensor)
        val diff = sensorBeaconDiff - intercept
        return if (diff >= 0) {
            // the beacon cannot be anywhere in this because the sensor would have detected it as closer
            (sensor.x - diff)..(sensor.x + diff)
        } else {
            IntRange.EMPTY
        }
    }

    private fun addRange(newRange: IntRange, existing: List<IntRange>): List<IntRange> {
        if (newRange.isEmpty()) {
            return existing
        }

        // -----xxxxx----yyyyy----zzzzz----
        // --aaa---------------------------
        // --aaaaaa
        // --aaaaaaaaaa--

        val overlappingRanges = existing.filter { overlap(it, newRange) || touchingRanges(it, newRange)}
        val nonOverlappingNewRanges = existing.filter { !overlap(it, newRange) }
        val newRanges = ArrayList(nonOverlappingNewRanges)
        if (overlappingRanges.isNotEmpty()) {
            newRanges.add(overlappingRanges.fold(newRange) { acc, intRange ->
                min(acc.first, intRange.first) .. max(acc.last, intRange.last)
            })
        } else {
            newRanges.add(newRange)
        }
        return newRanges.sortedBy { it.first }
    }

    private fun touchingRanges(range1: IntRange, range2: IntRange): Boolean {
        return range1.last + 1 == range2.first || range2.last + 1 == range1.first
    }

    private fun toPos(str: String): Pos {
        val x = str.substringAfter("x=").substringBefore(",").toInt()
        val y = str.substringAfter("y=").toInt()
        return Pos(x, y)
    }

    private fun manhanttanDistance(pos1: Pos, pos2: Pos): Int {
        val diff = pos1 - pos2
        return abs(diff.x) + abs(diff.y)
    }
    fun overlap(
        range1: IntRange,
        range2: IntRange
    ): Boolean {
        return range1.first in range2 || range1.last in range2 || range2.first in range1 || range2.last in range1
    }

    fun simplifyAndClipRanges(ranges: List<IntRange>, minVal: Int, maxVal: Int): List<IntRange> {
        var newRanges: List<IntRange> = emptyList()
        ranges.forEach {
            newRanges = addRange(clipRange(it, minVal, maxVal), newRanges)
        }
        return newRanges
    }

    fun clipRange(range: IntRange, minVal: Int, maxVal: Int): IntRange {
        return max(range.first, minVal) .. min(range.last, maxVal)
    }
}

