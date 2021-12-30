package com.advent2021.puzzle23

import com.advent2021.base.Base
import java.util.*
import kotlin.collections.HashMap
import java.util.ArrayList

data class Data(
    val solution1: Boolean = true,
    val rooms: ArrayList<Room> = ArrayList(),
    val hall: ArrayList<Amphipod?> = ArrayList(),
    var roomToAmphipod: HashMap<Room, ArrayList<Amphipod?>> = HashMap()
) {
    fun toPuzzleState(): PuzzleState {
        return PuzzleState(
            rooms.map { it.copy(amphipods = roomToAmphipod[it]!!) },
            Hall(amphipods = hall)
        )
    }
}
typealias Solution = Long
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle23()
//        val solution1 = puz.solvePuzzle("inputs.txt", Data())
//        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data(solution1 = false))
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle23 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        fun addLine(l: String, d: Data) {
            val chars = l.substring(3, l.lastIndexOf('#')).split("#").filter { it.isNotBlank() }
            chars.forEachIndexed { index, s ->
                val room = d.rooms[index]
                val amphList = d.roomToAmphipod[room]!!
                val roomOrder = amphList.size
                val amphipod = if (s[0] == '.') null else Amphipod(s[0], Position(roomOrder = roomOrder, roomLetter = room.wantsLetter))
                amphList.add(amphipod)
            }
        }

        when {
            line == "#############" -> {
                var hallPosition = 3
                for (index in 0..3) {
                    val room = Room(Position(hallPosition = hallPosition), 'A' + index)
                    data.rooms.add(room)
                    data.roomToAmphipod[room] = ArrayList()
                    hallPosition += 2
                }
            }
            line == "  #########" -> { }
            line.startsWith("#.") -> {
                var hallPos = 1
                for (ch in line.substring(1, line.length - 1)) {
                    val amphipod = if (ch == '.') null else Amphipod(ch, Position(hallPosition = hallPos))
                    data.hall.add(amphipod)
                    ++hallPos
                }
            }
            line.startsWith("###") -> {
                addLine(line, data)
            }
            line.startsWith("  #") -> {
                if (!data.solution1) {
                    addLine("  #D#C#B#A#", data)
                    addLine("  #D#B#A#C#", data)
                }
                addLine(line, data)
            }
        }
    }

    override fun computeSolution(data: Data): Solution {
        val queue = PriorityQueue<PuzzleState>(100000) { state1, state2 ->
            state1.minumumPossibleCost.compareTo(state2.minumumPossibleCost)
        }
        val firstState = data.toPuzzleState()
        queue.add(firstState)

        var steps = 0
        while (queue.isNotEmpty() && !queue.peek().solved()) {
            val top = queue.remove()
            val newLegalMoves = calcLegalMoves(top)
            queue.addAll(newLegalMoves)
            ++steps
            if ((steps % 100000) == 0) {
                println("Step $steps has best score of ${top.score} with miminum possible ${top.minumumPossibleCost}")
            }
        }
        return queue.peek().score
    }

    override fun computeSolution2(data: Data): Solution2 {
        return computeSolution(data)
    }

    fun calcLegalMoves(puzzleState: PuzzleState): List<PuzzleState> {
        val amphipods = puzzleState.amphipods().filter { !puzzleState.isAmphipodAllSet(amphipod = it) }

        // if we can move an amphipod to its ultimate destinate, do that.  It is the best move
        amphipods.forEach { amphipod ->
            val homePuzzleState = moveAmphipodToAllSet(puzzleState, amphipod)
            if (homePuzzleState != null) {
                return listOf(homePuzzleState)
            }
        }

        // move as many amphipods out to the hall as possible
        val ret = ArrayList<PuzzleState>()
        amphipods.forEach { amphipod ->
            if (!puzzleState.isAmphipodAllSet(amphipod)) {
                val pos = amphipod.position
                if (pos.inRoom) {
                    for (hallPosition in 1..puzzleState.hall.lastHallPos) {
                        val newState =
                            puzzleState.moveAmphipodToPosition(amphipod, Position(hallPosition = hallPosition))
                        if (newState != null) {
                            ret.add(newState)
                        }
                    }
                }
            }
        }
        return ret
    }

    // clear path to final destination
    private fun moveAmphipodToAllSet(puzzleState: PuzzleState, amphipod: Amphipod): PuzzleState? {
        val homeRoom = puzzleState.findAmphipodHomeRoom(amphipod)
        return puzzleState.moveAmphipodToPosition(
            amphipod,
            Position(roomLetter = homeRoom.wantsLetter, roomOrder = homeRoom.effectiveHeight - 1)
        )
    }
}

