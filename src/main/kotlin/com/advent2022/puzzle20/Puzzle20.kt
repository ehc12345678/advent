package com.advent2022.puzzle20

import com.advent2021.base.Base

class Node(var num: Long, var next: Node? = null, var prev: Node? = null) {
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

    fun nodeFromOffset(offset: Long, modulus: Int): Node {
        val mod = if (offset > 0) (offset % modulus).toInt() else (-offset % modulus).toInt()
        return when {
            mod == 0 -> this
            offset > 0 -> {
                var ret = this
                repeat (mod) {
                    ret = ret.nextNode
                }
                ret
            }
            else -> {
                var ret = this
                repeat (mod) {
                    ret = ret.prevNode
                }
                ret.prevNode
            }
        }
    }
}

class Data {
    fun find(num: Long): Node {
        // start from first and try to find num
        var go = first!!
        while (go.num != num && go.next != first) {
            go = go.nextNode
        }
        return go
    }

    val items = ArrayList<Node>()
    var first: Node? = null
    var prev: Node? = null
}
typealias Solution = Long
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
        val node = Node(line.toLong())
        data.items.add(node)

        data.first = data.first ?: node // assign first if we have not seen it
        data.prev?.addNodeAfter(node)
        data.prev = node
    }

    override fun readInput(filename: String, data: Data, parseLineFunc: (String, Data) -> Unit): Data {
        val ret = super.readInput(filename, data, parseLineFunc)
        data.first!!.prev = ret.prev // have to close the loop
        data.prev!!.next = data.first
        return ret
    }

    override fun computeSolution(data: Data): Solution {
        data.items.forEach { node ->
            val num = node.num
            if (num != 0L) {
                moveNodeByNumSpaces(node, num, data.items.size)
            }
        }

        val zeroeth = data.find(0)
        return listOf(1000L, 2000L, 3000L).sumOf {
            val num = zeroeth.nodeFromOffset(it, data.items.size).num
            println(num)
            num
        }
    }

    override fun computeSolution2(data: Data): Solution2 {
        data.items.forEach { node -> node.num *= 811589153 }

        repeat(10) {
            data.items.forEach { node ->
                node.num = node.num
                if (node.num != 0L) {
                    moveNodeByNumSpaces(node, node.num, data.items.size)
                }
            }
        }

        val zeroeth = data.find(0)
        return listOf(1000L, 2000L, 3000L).sumOf {
            val num = zeroeth.nodeFromOffset(it, data.items.size).num
            println(num)
            num
        }
    }

    fun moveNodeByNumSpaces(node: Node, num: Long, size: Int) {
        node.removeThisNode()

        val insertAfter = node.nodeFromOffset(num, size - 1)
        insertAfter.addNodeAfter(node)
        // printNodeList(node.prevNode)
    }

    fun printNodeList(node: Node) {
        var r = node
        do {
//            print("${r.prevNode.num}<-(${r.num})->${r.nextNode.num}, ")
            print("${r.num}, ")
            r = r.nextNode
        } while (r != node)
        println()
    }

    fun sanityCheckNode(node: Node, data: Data) {
        val allNums = ArrayList<Long>()
        var go = node
        do {
            allNums.add(go.num)
            if (go.prevNode.nextNode != go) {
                throw IllegalArgumentException("Something bad linked in prev node")
            }
            if (go.nextNode.prevNode != go) {
                throw IllegalArgumentException("Something bad linked in next node")
            }
            go = go.nextNode
        } while (go != node)

        val dataItems = data.items.map { it.num }.toSet()
        val allItemSet = allNums.toSet()
        if (allItemSet != dataItems) {
            println(allItemSet - dataItems)
            println(dataItems - allItemSet)
            printNodeList(node)
            throw IllegalArgumentException("Did not see all the items in the data")
        }
    }
}

