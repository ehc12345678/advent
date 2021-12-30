package com.advent2021.puzzle24

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Puzzle24Test {
    private val puz = Puzzle24()

    @Test
    fun testSimpleAdd() {
        val data = setup(
            listOf(
                "inp w",
                "add w 0"
            )
        )
        val first = data.symbolTable.inputs[0]
        assertThat(first.maxCouldBe(), equalTo(9))
    }

    @Test
    fun testEqualZero() {
        val data = setup(
            listOf(
                "inp z",
                "add z 1",
                "eql z 0"
            )
        )
        val first = data.symbolTable.inputs[0]
        assertThat(first.maxCouldBe(), equalTo(0))

        val data2 = setup(
            listOf(
                "inp z",
                "add z 1",
                "eql z 1"
            )
        )
    }

    private fun setup(lines: List<String>): Data {
        val data = Data()
        lines.forEach { line -> puz.parseLine(line, data) }

        data.instructions.forEach { instruction ->
            instruction.execute(data.symbolTable)
        }
        return data
    }
}