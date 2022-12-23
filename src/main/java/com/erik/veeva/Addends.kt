package com.erik.veeva

fun main() {
    val test = listOf(1,2,3,4,5,6,7,8,12,15)
    val answer = Addends().findAddendsForSum(test, 16)

    println(answer)
    println(answer.map { Pair(test[it.first], test[it.second]) })
    println(Addends().pow(3.0,2))
    println(Addends().pow(3.0,3))
    println(Addends().pow(3.0,4))

}

class Addends {
    fun findAddendsForSum(nums: List<Int>, sum: Int): List<Pair<Int, Int>> {
        val indexed = nums.mapIndexed { index, num -> num to index }.toMap()
        return nums.mapIndexedNotNull { index, it ->
            val diff = sum - it
            val otherIndex = indexed[diff]
            if (otherIndex != null && diff != it) {
                Pair(index, otherIndex)
            } else {
                null
            }
        }
    }

    fun pow(num: Double, p: Int): Double {
        return (1..p).fold(1.0) { acc: Double, i: Int -> num * acc }
    }
}