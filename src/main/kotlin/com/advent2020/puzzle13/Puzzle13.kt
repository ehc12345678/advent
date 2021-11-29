package com.advent.advent2020.puzzle13

import java.io.File
import java.math.BigInteger

class Inputs(val busIds: List<Bus>)
data class Bus(val id: Int, val modTime: Int, val nextTime: Int)

fun main() {
    val puzzle = Puzzle13()
    try {
        val inputs = puzzle.readInputs("inputs.txt")
        val possibleBuses = inputs.busIds.filter { it.id != 0 }
        val bus = possibleBuses.minByOrNull { it.nextTime }!!
        println("Answer is ${bus.id * bus.modTime}")

        val findFirstT = puzzle.findFirstT(inputs.busIds)
        println("First t is $findFirstT")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle13 {
    fun readInputs(filename: String): Inputs {
        val file = File(filename)
        val lines = file.readLines()
        val time = lines[0].toInt()
        return Inputs(lines[1].split(",").map {
            calcTimes(if (it == "x") 0 else it.toInt(), time)
        })
    }

    fun calcTimes(busId: Int, time: Int): Bus {
        if (busId != 0) {
            val modTime = busId - (time % busId)
            val nextTime = time + modTime
            return Bus(busId, modTime, nextTime)
        }
        return Bus(busId, 0, 0)
    }

    fun findFirstT(buses: List<Bus>): BigInteger {
        // this uses the Chinese remainder theorem
        // n is the bus ids, the thing we are dividing by
        // b is remainder we are trying to hit
        // x is calculated
        val busWithIndex =
            buses.mapIndexed { idx, value -> Pair(idx, value.id.toBigInteger()) }.filter { it.second.toLong() != 0L }

        val bigN = busWithIndex.fold(BigInteger.valueOf(1)) { acc, it -> acc.multiply(it.second) }
        var total = BigInteger.valueOf(0)
        println("bigN ${bigN}")

        println("id\tb\tni\t\txi\ttotal")
        busWithIndex.forEach {
            val busId = it.second
            val b = (busId - it.first.toBigInteger())
            val ni = bigN / busId
            val xi = ni.modInverse(busId)
            val totalThis = b * ni * xi
            println("${busId}\t${b}\t${ni}\t${xi}\t${totalThis}")
            total += totalThis
        }

        return total % bigN
    }
}
