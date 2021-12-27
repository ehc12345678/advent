package com.advent2021.puzzle23

import java.lang.IllegalStateException

data class Amphipod(
    val letter: Char ,
    val position: Position
) {
    override fun toString(): String {
        return letter.toString()
    }

    fun move(newPosition: Position) = copy(position = newPosition)
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
    fun solved(): Boolean {
        return amphipods[0]?.letter == wantsLetter && amphipods[1]?.letter == wantsLetter
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
        buf.append("  ")
        for (r in rooms) {
            val letter = r.amphipods[1]?.letter ?: '.'
            buf.append("#${letter}")
        }
        buf.appendLine("#")
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
            pos.roomOrder == 1 && !linedUp -> {
                // go towards hall
                pos.copy(roomOrder = 0)
            }
            pos.roomOrder == 0 && !linedUp -> {
                // go into hall
                pos.copy(roomOrder = null, roomLetter = null, hallPosition = getHallPosition(pos).hallPosition)
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
            endPosition.inRoom && pos.roomOrder == 0 -> {
                // move to end position
                pos.copy(roomOrder = 1, roomLetter = getRoom(endPosition)!!.wantsLetter, hallPosition = null)
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

    fun checkLegal(): PuzzleState {
        val amphipods = amphipods()
        val counts = amphipods.groupingBy { it.letter }.eachCount().filter { it.value > 1 }
        if (!(counts.getOrDefault('A', 0) == 2 &&
                counts.getOrDefault('B', 0) == 2 &&
                counts.getOrDefault('C', 0) == 2 &&
                counts.getOrDefault('D', 0) == 2)) {
            println("Problem")
        }
        return this
    }

    fun getRoom(position: Position): Room? {
        return rooms.find { it.wantsLetter == position.roomLetter }
    }
}
