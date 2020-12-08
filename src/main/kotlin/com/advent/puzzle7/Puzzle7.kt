package com.advent.puzzle7

import java.io.File

fun main() {
    val puzzle = Puzzle7()
    try {
        val graph = puzzle.readInputs("inputs.txt")
        val count = puzzle.canContain(graph.root, "shiny gold", HashSet())
        println("Count is $count")

        println("Test = ${puzzle.countBags(puzzle.readInputs("test.txt").root, "shiny gold")}")

        val countBags = puzzle.countBags(graph.root, "shiny gold")
        println("Count is $countBags")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Node(
    val color: String,
    val number: Int
) {
    val children: MutableMap<String, Node> = HashMap()

    fun add(node: Node) {
        children[node.color] = node
    }
}

class GraphBags {
    val root = Node("", 0)
}

class Puzzle7 {
    fun readInputs(filename: String): GraphBags {
        val file = File(filename)
        val contains = GraphBags()
        file.readLines().forEach { contains.root.add(parseLine(it)) }
        return contains
    }

    fun parseLine(str: String) : Node {
        val first = str.split(" bags contain ")
        val node = Node(first[0], 1)

        val children = first[1].split(",", ".")
        children.forEach {
            if (it.isNotEmpty()) {
                val match = """(\d+) (.*) bag[s]?""".toRegex().find(it)
                if (match != null) {
                    val num = match.groups[1]?.value?.toInt() ?: 1
                    val color = match.groups[2]?.value ?: ""
                    node.add(Node(color, num))
                } else {
                    println("Found leaf " + node.color)
                }
            }
        }
        return node
    }

    fun canContain(node: Node, color: String, seen: MutableSet<String>) : Int {
        val containing = getChildrenContaining(node, color)
        containing.forEach {
            if (!seen.contains(it.color)) {
                seen.add(it.color)
                canContain(node, it.color, seen)
            }
        }
        return seen.size
    }

    fun getChildrenContaining(node: Node, color: String) : List<Node> {
        return node.children.values.filter { it.children.containsKey(color) }
    }

    fun countBags(root: Node, color: String) : Int {
        val child = root.children[color]
        var count = 1
        child?.children?.forEach { key, value ->
            count += value.number * countBags(root, key)
        }
        return count
    }

}