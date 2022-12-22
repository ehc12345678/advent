package com.advent2022.puzzle20

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Puzzle20Test {
    val data = Data()
    val puz = Puzzle20()
    lateinit var one: Node
    lateinit var two: Node
    lateinit var three: Node
    lateinit var seven: Node
    lateinit var four: Node

    @BeforeEach
    fun setup() {
        puz.parseLine("1", data)
        puz.parseLine("2", data)
        puz.parseLine("3", data)
        puz.parseLine("7", data)
        puz.parseLine("4", data)
        one = data.nodes[1]!!
        two = data.nodes[2]!!
        three = data.nodes[3]!!
        seven = data.nodes[7]!!
        four = data.nodes[4]!!
        four.next = one
        one.prev = four
    }

    @Test
    fun testStart() {
        assertThat(one.next, equalTo(two))
        assertThat(two.next, equalTo(three))
        assertThat(three.next, equalTo(seven))
        assertThat(seven.next, equalTo(four))
        assertThat(four.next, equalTo(one))
        assertThat(one.prev, equalTo(four))
        assertThat(two.prev, equalTo(one))
        assertThat(three.prev, equalTo(two))
        assertThat(seven.prev, equalTo(three))
        assertThat(four.prev, equalTo(seven))
    }

    @Test
    fun testAddNode() {
        puz.moveNodeByNumSpaces(one, 1, data.items.size)
        assertThat(one.next, equalTo(three))
        assertThat(three.prev, equalTo(one))
        assertThat(one.prev, equalTo(two))
        assertThat(two.next, equalTo(one))

        sanityCheckNodes(one, two, three, four)
    }

    @Test
    fun testAddNodeTwo() {
        puz.moveNodeByNumSpaces(two, 2, data.items.size)
        assertThat(one.next, equalTo(three))
        assertThat(two.next, equalTo(four))
        assertThat(two.prev, equalTo(seven))

        sanityCheckNodes(one, two, three, four)
    }

    @Test
    fun testAddNodeSeven() {
        puz.moveNodeByNumSpaces(two, 2, data.items.size)
        // 1,2,3,7,4 -> 1,3,7,2,4
        assertThat(one.next, equalTo(three))
        assertThat(two.next, equalTo(one))
        assertThat(two.prev, equalTo(four))

        sanityCheckNodes(one, two, three, four)
    }

    @Test
    fun testAddNodeThree() {
        puz.moveNodeByNumSpaces(three, 3, data.items.size)
        // 1,2,3,7,4 -> 3,1,2,7,4
        assertThat(three.nextNode.num, equalTo(two.num))
        assertThat(three.prevNode.num, equalTo(one.num))

        sanityCheckNodes(one, two, three, four)
    }

    fun sanityCheckNodes(vararg nodes: Node) {
        nodes.forEach { node ->
            assertThat(node.nextNode.prevNode, equalTo(node))
            assertThat(node.prevNode.nextNode, equalTo(node))
        }
    }
}