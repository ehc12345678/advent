package com.advent2021.puzzle1

import com.advent2021.base.Base

/**
Part 1:

count the number of times a depth measurement increases from the previous measurement. (There is no measurement before the first measurement.) In the example above, the changes are as follows:

<pre>{@code
199 (N/A - no previous measurement)
200 (increased)
208 (increased)
210 (increased)
200 (decreased)
207 (increased)
240 (increased)
269 (increased)
260 (decreased)
263 (increased)
}</pre>
In this example, there are 7 measurements that are larger than the previous measurement.

How many measurements are larger than the previous measurement?

Part 2

Instead, consider sums of a three-measurement sliding window. Again considering the above example:

<code>
199  A
200  A B
208  A B C
210    B C D
200  E   C D
207  E F   D
240  E F G
269    F G H
260      G H
263        H
</code>
Start by comparing the first and second three-measurement windows. The measurements in the first window are marked A (199, 200, 208); their sum is 199 + 200 + 208 = 607. The second window is marked B (200, 208, 210); its sum is 618. The sum of measurements in the second window is larger than the sum of the first, so this first comparison increased.

Your goal now is to count the number of times the sum of measurements in this sliding window increases from the previous sum. So, compare A with B, then compare B with C, then C with D, and so on. Stop when there aren't enough measurements left to create a new three-measurement sum.
 */

typealias Data = ArrayList<Int>
data class Solution(
   var count: Int
)
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle1()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle1 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        data.add(line.toInt())
    }

    override fun computeSolution(data: Data): Solution = computeWindow(data, 1)
    override fun computeSolution2(data: Data): Solution2 = computeWindow(data, 3)

    private fun computeWindow(data: Data, windowSize: Int) : Solution {
        var last = sumWindow(data, 0, windowSize)
        val solution = Solution(0)
        (1..data.size - windowSize).forEach { index ->
            val window = sumWindow(data, index, windowSize)
            if (window > last) {
                solution.count++
            }
            last = window
        }
        return solution
    }

    private fun sumWindow(data: Data, index: Int, windowSize: Int) : Int {
        return data.subList(index, index + windowSize).sum()
    }
}

