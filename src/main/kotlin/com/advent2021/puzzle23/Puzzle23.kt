package com.advent2021.puzzle23

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

data class Amphipod(
    val letter: Char,
    val roomOrder: Int? = null,
    val roomLetter: Char? = null,
    val hallPosition: Int? = null
) {
    // var room: Room? = null

    override fun toString(): String {
        return letter.toString()
    }
}

data class Room(
    val hallPosition: Int,
    val wantsLetter: Char,
    val amphipods: List<Amphipod?> = ArrayList(2)
) {
    fun solved(): Boolean {
        return amphipods[0]?.letter == wantsLetter && amphipods[1]?.letter == wantsLetter
    }

    val empty: Boolean
        get() = amphipods.all { it == null }

    fun canMoveIntoRoom(amphipod: Amphipod): Boolean {
        return amphipod.letter == wantsLetter && amphipods.all { it == null || it.letter == wantsLetter }
    }

    override fun toString(): String {
        return "$wantsLetter ($hallPosition): $amphipods"
    }
}

class PuzzleState(
    val rooms: List<Room>,
    val hall: List<Amphipod?>,
    val score: Long = 0L
) {
    fun solved(): Boolean {
        return rooms.all { it.solved() }
    }

    fun amphipods(): List<Amphipod> {
        return rooms.map { room -> room.amphipods.filterNotNull() }.flatten() + hall.filterNotNull()
    }

    override fun toString(): String {
        val buf = StringBuffer()
        buf.appendLine("#############")
        buf.append("#")
        for (h in hall) {
            val letter = h?.letter ?: '.'
            buf.append(letter)
        }
        buf.appendLine("#")
        buf.append("##")
        for (r in rooms) {
            val letter = r.amphipods[0]?.letter ?: '.'
            buf.append("#${letter}")
        }
        buf.appendLine("###")
        buf.append("  ")
        for (r in rooms) {
            val letter = r.amphipods[1]?.letter ?: '.'
            buf.append("#${letter}")
        }
        buf.appendLine("#")
        buf.appendLine("  #########  ")
        return buf.toString()
    }

    val lastHallPos: Int
        get() = hall.size
    fun amphipodInHall(pos: Int): Amphipod? = hall[pos - 1]

    fun placeAmphipodInHall(amphipod: Amphipod, hallPosition: Int, costToMove: Int): PuzzleState {
        val roomLetter = amphipod.roomLetter
        val room = getRoomByLetter(roomLetter) ?: return this

        val newAmphipod = amphipod.copy(roomOrder = null, roomLetter = null, hallPosition = hallPosition)
        val replaceRoom = room.copy(amphipods = ArrayList(room.amphipods.map { if (it == amphipod) null else it?.copy() }))
        val newRooms = ArrayList(rooms.map { if (it.wantsLetter == roomLetter) replaceRoom else it.copy() })

        val newHalls = ArrayList(hall.map { it?.copy() })
        newHalls[hallPosition - 1] = newAmphipod
        return PuzzleState(rooms = newRooms, hall = newHalls, score = score + costToMove)
    }

    fun findAmphipodHomeRoom(amphipod: Amphipod): Room {
        return rooms.find { it.wantsLetter == amphipod.letter }!!
    }

    fun placeAmphipodInRoom(amphipod: Amphipod, room: Room, roomOrder: Int, costToMove: Int): PuzzleState {
        val newAmphipod = amphipod.copy(roomOrder = roomOrder, roomLetter = room.wantsLetter, hallPosition = null)
        val newAmphipods = ArrayList(room.amphipods.mapIndexed { index, it ->
            if (index == roomOrder) { newAmphipod } else { it }
        })

        val newRooms = ArrayList(rooms.map {
            if (it == room) {
                it.copy(amphipods = newAmphipods)
            } else {
                it.copy(amphipods = it.amphipods.mapIndexed() { index, roomCopy ->
                    if (it.wantsLetter == amphipod.roomLetter && index == amphipod.roomOrder) { null } else { roomCopy }
                })
            }
        })

        val newHalls = ArrayList(hall.map { it?.copy() })
        if (amphipod.hallPosition != null) {
            newHalls[amphipod.hallPosition - 1] = null
        }
        return PuzzleState(rooms = newRooms, hall = newHalls, score = score + costToMove)
    }

    fun legal(): Boolean {
        val amphipods = amphipods()
        val counts = amphipods.groupingBy { it.letter }.eachCount().filter { it.value > 1 }
        return counts.getOrDefault('A', 0) == 2 &&
                counts.getOrDefault('B', 0) == 2 &&
                counts.getOrDefault('C', 0) == 2 &&
                counts.getOrDefault('D', 0) == 2
    }

    fun getRoomByLetter(ch: Char?): Room? {
        return rooms.find { it.wantsLetter == ch }
    }
}

data class Data(val rooms: ArrayList<Room> = ArrayList(),
                val hall: ArrayList<Amphipod?> = ArrayList(),
                var roomToAmphipod: HashMap<Room, ArrayList<Amphipod>> = HashMap()
) {
    fun toPuzzleState(): PuzzleState {
        return PuzzleState(rooms.map { it.copy(amphipods = roomToAmphipod[it]!!) }, hall)
    }
}
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle23()
        val solution1 = puz.solvePuzzle("inputsTest.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputsTest.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle23 : Base<Data, Solution?, Solution2?>() {
    val costs: Map<Char, Int> = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
    
    override fun parseLine(line: String, data: Data) {
        when {
            line == "#############" || line == "  #########" -> { }
            line.startsWith("#.") -> {
                for (ch in line.substring(1, line.length - 1)) {
                    data.hall.add(null)
                }
            }
            line.startsWith("###") -> {
                val chars = line.substring(3, line.length - 3).split("#")
                var hallPosition = 3

                chars.forEachIndexed { index, s ->
                    val room = Room(hallPosition, 'A' + index)
                    data.rooms.add(room)
                    data.roomToAmphipod.putIfAbsent(room, ArrayList<Amphipod>().also {
                        it.add(Amphipod(s[0], 0, room.wantsLetter, null))
                    })
                    hallPosition += 2
                }
            }
            line.startsWith("  #") -> {
                val chars = line.substring(3, line.lastIndexOf('#')).split("#")
                chars.forEachIndexed { index, s ->
                    val room = data.rooms[index]
                    data.roomToAmphipod[room]!!.add(Amphipod(s[0], 1, room.wantsLetter, null))
                }
            }
        }
    }

    override fun computeSolution(data: Data): Solution {
        val queue = PriorityQueue<PuzzleState>(1000) { state1, state2 ->
            state1.score.compareTo(state2.score)
        }
        queue.add(data.toPuzzleState())

        var steps = 0
        while (queue.isNotEmpty() && !queue.peek().solved()) {
            val top = queue.remove()
            if (top.score == 12521L) {
                val list = queue.filter { top.score == 12521L }
                println("Stop!")
            }
            val newLegalMoves = calcLegalMoves(top)
            queue.addAll(newLegalMoves)
            ++steps
            if ((steps % 1000) == 0) {
                println("Step $steps has best score of ${top.score}")
            }
        }
        println(data)
        return 0
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }

    fun calcLegalMoves(puzzleState: PuzzleState): List<PuzzleState> {
        return puzzleState.amphipods().map { calcLegalMoves(puzzleState, it) }.flatten()
    }

    fun calcLegalMoves(puzzleState: PuzzleState, amphipod: Amphipod): List<PuzzleState> {
        val room = puzzleState.getRoomByLetter(amphipod.roomLetter)

        val legalMoves = ArrayList<PuzzleState>()
        if (room != null) {
            var canMove = true
            if (amphipod.roomOrder == 1 && room.amphipods[0] != null) {
                canMove = false
            }
            if (canMove) {
                val homeRoom = puzzleState.findAmphipodHomeRoom(amphipod)
                val stepsIntoRoom = stepsToOtherPos(puzzleState, amphipod, room.hallPosition, homeRoom.hallPosition)
                if (homeRoom.canMoveIntoRoom(amphipod) && stepsIntoRoom > 0) {
                    val roomOrder: Int = if (homeRoom.empty) { 1 } else { 0 }
                    val totalCost = stepsIntoRoom + roomOrder + 1
                    val newState = puzzleState.placeAmphipodInRoom(amphipod, homeRoom, roomOrder, totalCost)
                    if (!newState.legal()) {
                        println("Problem")
                    }
                    legalMoves.add(newState)
                } else {
                    for (hallPosition in 1..puzzleState.lastHallPos) {
                        val stepsInHall = stepsToOtherPos(puzzleState, amphipod, room.hallPosition, hallPosition)
                        if (stepsInHall > 0) { // we can go
                            val totalSteps = amphipod.roomOrder!! + 1 + stepsInHall
                            val totalCost = costs[amphipod.letter]!! * totalSteps
                            val newState = puzzleState.placeAmphipodInHall(amphipod, hallPosition, totalCost)
                            if (!newState.legal()) {
                                println("Problem")
                            }
                            legalMoves.add(newState)
                        }
                    }
                }
            }
        } else if (amphipod.hallPosition != null) {
            val hallPosition = amphipod.hallPosition
            val homeRoom = puzzleState.findAmphipodHomeRoom(amphipod)
            if (homeRoom.canMoveIntoRoom(amphipod)) {
                val stepsInHall = stepsToOtherPos(puzzleState, amphipod, homeRoom.hallPosition, hallPosition)
                if (stepsInHall > 0) { // we can go
                    val roomOrder: Int = if (homeRoom.empty) { 1 } else { 0 }
                    val totalCost = stepsInHall + roomOrder + 1
                    val newState = puzzleState.placeAmphipodInRoom(amphipod, homeRoom, roomOrder, totalCost)
                    if (!newState.legal()) {
                        println("Problem")
                    }
                    legalMoves.add(newState)
                }
            }
        }
        return legalMoves
    }

    fun stepsToOtherPos(puzzleState: PuzzleState, amphipod: Amphipod, startPos: Int, endPos: Int): Int {
        if (startPos == endPos) {
            return 0
        }
        
        val delta = if (startPos > endPos) -1 else 1
        var steps = 0
        var hallPos = startPos

        do {
            val other = puzzleState.amphipodInHall(hallPos)
            if (other != null && other != amphipod) {
                return 0
            }
            hallPos += delta
            ++steps
        } while (hallPos != endPos)

        if (puzzleState.amphipodInHall(endPos) != null) {
            return 0
        }

        return abs(endPos - startPos)
    }

}

