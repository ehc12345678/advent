package com.advent.puzzle23

fun main() {
    val puzzle = Puzzle23()
    try {
        val input = "389125467"
//        val answerA = puzzle.solutionA(puzzle.toIntList(input), 100)
//        println("Answer A is $answerA")

        val answerB = puzzle.solutionB(puzzle.toIntList(input), 1000000)
        println("Answer B is $answerB")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle23 {
    fun toIntList(str: String) : ArrayList<Int> {
        return ArrayList(str.map { ch -> ch.toString().toInt() })
    }

    fun solutionA(circle: ArrayList<Int>, numTimes: Int) : String {
        val length = circle.size
        var currentCupIndex = 0
        for (i in 0 until numTimes) {
            val currentCup = circle[currentCupIndex]
            val otherCups = subtractThree(circle, currentCupIndex)
            var destinationCup = if (currentCup - 1 == 0) length else currentCup - 1

            // find the destination cup
            while (otherCups.contains(destinationCup)) {
                destinationCup = if (destinationCup - 1 <= 0) length else destinationCup - 1
            }

            println("-- move ${i + 1} --")
            println("current: $currentCup")
            println("cups: ${circle.joinToString(" ") { if (it == currentCup) "($it)" else it.toString() }}")
            println("pick up: ${otherCups.joinToString(" ")}")
            println("destination: $destinationCup\n")

            circle.removeAll(otherCups)
            val findDestination = circle.indexOf(destinationCup)
            circle.addAll(findDestination + 1, otherCups)

            val findCurrent = circle.indexOf(currentCup)
            currentCupIndex = (findCurrent + 1) % length
        }

        var ret = ""
        val oneIndex = circle.indexOf(1)
        for (i in 0 until length - 1) {
            ret += circle[(oneIndex + i + 1) % length].toString()
        }
        return ret
    }

    private fun subtractThree(circle: ArrayList<Int>, currentCupIndex: Int): ArrayList<Int> {
        val ret = ArrayList<Int>()
        for (i in 0 until 3) {
            ret.add(circle[(currentCupIndex + i + 1) % circle.size])
        }
        return ret
    }

    private fun getNextThreeNodeValueSet(node: Node) : Set<Int> {
        val ret = HashSet<Int>()
        var next = node
        for (i in 0 until 3) {
            next = next.next!!
            ret.add(next.number)
        }
        return ret
    }

    private fun moveThreeNodes(beforeThree: Node, destinationNode: Node) {
        val firstThree = beforeThree.next!!
        val lastThree = firstThree.next?.next!!

        // moves the three nodes to after the destination node
        val saveDestinationNodeNext = destinationNode.next!!
        val saveLastThreeNext = lastThree.next!!
        destinationNode.next = firstThree
        beforeThree.next = lastThree.next!!
        lastThree.next = saveDestinationNodeNext

        // fixes the prev pointers
        firstThree.prev = destinationNode
        saveDestinationNodeNext.prev = lastThree
        saveLastThreeNext.prev = beforeThree
    }

    fun solutionB(input: ArrayList<Int>, upperLimit: Int) : Long {
        val numTimes = upperLimit * 10
        val circle = Circle(input, upperLimit)
        val length = circle.size
        var node = circle.head!!

        for (i in 0 until numTimes) {
            val currentCup = node.number
            val otherCups = getNextThreeNodeValueSet(node)
            var destinationCup = if (currentCup - 1 == 0) length else currentCup - 1

            // find the destination cup
            while (otherCups.contains(destinationCup)) {
                destinationCup = if (destinationCup - 1 <= 0) length else destinationCup - 1
            }

            val findDestination = circle.getNode(destinationCup)
            moveThreeNodes(node, findDestination)

            node = node.next!!
        }

        val oneNode = circle.getNode(1)
        val prevNode = oneNode.prev!!
        val nextNode = oneNode.next!!
        return prevNode.number.toLong() * nextNode.number.toLong()
    }

}

class Node(var number: Int, var next: Node?, var prev: Node?)
class Circle(input: ArrayList<Int>, highWater: Int) {
    var head: Node? = null
    var current: Node? = null
    val nodes = HashMap<Int, Node>()

    init {
        head = add(input[0])
        for (i in 1 until input.size) {
            add(input[i])
        }
        for (i in input.size until highWater) {
            add(i)
        }
        current?.next = head
    }

    fun add(number: Int) : Node {
        val node = Node(number, null, current)
        current?.next = node
        current = node
        nodes[number] = node
        return node
    }

    fun getNode(number: Int) : Node {
        return nodes[number]!!
    }

    val size : Int
        get() = nodes.size

    override fun toString(): String {
        val buf = StringBuffer()
        var node = head
        buf.append("next: ")
        for (i in 0 until 20) {
            buf.append("${node!!.number}->")
            node = node.next
        }
        buf.append("\n")
        buf.append("prev: ")
        for (i in 0 until 20) {
            node = node!!.prev
            buf.append("${node!!.number}->")
        }
        return buf.toString()
    }
}

