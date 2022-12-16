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
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle15()
        val solution1 = puz.solvePuzzle("inputsTest.txt", Data())
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
        val ranges = ArrayList<IntRange>()
        data.forEach {
            var range = getRangeThatCannotBeBeaconAtY(10, it.sensor, it.beacon)
            addRange(range, ranges)
        }
        return ranges.sumOf { it.count() }
    }
    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }

    private fun getRangeThatCannotBeBeaconAtY(gridY: Int, sensor: Pos, beacon: Pos): IntRange {
        val sensorBeaconDiff = manhanttanDistance(sensor, beacon)
        val intercept = manhanttanDistance(Pos(sensor.x, gridY), sensor)
        val diff = intercept - sensorBeaconDiff
        return if (diff >= 0) {
            // the beacon cannot be anywhere in this because the sensor would have detected it as closer
            (sensor.x - diff)..(sensor.x + diff)
        } else {
            IntRange.EMPTY
        }
    }

    private fun addRange(newRange: IntRange, existing: MutableList<IntRange>) {
        if (newRange.isEmpty()) {
            return
        }

        //12345678901234567891
        //-----xxxx----------- // existing
        //---yyyyyyyyy-------- // new
        //---zzxxxxzzz-------- // split the ranges

        //----xxxxxxxx--------- // existing
        //------yyy------------ // new
        //

        //----xxxxxxxx--------- // existing
        //--yyyyyyyy----------- // new
        //--zzxxxxxxxx--------- // split only front

        //----xxxxxxxx--------- // existing
        //-------yyyyyyyy------ // new
        //----xxxxxxxxzzz------ // split only end

        var clippedRange = newRange
        existing.forEach {
            // if they overlap, clip it
            val clipStart = clippedRange.start .. min(it.start, clippedRange.endInclusive)
            if (!clipStart.isEmpty()) {
                existing.add(clipStart)
            }
            val clipEnd = it.endInclusive + 1 .. max(clippedRange.endInclusive, it.endInclusive + 1)
            if (!clipEnd.isEmpty()) {
                existing.add(clippedRange)
            }
        }
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
}

