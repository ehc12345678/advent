package com.advent2021.puzzle23

import java.lang.IllegalStateException
import kotlin.math.max
import kotlin.math.min

enum class WantsToMove { Left, Right, None }

data class Amphipod(
    val letter: Char ,
    val position: Position
) {
    override fun toString(): String {
        return letter.toString()
    }

    fun move(newPosition: Position) = copy(position = newPosition)

    fun wantsToMove(): WantsToMove {
        val delta: Int
        if (position.inHall) {
            val homeHallPosition = 3 + ('D' - letter) * 2
            delta = position.hallPosition!! - homeHallPosition
        } else {
            delta = position.roomLetter!! - letter
        }

        return if (delta < 0) {
            WantsToMove.Left
        } else if (delta > 0) {
            WantsToMove.Right
        } else {
            WantsToMove.None
        }
    }
}


data class Position(
    val roomOrder: Int? = null,
    val roomLetter: Char? = null,
    val hallPosition: Int? = null
) {
    val inRoom: Boolean
        get() = roomLetter != null
    val inHall: Boolean
        get() = hallPosition != null

    override fun toString(): String {
        if (inRoom) {
            return "room $roomLetter: $roomOrder"
        }
        return "hall $hallPosition"
    }
}

data class Room(
    val position: Position,
    val wantsLetter: Char,
    val amphipods: List<Amphipod?> = ArrayList(2)
) {
    val height: Int
        get() = amphipods.size

    fun solved(): Boolean {
        return amphipods.all { it?.letter == wantsLetter }
    }

    fun removeAmphipod(amphipod: Amphipod): Room {
        val newAmphipods = amphipods.map { if (amphipod == it) null else it?.copy() }
        return copy(amphipods = newAmphipods)
    }

    fun addAmphipod(amphipod: Amphipod, newPosition: Position): Room {
        if (newPosition.roomLetter != wantsLetter) {
            throw IllegalStateException("Cannot move an amphipod into a room that is not its own")
        }
        val newAmphipod = amphipod.copy(position = newPosition)
        val newAmphipods = amphipods.mapIndexed { index, it ->  if (index == newPosition.roomOrder) newAmphipod else it?.copy() }
        return copy(amphipods = newAmphipods)
    }

    fun getAmphipod(position: Position): Amphipod? {
        return if (wantsLetter == position.roomLetter) {
            amphipods[position.roomOrder!!]
        } else null
    }

    override fun toString(): String {
        return "$wantsLetter ($position): $amphipods"
    }

    fun safeToEnter(amphipod: Amphipod): Boolean {
        return amphipods.all { it == null || it.letter == amphipod.letter }
    }
}

data class Hall(
    val amphipods: List<Amphipod?>
) {
    fun getAmphipod(pos: Position): Amphipod? {
        return if (!pos.inRoom) amphipods[pos.hallPosition!! - 1] else null
    }

    fun addAmphipod(amphipod: Amphipod, position: Position): Hall {
        if (getAmphipod(position) != null) {
            throw IllegalStateException("Cannot put $amphipod into $position, it is occupied")
        }
        val newAmphipods = amphipods.mapIndexed { index, it ->
            if (index + 1 == position.hallPosition)
                amphipod.move(position)
            else
                it?.copy()
        }
        return Hall(newAmphipods)
    }

    fun removeAmphipod(amphipod: Amphipod): Hall {
        if (!amphipod.position.inHall) {
            throw IllegalStateException("Amphipod not in hall")
        }
        val newAmphipods = amphipods.mapIndexed { index, it ->
            if (index + 1 == amphipod.position.hallPosition) null else it?.copy()
        }
        return Hall(newAmphipods)
    }

    fun amphipodsInHall(): List<Amphipod> {
        return amphipods.filterNotNull()
    }

    val lastHallPos: Int
        get() = amphipods.size
}

data class PuzzleState(
    val rooms: List<Room>,
    val hall: Hall,
    val score: Long = 0L
) {
    private val costs: Map<Char, Int> = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
    val minumumPossibleCost: Solution

    init {
        minumumPossibleCost = calcMinimumPossibleScore()
    }

    fun solved(): Boolean {
        return rooms.all { it.solved() }
    }

    fun amphipods(): List<Amphipod> {
        return rooms.map { room -> room.amphipods.filterNotNull() }.flatten() + hall.amphipodsInHall()
    }

    override fun toString(): String {
        val buf = StringBuffer()
        buf.appendLine("#############")
        buf.append("#")
        for (h in hall.amphipods) {
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
        for (roomPosition in 1 until rooms[0].height) {
            buf.append("  ")
            for (r in rooms) {
                val letter = r.amphipods[roomPosition]?.letter ?: '.'
                buf.append("#${letter}")
            }
            buf.appendLine("#")
        }
        buf.appendLine("  #########  ")
        return buf.toString()
    }

    fun amphipod(pos: Position): Amphipod? = if (pos.inHall) {
        hall.getAmphipod(pos)
    } else {
        getRoom(pos)!!.getAmphipod(pos)
    }

    fun moveAmphipodToPosition(amphipod: Amphipod, newPosition: Position): PuzzleState? {
        val startPosition = amphipod.position
        if (startPosition.inRoom && newPosition.inHall && newPosition == getHallPosition(startPosition)) {
            return null
        }
        if (newPosition.inRoom) {
            if (!getRoom(newPosition)!!.safeToEnter(amphipod)) {
                return null
            }
        }
        val path = getPath(startPosition, newPosition, true)
        if (path.isEmpty()) {
            return null
        }

        var newState = removeAmphipod(amphipod)
        newState = newState.addAmphipod(amphipod, newPosition)
        return PuzzleState(newState.rooms, newState.hall, score + path.size * costs[amphipod.letter]!!).checkLegal()
    }

    private fun removeAmphipod(amphipod: Amphipod): PuzzleState {
        var newRooms = copyRooms(rooms)
        var newHall = hall.copy()
        if (amphipod.position.inRoom) {
            val room = getRoom(amphipod.position)!!
            val newRoom = room.removeAmphipod(amphipod)
            newRooms = newRooms.map { if (it == room) newRoom else it }
        } else {
            newHall = hall.removeAmphipod(amphipod)
        }
        return PuzzleState(newRooms, newHall, score)
    }

    private fun addAmphipod(amphipod: Amphipod, newPosition: Position): PuzzleState {
        var newRooms = copyRooms(rooms)
        var newHall = hall.copy()
        if (newPosition.inRoom) {
            val room = getRoom(newPosition)!!
            val newRoom = room.addAmphipod(amphipod, newPosition)
            newRooms = newRooms.map { if (it == room) newRoom else it }
        } else {
            newHall = hall.addAmphipod(amphipod, newPosition)
        }
        return PuzzleState(newRooms, newHall, score)

    }

    private fun copyRooms(rooms: List<Room>): List<Room> {
        return rooms.map { it.copy() }
    }

    private fun getPath(startPosition: Position, endPosition: Position, stopIfOccupied: Boolean = true): List<Position> {
        val path = ArrayList<Position>()

        var next: Position? = nextPos(startPosition, endPosition)
        while (next != null) {
            if (amphipod(next) != null && stopIfOccupied) {
                return emptyList() // no path there
            }
            path.add(next)
            next = nextPos(next, endPosition)
        }
        return path
    }

    fun calculateMinimumCost(startPosition: Position, endPosition: Position, amphipod: Amphipod): Int {
        val path = getPath(startPosition, endPosition, false)
        return path.size * costs[amphipod.letter]!!
    }

    private fun nextPos(pos: Position, endPosition: Position): Position? {
        val endHallPosition = getHallPosition(endPosition)
        val linedUp = getHallPosition(pos) == endHallPosition
        return when {
            pos == endPosition -> null
            pos.roomOrder == 0 && !linedUp -> {
                // go into hall
                pos.copy(roomOrder = null, roomLetter = null, hallPosition = getHallPosition(pos).hallPosition)
            }
            pos.inRoom && !linedUp -> {
                // go towards hall
                pos.copy(roomOrder = pos.roomOrder!! - 1)
            }
            pos.inHall && pos != endHallPosition -> {
                // move through hall to the end position in the hall
                val startHallPos = pos.hallPosition!!
                val endHallPos = endHallPosition.hallPosition!!
                val delta = if (endHallPos > startHallPos) 1 else -1
                pos.copy(hallPosition = startHallPos + delta)
            }
            endPosition.inRoom && pos.inHall -> {
                // move into room
                pos.copy(roomOrder = 0, roomLetter = getRoom(endPosition)!!.wantsLetter, hallPosition = null)
            }
            endPosition.inRoom && pos.inRoom -> {
                // move towards end position
                val room = getRoom(endPosition)!!
                if (pos.roomOrder!! + 1 < room.height) {
                    pos.copy(roomOrder = pos.roomOrder + 1, roomLetter = room.wantsLetter, hallPosition = null)
                } else {
                    // we have reached the terminus
                    null
                }
            }
            else -> null
        }
    }

    private fun getHallPosition(pos: Position): Position {
        return if (pos.inRoom) {
            getRoom(pos)!!.position
        } else {
            pos
        }
    }

    fun findAmphipodHomeRoom(amphipod: Amphipod): Room {
        return rooms.find { it.wantsLetter == amphipod.letter }!!
    }

    fun checkLegal(): PuzzleState? {
        if (isImpossibleToSolve()) {
            return null
        }
        return this
    }

    fun getRoom(position: Position): Room? {
        return rooms.find { it.wantsLetter == position.roomLetter }
    }

    private fun calcMinimumPossibleScore(): Solution {
        var minimumScore = score
        val notHome = amphipods().filterNot { isAmphipodAllSet(it) }.groupBy { it.letter }

        notHome.forEach { entry ->
            val list = entry.value
            val thisCost = when (list.size) {
                1 -> { sumOfCosts(list) }
                else -> {
                    val permutes = permute((0 until list.size).toList())
                    permutes.maxOfOrNull { listOrder ->
                        val orderedList = listOrder.map { list[it] }
                        sumOfCosts(orderedList)
                    }!!
                }
            }
            minimumScore += thisCost
        }
        return minimumScore
    }

    fun sumOfCosts(amphipods: List<Amphipod>): Int {
        val homeRoom = findAmphipodHomeRoom(amphipods[0])
        var sum = 0
        amphipods.forEachIndexed { index, amphipod ->
            sum += calculateMinimumCost(
                amphipod.position,
                Position(roomOrder = index, roomLetter = homeRoom.wantsLetter),
                amphipod
            )
        }
        return sum
    }

    fun permute(num: List<Int>): List<List<Int>> {
        var result = java.util.ArrayList<java.util.ArrayList<Int>>()

        //start from an empty list
        result.add(java.util.ArrayList())
        for (i in num.indices) {
            //list of list in current iteration of the array num
            val current = java.util.ArrayList<java.util.ArrayList<Int>>()
            for (l in result) {
                // # of locations to insert is largest index + 1
                for (j in 0 until l.size + 1) {
                    // + add num[i] to different locations
                    l.add(j, num[i])
                    val temp = java.util.ArrayList(l)
                    current.add(temp)
                    l.removeAt(j)
                }
            }
            result = java.util.ArrayList(current)
        }
        return result
    }

    fun isAmphipodAllSet(amphipod: Amphipod): Boolean {
        val height = rooms[0].height
        return when {
            amphipod.position.inHall -> false
            amphipod.position.roomLetter != amphipod.letter -> false
            else -> {
                for (roomOrder in amphipod.position.roomOrder!! until height) {
                    val otherAmphipodInRoom = amphipod(amphipod.position.copy(roomOrder = roomOrder))
                    if (otherAmphipodInRoom?.letter != amphipod.letter) {
                        return false
                    }
                }
                true
            }
        }
    }

    fun isImpossibleToSolve(): Boolean {
        val amphipodsInHall = hall.amphipodsInHall().toSet()
        for (amphipod in amphipods()) {
            val homeRoom = findAmphipodHomeRoom(amphipod)
            if (amphipod.position.inHall) {
                val wantsToMove = amphipod.wantsToMove()
                val others = amphipodsInHall - amphipod
                for (other in others) {
                    if (other.wantsToMove() != wantsToMove) {
                        val otherHomeRoom = findAmphipodHomeRoom(other)
                        if (!between(amphipod, homeRoom, other) && !between(amphipod, otherHomeRoom, other)) {
                            return true
                        }
                    }
                }
            } else {
//                val room = getRoom(amphipod.position)!!

                // if we are in the wrong room, say a D in the B and we encounter a B towards our room, we cannot
                // proceed, because they both want to pass each other
// this is not quite correct
//                if (homeRoom.wantsLetter != amphipod.position.roomLetter) {
//                    var next: Position? = getHallPosition(amphipod.position)
//                    while (next != null) {
//                        next = nextPos(next, homeRoom.position)
//                        if (next != null && amphipod(next)?.letter == room.wantsLetter) {
//                            return false
//                        }
//                    }
//                }
            }
        }
        return false
    }

    private fun between(amphipod: Amphipod, room: Room, other: Amphipod): Boolean {
        // can we get to our home room
        return room.position.hallPosition!! in
                min(amphipod.position.hallPosition!!, other.position.hallPosition!!) ..
                max(amphipod.position.hallPosition, other.position.hallPosition)
    }

}
