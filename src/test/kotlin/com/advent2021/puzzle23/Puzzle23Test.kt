package com.advent2021.puzzle23

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Puzzle23Test {
    private val puz = Puzzle23()

    private fun createPuzzleState(input: String): PuzzleState {
        val data = Data()
        input.trimIndent().split("\n").forEach {
            puz.parseLine(it, data)
        }
        return data.toPuzzleState()
    }

    @Test
    public fun testIllegal() {
        val puz = createPuzzleState("""
#############
#.B.D...AB.B#
###.#.#.#D###
  #.#.#C#A#
  #D#B#C#C#
  #A#D#C#A#
  #########              
        """.trimIndent())
        assertThat(puz.checkLegal(), equalTo(null))
    }

    @Test
    public fun testMinimumCost() {
        val puz: PuzzleState = createPuzzleState("""
#############
#...........#
###C#A#D#D###
  #B#B#B#A#
  #D#C#C#B#
  #A#D#A#C#
  #########              
        """.trimIndent())
        val startPos = Position(2, 'A')
        val dInARoom = puz.amphipod(startPos)!!
        assertThat(puz.calculateMinimumCost(startPos, Position(3, 'D'), dInARoom), equalTo(13000))
        assertThat(puz.calculateMinimumCost(startPos, Position(2, 'D'), dInARoom), equalTo(12000))

        val bPosInARoom = Position(1, 'A')
        val bInARoom = puz.amphipod(bPosInARoom)!!
        assertThat(puz.calculateMinimumCost(bPosInARoom, Position(3, 'B'), bInARoom), equalTo(80))

        val bPosInBRoom = Position(1, 'B')
        val bInBRoom = puz.amphipod(bPosInBRoom)!!
        assertThat(puz.calculateMinimumCost(bPosInBRoom, Position(3, 'B'), bInBRoom),
            equalTo(80))

        val aPosInDRoom = Position(1, 'D')
        val aInDRoom = puz.amphipod(aPosInDRoom)!!
        assertThat(puz.calculateMinimumCost(aPosInDRoom, Position(2, 'A'), aInDRoom),
            equalTo(11))
    }

}