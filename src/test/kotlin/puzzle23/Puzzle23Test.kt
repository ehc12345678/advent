package puzzle23

import com.advent2021.puzzle23.Data
import com.advent2021.puzzle23.Puzzle23
import com.advent2021.puzzle23.PuzzleState
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
}