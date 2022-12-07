package com.advent2022.puzzle7

import com.advent2021.base.Base

open class FsItem(val name: String) {
    open val size: Long
        get() = 0
}
class File(name: String, override val size: Long) : FsItem(name)
class Directory(name: String, val parent: Directory?): FsItem(name) {
    val fsItems = HashMap<String, FsItem>()
    var calcSize: Long? = null
    override val size: Long
        get() {
            if (calcSize == null) {
                calcSize = fsItems.values.sumOf { it.size }
            }
            return calcSize ?: 0
        }

    fun mkdir(name: String): Directory {
        return fsItems.computeIfAbsent(name) { Directory(name, this) } as Directory
    }

    fun addFile(size: Long, name: String): File {
        val newFile = File(name, size)
        fsItems.put(name, newFile)
        calcSize = null  // recompute size
        return newFile
    }

    val childDirs: List<Directory>
        get() = fsItems.values.filterIsInstance<Directory>()

    override fun toString(): String {
        return "dir $name $size"
    }
}

class Data {
    val root = Directory("/", null)
    var cwd = root
}
typealias Solution = Long
typealias Solution2 = Solution

fun main() {
    try {
        val puz = Puzzle7()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

// https://adventofcode.com/2022/day/7

enum class Command { ls, cd }
class Puzzle7 : Base<Data, Solution?, Solution2?>() {
    var currentCommand: Command = Command.cd
    override fun parseLine(line: String, data: Data) {
        val parts = line.split(" ")
        when (parts[0]) {
            "$" -> {
                currentCommand = Command.valueOf(parts[1])
                if (currentCommand == Command.cd) {
                    data.cwd = when (parts[2]) {
                        "/" -> data.root
                        ".." -> data.cwd.parent!!
                        else -> data.cwd.mkdir(parts[2])
                    }
                }
            }
            "dir" -> data.cwd.mkdir(parts[1])
            else -> data.cwd.addFile(parts[0].toLong(), parts[1])
        }
    }

    override fun computeSolution(data: Data): Solution {
        return dftTotal(data.root)
    }

    override fun computeSolution2(data: Data): Solution2 {
        val overallSpace = data.root.size
        val unusedSpace = 70000000L - overallSpace
        val needToDelete = 30000000L - unusedSpace

        val candidates = ArrayList<Directory>()
        dftGather(data.root, candidates, needToDelete)
        val sorted = candidates.sortedBy { it.size }
        return sorted.first().size
    }

    fun dftTotal(d: Directory): Long {
        val totalThis = d.size
        var ret = 0L
        if (totalThis <= 100000) {
            ret += totalThis
        }
        return ret + d.childDirs.sumOf { dftTotal(it) }
    }

    fun dftGather(d: Directory, candidates: ArrayList<Directory>, unusedToDelete: Long) {
        if (d.size >= unusedToDelete) {
            candidates.add(d)
        }
        d.childDirs.forEach { dftGather(it, candidates, unusedToDelete) }
    }
}

