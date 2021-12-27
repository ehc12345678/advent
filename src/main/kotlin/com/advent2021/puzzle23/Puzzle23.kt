package com.advent2021.puzzle23

import com.advent2021.base.Base
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.min

data class Data(val rooms: ArrayList<Room> = ArrayList(),
                val hall: ArrayList<Amphipod?> = ArrayList(),
                var roomToAmphipod: HashMap<Room, ArrayList<Amphipod>> = HashMap()
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
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputsTest.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle23 : Base<Data, Solution?, Solution2?>() {
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
                    val room = Room(Position(hallPosition = hallPosition), 'A' + index)
                    data.rooms.add(room)
                    data.roomToAmphipod.putIfAbsent(room, ArrayList<Amphipod>().also {
                        it.add(Amphipod(s[0], Position(roomOrder = 0, roomLetter = room.wantsLetter)))
                    })
                    hallPosition += 2
                }
            }
            line.startsWith("  #") -> {
                val chars = line.substring(3, line.lastIndexOf('#')).split("#")
                chars.forEachIndexed { index, s ->
                    val room = data.rooms[index]
                    data.roomToAmphipod[room]!!.add(Amphipod(s[0], Position(roomOrder = 1, roomLetter = room.wantsLetter)))
                }
            }
        }
    }

    override fun computeSolution(data: Data): Solution {
        val queue = PriorityQueue<PuzzleState>(1000) { state1, state2 ->
            calcMinimumPossibleScore(state1).compareTo(calcMinimumPossibleScore(state2))
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
        return queue.peek().score
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }

    fun calcLegalMoves(puzzleState: PuzzleState): List<PuzzleState> {
        // if we can move an amphipod to its ultimate destinate, do that.  It is the best move
        val amphipods = puzzleState.amphipods()
        amphipods.forEach { amphipod ->
            val homePuzzleState = moveAmphipodToAllSet(puzzleState, amphipod)
            if (homePuzzleState != null) {
                return listOf(homePuzzleState)
            }
        }

        // move as many amphipods out to the hall as possible
        val ret = ArrayList<PuzzleState>()
        amphipods.forEach { amphipod ->
            if (!isAmphipodAllSet(puzzleState, amphipod)) {
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
        val lastPlaceInRoom = Position(1, homeRoom.wantsLetter)
        val state = puzzleState.moveAmphipodToPosition(amphipod, lastPlaceInRoom)
        if (state != null) {
            return state
        }
        if (puzzleState.amphipod(lastPlaceInRoom)?.letter == amphipod.letter) {
            return puzzleState.moveAmphipodToPosition(amphipod, Position(0, homeRoom.wantsLetter))
        }
        return null
    }

    private fun isAmphipodAllSet(puzzleState: PuzzleState, amphipod: Amphipod): Boolean {
        return when {
            amphipod.position.inHall -> false
            amphipod.position.roomLetter != amphipod.letter -> false
            amphipod.position.roomOrder == 1 -> true
            else -> {
                val otherAmphipodInRoom = puzzleState.amphipod(amphipod.position.copy(roomOrder = 1))
                otherAmphipodInRoom?.letter == amphipod.letter
            }
        }
    }

    private fun calcMinimumPossibleScore(puzzleState: PuzzleState): Solution {
        var minimumScore = puzzleState.score
        val notHome = puzzleState.amphipods().filterNot { isAmphipodAllSet(puzzleState, it) }.groupBy { it.letter }

        notHome.forEach { entry ->
            val list = entry.value
            if (list.size == 2) {
                val amphipod1 = list[0]
                val amphipod2 = list[1]
                val homeRoom = puzzleState.findAmphipodHomeRoom(amphipod1)
                val cost1 =
                    puzzleState.calculateMinimumCost(
                        amphipod1.position,
                        Position(roomOrder = 0, roomLetter = homeRoom.wantsLetter),
                        amphipod1) +
                    puzzleState.calculateMinimumCost(
                        amphipod2.position,
                        Position(roomOrder = 1, roomLetter = homeRoom.wantsLetter),
                        amphipod2)

                // the cost could depend on starting position
                val cost2 =
                    puzzleState.calculateMinimumCost(
                        amphipod1.position,
                        Position(roomOrder = 1, roomLetter = homeRoom.wantsLetter),
                        amphipod1) +
                    puzzleState.calculateMinimumCost(
                        amphipod2.position,
                        Position(roomOrder = 0, roomLetter = homeRoom.wantsLetter),
                        amphipod2)
                minimumScore += min(cost1, cost2)
            } else {
                val amphipod = list[0]
                val homeRoom = puzzleState.findAmphipodHomeRoom(amphipod)
                minimumScore += puzzleState.calculateMinimumCost(
                    amphipod.position,
                    Position(roomOrder = 0, roomLetter = homeRoom.wantsLetter),
                    amphipod)
            }
        }
        return minimumScore
    }

}

