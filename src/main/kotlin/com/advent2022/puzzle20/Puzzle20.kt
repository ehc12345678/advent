package com.advent2022.puzzle20

import com.advent2021.base.Base
import kotlin.math.abs

typealias Data = ArrayList<Int>
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

fun Data.wrapped(index: Int): Int {
    return this[wrappedIndex(index)]
}

private fun Data.wrappedIndex(index: Int): Int {
    var fixedIndex = (index % size)
    if (fixedIndex < 0) {
        fixedIndex += size
    }
    return fixedIndex
}

fun Data.swap(firstIndex: Int, secondIndex: Int) {
    val tmp = wrapped(firstIndex)
    this[wrappedIndex(firstIndex)] = wrapped(secondIndex)
    this[wrappedIndex(secondIndex)] = tmp
}

class Puzzle20 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line.toInt())
    }

    override fun computeSolution(data: Data): Solution {
        val ret = Data(data)
        val swapMethod = Data(data)
        data.forEach { num ->
            println(ret)
            println("$num moves")
            moveViaIndexMethod(num, ret)
            moveViaSwapMethod(num, swapMethod)
            if (ret != swapMethod) {
                println("ret: $ret")
                println("swap: $swapMethod")
            }
        }

        val zeroIndex = swapMethod.indexOf(0)
        return listOf(1000, 2000, 3000).sumOf {
            val wrappedIndex = swapMethod.wrappedIndex(it + zeroIndex)
            val addend = swapMethod[wrappedIndex]
            println(addend)
            addend
        }
    }

    private fun moveViaSwapMethod(num: Int, ret: Data) {
        val index = ret.indexOf(num)
        val inc = if (num > 0) { 1 } else if (num < 0) { -1 } else return
        var pos = index
        var times = abs(num)
        repeat(times) {
            var newPos = pos + inc
            if (newPos in ret.indices) {
                ret.swap(pos, newPos)
            } else if (newPos < 0) {
                // remove it from the beginning, add it to the end
                ret.removeAt(pos)
                ret.add(ret.size - 1, num)
                newPos = ret.size - 2
            } else {
                ret.removeAt(pos)
                ret.add(1, num)
                newPos = 1
            }
            pos = newPos
        }
    }

    private fun moveViaIndexMethod(num: Int, ret: Data) {
        val index = ret.indexOf(num)
        val newUnwrappedIndex = index + num

        var destIndex = when {
            newUnwrappedIndex >= ret.size -> {
                var wrappedIndex = newUnwrappedIndex - ret.size
                if (wrappedIndex > index) {
                    // we passed the spot where the number was removed
                    ++wrappedIndex
                }
                ret.wrappedIndex(wrappedIndex)
            }

            newUnwrappedIndex < 0 -> {
                var wrappedIndex = newUnwrappedIndex + ret.size - 1
                if (wrappedIndex < index) {
                    // we passed the spot where the number was remove
                    --wrappedIndex
                }
                ret.wrappedIndex(wrappedIndex)
            }

            else -> newUnwrappedIndex
        }

        // certainly don't need to remove it and readd it if it's already there
        if (index != destIndex) {
            ret.removeAt(index)
            if (index > destIndex) {
                destIndex++
            }
            ret.add(destIndex, num)
        }
        //            println(ret)
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

