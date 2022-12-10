package com.advent2022.puzzle9

import com.advent2021.base.Base
import kotlin.math.abs

data class Point(val x: Int, val y: Int)
data class Item(val num: Int, var currentNode: Node, val isTail: Boolean = false) {
    val pos: Point
        get() = currentNode.pos
}
class Node(val pos: Point, var visited: Boolean = false) {
    var items = ArrayList<Item>()

    fun addItem(newItem: Item) {
        items.add(newItem)
        newItem.currentNode = this
        visited = visited || newItem.isTail
    }
    fun removeItem(item: Item) {
        items.remove(item)
    }

    override fun toString(): String {
        return "($pos) $visited"
    }
}

enum class Direction { U, L, R, D }
data class Instruction(val direction: Direction, val numTimes: Int)

class Graph(val tailNum: Int) {
    val start = Node(Point(0, 0))
    val nodes = HashMap<Point, Node>()
    val items = HashMap<Int, Item>()
    val head: Item
        get() = getItem(0)
    val tail: Item
        get() = getItem(tailNum)

    fun getItem(num: Int) = items[num]!!

    init {
        for (i in 0..tailNum) {
            val item = Item(i, start, i == tailNum)
            items[i] = item
            start.addItem(item)
        }
        nodes[start.pos] = start
    }

    fun addNodeIfNeeded(pos: Point): Node = nodes.computeIfAbsent(pos) { Node(pos) }

    fun moveUp() = moveHead(Point(head.pos.x, head.pos.y + 1))
    fun moveDown() = moveHead(Point(head.pos.x, head.pos.y - 1))
    fun moveLeft() = moveHead(Point(head.pos.x - 1, head.pos.y))
    fun moveRight() = moveHead(Point(head.pos.x + 1, head.pos.y))

    fun moveHead(newPos: Point) {
        moveItem(head, newPos)
    }

    fun moveItem(item: Item, newPos: Point) {
        val node = addNodeIfNeeded(newPos)
        item.currentNode.removeItem(item)
        node.addItem(item)

        if (item != tail) {
            val nextToMove = getItem(item.num + 1)
            moveItemIfMust(nextToMove, item)
        }
    }

    fun moveItemIfMust(nextToMove: Item, item: Item) {
        val nextPos = nextToMove.pos
        val itemPos = item.pos

        // we have to move the tail if it is more than 1 space away in any direction
        val diffY = itemPos.y - nextPos.y
        val diffX = itemPos.x - nextPos.x
        if (abs(diffY) > 1 || abs(diffX) > 1) {
            val newPos = if (diffY == 0) {
                Point(nextPos.x + (diffX / 2), nextPos.y)
            } else if (diffX == 0) {
                Point(nextPos.x, nextPos.y + (diffY / 2))
            } else {
                val deltaX = if (diffX > 0) 1 else -1
                val deltaY = if (diffY > 0) 1 else -1
                Point(nextPos.x + deltaX, nextPos.y + deltaY)
            }
            moveItem(nextToMove, newPos)
        }
    }
}
typealias Data = ArrayList<Instruction>
typealias Solution = Int
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle9()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle9 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val parts = line.split(" ")
        data.add(Instruction(Direction.valueOf(parts[0]), parts[1].toInt()))
    }

    override fun computeSolution(data: Data): Solution {
        return calc(data, 1)
    }

    override fun computeSolution2(data: Data): Solution2 {
        return calc(data, 9)
    }

    private fun calc(data: Data, tailNum: Int): Int {
        val graph = Graph(tailNum)
        data.forEach { instruction ->
            repeat(instruction.numTimes) {
                when (instruction.direction) {
                    Direction.U -> graph.moveUp()
                    Direction.L -> graph.moveLeft()
                    Direction.R -> graph.moveRight()
                    Direction.D -> graph.moveDown()
                }
            }
        }
        return graph.nodes.values.count { it.visited }
    }

}

