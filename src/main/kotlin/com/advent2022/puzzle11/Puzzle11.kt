package com.advent2022.puzzle11

import com.advent2021.base.Base

typealias ItemOp = (old: Long) -> Long
data class ModItem(val item: Long, val mod: Int)
typealias ModItemMap = HashMap<Int, ModItem>
typealias ModItemOp = (old: ModItemMap) -> ModItemMap

class Monkey {
    var numItemsInspected = 0

    var items = ArrayList<Long>()
    var modItems = ArrayList<ModItemMap>()
    var op: ItemOp = { it }
    var modOp: ModItemOp = { it }
    var divisibleBy = 1
    var trueIndex = 0
    var falseIndex = 0

    fun inspectItem(data: Data) {
        var item = items.removeFirst()
        item = op(item) / 3
        ++numItemsInspected

        val index = if ((item % divisibleBy) == 0L) trueIndex else falseIndex
        val nextMonkey = data.monkies[index]
        nextMonkey.items.add(item)
    }

    fun inspectItemTwo(data: Data) {
        val modItem = modItems.removeFirst()
        val opedModItem = modOp(modItem)
        ++numItemsInspected

        val modIndex = if (opedModItem[divisibleBy]?.item!! % divisibleBy == 0L) trueIndex else falseIndex
        val nextMonkey = data.monkies[modIndex]
        nextMonkey.modItems.add(opedModItem)
    }
}
class Data {
    val monkies = ArrayList<Monkey>()

    fun addMonkey() {
        monkies.add(Monkey())
    }

    fun last() = monkies.last()
}
typealias Solution = Long
typealias Solution2 = Long

fun main() {
    try {
        val puz = Puzzle11()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle11 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val trimmed = line.trim()
        when {
            trimmed.startsWith("Monkey") -> data.addMonkey()
            trimmed.startsWith("Starting items:") -> {
                val monkey = data.last()
                val items = trimmed.substringAfter("Starting items: ").split(", ").map { it.toLong() }
                monkey.items.addAll(items)
            }
            trimmed.startsWith("Operation:") -> {
                val monkey = data.last()
                val parts = trimmed.substringAfter("new = old ").split(" ")
                monkey.op = when (parts[0]) {
                    "*" -> { item -> mult(item, if (parts[1] == "old") item else parts[1].toLong(), monkey.divisibleBy) }
                    "+" -> { item -> add(item, if (parts[1] == "old") item else parts[1].toLong(), monkey.divisibleBy) }
                    else -> throw IllegalArgumentException("Don't know op ${parts[0]}")
                }
                monkey.modOp = when (parts[0]) {
                    "*" -> { item -> multMods(item, parts[1]) }
                    "+" -> { item -> addMods(item, parts[1]) }
                    else -> throw IllegalArgumentException("Don't know op ${parts[0]}")
                }
            }
            trimmed.startsWith("Test:") -> {
                val monkey = data.last()
                monkey.divisibleBy = trimmed.substringAfter("divisible by ").toInt()
            }
            trimmed.startsWith("If true: ") -> {
                val monkey = data.last()
                monkey.trueIndex = trimmed.substringAfter("throw to monkey ").toInt()
            }
            trimmed.startsWith("If false: ") -> {
                val monkey = data.last()
                monkey.falseIndex = trimmed.substringAfter("throw to monkey ").toInt()
            }
        }
    }

    private fun multMods(item: ModItemMap, str: String): ModItemMap {
        var ret = ModItemMap()
        item.forEach { mod, value ->
            val factor = if (str == "old") value.item else str.toLong()
            val modFactor = factor % mod
            val multMods = (value.item * modFactor) % mod
            ret[mod] = ModItem(multMods, mod)
        }
        return ret
    }

    private fun addMods(item: ModItemMap, str: String): ModItemMap {
        var ret = ModItemMap()
        item.forEach { mod, value ->
            val factor = if (str == "old") value.item else str.toLong()
            val multMods = (value.item + factor) % mod
            ret[mod] = ModItem(multMods, mod)
        }
        return ret
    }

    private fun mult(item: Long, factor: Long, mod: Int): Long = item * factor
    private fun add(item: Long, factor: Long, mod: Int): Long = item + factor

    override fun computeSolution(data: Data): Solution {
        return compute(data, true, 20)
    }
    
    override fun computeSolution2(data: Data): Solution2 {
        return compute(data, false, 10000)
    }

    private fun compute(data: Data, worryReduction: Boolean, numTimes: Int): Long {
        val divisors = data.monkies.map { it.divisibleBy }

        // track all the items with all the mods
        data.monkies.forEach { monkey ->
            monkey.modItems = ArrayList(monkey.items.map { item ->
                ModItemMap().also { modItem ->
                    divisors.forEach { mod ->
                        modItem.put(mod, ModItem(item % mod, mod))
                    }
                }
            })
        }

        repeat(numTimes) {
            data.monkies.forEach { monkey ->
                doRoundWithMonkey(data, monkey, worryReduction)
            }

            val num = it + 1
            if ((num % 1000) == 0 || num == 1 || num == 20) {
                println("== After round $num")
                data.monkies.forEachIndexed { index, monkey -> println("Monkey $index inspected ${monkey.numItemsInspected} times.")}
                println()
            }
        }
        val sorted = data.monkies.sortedByDescending { it.numItemsInspected }
        return sorted[0].numItemsInspected.toLong() * sorted[1].numItemsInspected.toLong()
    }

    private fun doRoundWithMonkey(data: Data, monkey: Monkey, worryReduction: Boolean) {
        if (worryReduction) {
            while (monkey.items.isNotEmpty()) {
                monkey.inspectItem(data)
            }
        } else {
            while (monkey.modItems.isNotEmpty()) {
                monkey.inspectItemTwo(data)
            }
        }
    }
}

