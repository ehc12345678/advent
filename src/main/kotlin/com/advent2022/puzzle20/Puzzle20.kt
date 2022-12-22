package com.advent2022.puzzle20

import com.advent2021.base.Base

class Node(val num: Int, var next: Node? = null, var prev: Node? = null) {
    val nextNode: Node
        get() = next ?: throw IllegalArgumentException("Something went wrong")

    val prevNode: Node
        get() = prev ?: throw IllegalArgumentException("Something went wrong")

    // this remove this node from the circular chain, but does not forget what it did point to
    fun removeThisNode() {
        nextNode.prev = prev
        prevNode.next = next
    }

    // -----
    // before:   prev -> this -> next
    // after:    prev -> this -> node -> next
    // before:   prev <- this <- next
    // after:    prev <- this <- node <- next
    fun addNodeAfter(node: Node) {
        if (node != this) {
            node.next = next
            next?.prev = node
            next = node
            node.prev = this
        } else {
            throw IllegalArgumentException("Shouldn't happen")
        }
    }

    fun nodeFromOffset(offset: Int): Node {
        return when {
            offset == 0 -> this
            offset > 0 -> {
                var ret = this
                repeat (offset) {
                    ret = ret.nextNode
                }
                ret
            }
            else -> {
                var ret = this
                repeat (-offset) {
                    ret = ret.prevNode
                }
                ret.prevNode
            }
        }
    }
}

class Data {
    val items = ArrayList<Int>()
    val nodes = HashMap<Int, Node>()
    var first: Node? = null
    var prev: Node? = null

    fun node(num: Int) = nodes[num]!!
}
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle20()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle20 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val node = Node(line.toInt())
        data.items.add(node.num)

        data.first = data.first ?: node // assign first if we have not seen it
        data.prev?.addNodeAfter(node)
        data.prev = node

        data.nodes[node.num] = node
    }

    override fun readInput(filename: String, data: Data, parseLineFunc: (String, Data) -> Unit): Data {
        val ret = super.readInput(filename, data, parseLineFunc)
        data.first!!.prev = ret.prev // have to close the loop
        data.prev!!.next = data.first
        return ret
    }

    override fun computeSolution(data: Data): Solution {
        printNodeList(data.first!!)
        data.items.forEach { num ->
            println("Move $num")
            if (num != 0) {
                moveNodeByNumSpaces(data.node(num), num)
            }
        }

        val zeroeth = data.node(0)
        return listOf(1000, 2000, 3000).sumOf { zeroeth.nodeFromOffset(it).num }
    }

    fun moveNodeByNumSpaces(node: Node, num: Int) {
        node.removeThisNode()

        val insertAfter = node.nodeFromOffset(num)
        insertAfter.addNodeAfter(node)
        // printNodeList(node.prevNode)
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }

    fun printNodeList(node: Node) {
        var r = node
        do {
            print("${r.prevNode.num}<-(${r.num})->${r.nextNode.num}, ")
            r = r.nextNode
        } while (r != node)
        println()
    }
}

