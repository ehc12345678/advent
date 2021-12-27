package puzzle23

import com.advent2021.puzzle23.Data
import com.advent2021.puzzle23.Puzzle23
import org.junit.jupiter.api.Test

class Puzzle23Test {
    private val puz = Puzzle23()

    @Test
    fun doSetCase() {
        val data = Data()
        val input = """
#############
#...........#
###B#C#A#B###
  #C#D#D#A#
  #########""".trim()
        input.split("\n").forEach {
            puz.parseLine(it, data)
        }

        var state = data.toPuzzleState()
        println(state.score)
        println(state)

//        val firstA = state.getRoom('C')!!.amphipods[0]!!
//        var score = state.score
//
//        var cost = puz.cost(puz.stepsToOtherPos(state, firstA, 10), firstA)
//        score += cost
//
//        state = state.placeAmphipodInHall(firstA, cost)
//        println(state.score)
//        println(state)
//
//        val firstC = state.getRoom('B')!!.amphipods[0]!!
//        cost = puz.cost(puz.stepsToOtherPos(state, firstC, 2), firstC)
//        score += cost
//
//        state = state.placeAmphipodInHall(firstC, cost)
//        println(state.score)
//        println(state)
//
//        val homeC = state.getRoom('C')!!
//        val amphD = state.getRoom('B')!!.amphipods[1]!!
//        cost = puz.cost(puz.stepsToOtherPos(state, amphD, homeC.hallPosition) + 1, amphD)
//        state = state.placeAmphipodInRoom(amphD, homeC, 0, cost)
//        score += cost
//
//        println(state.score)
//        println(state)
    }
}