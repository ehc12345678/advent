package com.advent2021.puzzle16

import com.advent2021.base.Base
import java.math.BigDecimal
import kotlin.math.min

typealias Data = ArrayList<String>
typealias Solution = Int
typealias Solution2 = BigDecimal

class State(
    val binary: String,
    var position: Int = 0
) {
    fun consumeAsStr(len: Int): String = binary.substring(position, min(binary.length, position + len)).also { position += len }
    fun consumeAsInt(len: Int) : Int = Integer.parseInt(consumeAsStr(len), 2)
    fun consumeAsBool() : Boolean = consumeAsStr(1) == "1"
    fun more(len: Int) = position + len < binary.length
    fun end() = position >= binary.length
}

data class Packet(
    val header: Header,
    var literal: Long? = null,
    val subPackets: ArrayList<Packet> = ArrayList()
)
data class Header(val version: Int, val typeId: Int)

fun main() {
    try {
        val puz = Puzzle16()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle16 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(toBinaryString(line))
    }

    override fun computeSolution(data: Data): Solution {
        val state = State(data[0])
        val packet = parsePacket(state)
        return sumVersions(packet)
    }

    override fun computeSolution2(data: Data): Solution2 {
        val state = State(data[0])
        val packet = parsePacket(state)
        return calcValue(packet)
    }

    private fun parsePacket(state: State): Packet {
        val header = parseHeader(state)
        val packet = when (header.typeId) {
            4 -> parseLiteral(header, state)
            else -> parseOperator(header, state)
        }
        return packet
    }

    private fun parseHeader(state: State): Header {
        val version = state.consumeAsInt(3)
        val typeId = state.consumeAsInt(3)
        return Header(version, typeId)
    }

    private fun parseLiteral(header: Header, state: State): Packet {
        var workingBinary = ""
        var isEnd: Boolean
        do {
            isEnd = !state.consumeAsBool()
            workingBinary += state.consumeAsStr(4)
        } while(!isEnd)

        return Packet(header).apply { literal = workingBinary.toLong(2) }
    }

    fun parseOperator(header: Header, state: State): Packet {
        val packet = Packet(header)
        val mode = state.consumeAsBool()
        if (!mode) {
            val length = state.consumeAsInt(15)
            val substr = state.consumeAsStr(length)
            val substate = State(substr)
            while (!substate.end()) {
                packet.subPackets.add(parsePacket(substate))
            }
        } else {
            val numSubpackets = state.consumeAsInt(11)
            for (i in 0 until numSubpackets) {
                packet.subPackets.add(parsePacket(state))
            }
        }
        return packet
    }

    fun sumVersions(packet: Packet): Solution {
        return packet.header.version + packet.subPackets.sumOf { sumVersions(it) }
    }

    fun calcValue(packet: Packet): Solution2 {
        val kids = packet.subPackets
        return when (packet.header.typeId) {
            4 -> BigDecimal.valueOf(packet.literal!!)
            0 -> kids.map { calcValue(it) }.reduce { acc, it -> acc.add(it) }
            1 -> kids.map { calcValue(it) }.reduce { acc, it -> acc.multiply(it) }
            2 -> kids.map { calcValue(it) }.reduce { acc, it -> if (it < acc) it else acc }
            3 -> kids.map { calcValue(it) }.reduce { acc, it -> if (it > acc) it else acc }
            5 -> if (calcValue(kids[0]) > calcValue(kids[1])) BigDecimal.ONE else BigDecimal.ZERO
            6 -> if (calcValue(kids[0]) < calcValue(kids[1])) BigDecimal.ONE else BigDecimal.ZERO
            7 -> if (calcValue(kids[0]) == calcValue(kids[1])) BigDecimal.ONE else BigDecimal.ZERO
            else -> throw IllegalArgumentException("Unrecognized type")
        }
    }

    fun toBinaryString(hexString: String): String {
        val str = hexString + (if ((hexString.length % 2) != 0) "0" else "")
        return str.chunked(2).map { it.toInt(16) }.joinToString("") {
            var s = Integer.toBinaryString(it)
            while (s.length < 8) {
                s = "0$s"
            }
            s
        }
    }
}

