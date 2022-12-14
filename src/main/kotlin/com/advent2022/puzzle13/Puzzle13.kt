package com.advent2022.puzzle13

import com.advent2021.base.Base
import kotlin.collections.ArrayDeque
import kotlin.collections.ArrayList

open class PacketData
class NumPacketData(val num: Int): PacketData() {
    override fun toString(): String {
        return num.toString()
    }

    fun asList(): ListPacketData = ListPacketData().also { it.list.add(this) }
}
class ListPacketData(val list: ArrayList<PacketData> = ArrayList()) : PacketData() {
    override fun toString(): String {
        return list.toString()
    }
}
typealias PacketPair = Pair<PacketData?, PacketData?>
class Data {
    val packetPairs = ArrayList<PacketPair>()
}

typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle13()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        puz.current = PacketPair(null, null)
        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle13 : Base<Data, Solution?, Solution2?>() {
    var current = PacketPair(null, null)
    override fun parseLine(line: String, data: Data) {
        if (line.isEmpty()) {
            current = PacketPair(null, null)
        } else {
            if (current.first == null) {
                current = PacketPair(stringToPacket(line), null)
            } else if (current.second == null) {
                current = PacketPair(current.first, stringToPacket(line))
                data.packetPairs.add(current)
            }
        }
    }

    fun String.toCharQueue() = ArrayDeque(toList())
    private fun stringToPacket(str: String): PacketData {
        return parseList(str.toCharQueue())
    }

    private fun parseList(queue: ArrayDeque<Char>): ListPacketData {
        val ret = ListPacketData()
        queue.removeFirst() // '['
        while (queue.first() != ']') {
            if (queue.first() == ',') {
                queue.removeFirst() // ','
            }
            ret.list.add(parseItem(queue))
        }
        queue.removeFirst() // ']'
        return ret
    }

    private fun parseItem(queue: ArrayDeque<Char>): PacketData {
        val first = queue.first()
        return when {
            first.isDigit() -> parseNumber(queue)
            first == '[' -> parseList(queue)
            else -> throw IllegalArgumentException("Something bad happened $first")
        }
    }

    private fun parseNumber(queue: ArrayDeque<Char>): NumPacketData {
        var str = ""
        while (queue.first().isDigit()) {
            str += queue.removeFirst()
        }
        return NumPacketData(str.toInt())
    }

    override fun computeSolution(data: Data): Solution {
        var ret = 0
        val comparator = PacketComparator()
        data.packetPairs.forEachIndexed { index, pair ->
            if (comparator.compare(pair.first!!, pair.second!!) < 0) {
                ret += index + 1
            }
        }
        return ret
    }
    
    override fun computeSolution2(data: Data): Solution2 {
        val comparator = PacketComparator()
        val markPacket2 = stringToPacket("[[2]]")
        val markPacket6 = stringToPacket("[[6]]")
        val allPackets = data.packetPairs.map { listOf(it.first!!, it.second!!) }.flatten().toMutableList()
        allPackets.add(markPacket2)
        allPackets.add(markPacket6)

        val sorted = allPackets.sortedWith(comparator)
        val index2 = sorted.indexOf(markPacket2) + 1
        val index6 = sorted.indexOf(markPacket6) + 1
        return index2 * index6
    }

    class PacketComparator: Comparator<PacketData> {
        override fun compare(first: PacketData?, second: PacketData?): Int {
            return if (first is NumPacketData) {
                if (second is NumPacketData) {
                    compareNumbers(first, second)
                } else {
                    compareLists(first.asList(), second as ListPacketData)
                }
            } else {
                if (second is NumPacketData) {
                    compareLists(first as ListPacketData, second.asList())
                } else {
                    compareLists(first as ListPacketData, second as ListPacketData)
                }
            }
            throw IllegalArgumentException("What is $first!!!")
        }

        fun compareNumbers(first: NumPacketData, second: NumPacketData): Int {
            return first.num.compareTo(second.num)
        }

        fun compareLists(first: ListPacketData, second: ListPacketData): Int {
            var i = 0
            while (i < first.list.size && i < second.list.size) {
                val cmp = compare(first.list[i], second.list[i])
                if (cmp != 0) {
                    return cmp
                }
                ++i
            }
            return when {
                i < second.list.size -> -1
                i < first.list.size -> 1
                else -> 0
            }
        }
    }

}

