package com.advent.puzzle13

import java.io.File
import java.math.BigDecimal

class Inputs(val startTime: Int, val busIds: List<Bus>)
data class Bus(val id: Int, val modTime: Int, val nextTime: Int)

fun main() {
    val puzzle = Puzzle13()
    try {
        val inputs = puzzle.readInputs("test.txt")
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
        return Inputs(lines[0].toInt(), lines[1].split(",").map {
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

    fun findFirstT(buses: List<Bus>): BigDecimal {
        // this uses the Chinese remainder theorem
        // n is the bus ids, the thing we are dividing by
        // b is remainder we are trying to hit
        // x is calculated
        val busWithIndex = buses.mapIndexed { idx, value -> Pair(idx, value.id) }.filter { it.second != 0 }

        val bigN = busWithIndex.fold(BigDecimal(1)) { acc, it -> acc * BigDecimal(it.second) }
        var total = BigDecimal(0)
        println("bigN ${bigN}")
        busWithIndex.forEach {
            val b = BigDecimal(it.first)
            val mod = BigDecimal(it.second)
            val ni = bigN / mod
            val xi = findModInverse(ni, mod)
            val totalThis = b * ni * xi
            println("${b}\t${ni}\t${xi}\t${totalThis}")
            total += totalThis
        }

        val answer = total % bigN
        println("--- checks --- ")
        busWithIndex.forEach {
            println("${answer} % ${it.second} = ${answer % BigDecimal(it.second)} which was expected ${it.first}")
        }

        return answer
    }

    fun findModInverse(ni: BigDecimal, mod: BigDecimal) : BigDecimal {
        val calc = (ni.toInt() % mod.toInt())
        for (i in 1..calc) {
            if (((i * calc) % mod.toInt()) == 1) {
                return BigDecimal(i)
            }
        }
        return BigDecimal(calc)
    }
}
