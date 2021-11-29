package com.advent.advent2020.puzzle19

import java.io.File

fun main() {
    val puzzle = Puzzle19()
    try {
        val data = puzzle.readInputs("inputs.txt")
        val countValids = data.strs.count { puzzle.isValid(data, it) }
        println("valid count is $countValids")

        val countValidsB = data.strs.count { puzzle.isValidPartB(data, it) }
        println("B count is $countValidsB")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

typealias RuleSegment = List<Int>

class Rule(val key: Int, val str: String? = null, val ruleSegments: List<RuleSegment>? = null) {
    override fun toString(): String {
        if (ruleSegments != null) {
            return "$key: ${ruleSegments.joinToString(" | ")}"
        } else {
            return "$key: $str"
        }
    }
}

class Data {
    var rules = HashMap<Int, Rule>()
    var strs = ArrayList<String>()

    override fun toString(): String {
        return """
            Rules: ${rules.values}
            Strings: $strs
        """.trimIndent()
    }
}

class Puzzle19 {
    fun readInputs(filename: String): Data {
        val file = File(filename)
        val lines = file.readLines()
        var i = 0

        val data = Data()
        while (i < lines.size && lines[i].isNotEmpty()) {
            val rule = parseRule(lines[i++])
            data.rules[rule.key] = rule
        }
        ++i
        while (i < lines.size) {
            data.strs.add(lines[i++])
        }
        return data
    }

    fun parseRule(line: String) : Rule {
        val colonIndex = line.indexOf(':')
        val key = line.substring(0 until colonIndex).toInt()
        val rule: Rule

        val rest = line.substring(colonIndex + 2)
        if (rest.startsWith("\"")) {
            rule = Rule(key, str = rest.replace("\"",""))
        } else {
            val segmentStrs = rest.split("|").map { it.trim() }
            rule = Rule(key, ruleSegments = segmentStrs.map { segment -> segment.split(" ").map { it.toInt() } })
        }
        return rule
    }

    fun isValidPartB(data: Data, str: String) : Boolean {
        // rule 0 is 8 11
        // rule 8: is 42 | 8
        // rule 11: is 42 31 | 42 11 31
        // so we need at least one or more 42s... like this 42 42 42 42 42 ...
        //    if we do not see a 42, it must be a 31... we should have a number of 31s that is at least one less than the 42s
        //    42 42 (42 31) works, 42 (42 31) works, but not 42 42 31 31 (2 31s >= 2 42s)
        //    (42 42 42 42 42) 42 31
        val rule42 = data.rules[42]
        val rule31 = data.rules[31]
        val strFirst = traverse(data, rule42!!, str) ?: return false
        var count42 = 0

        var workingStr = ""
        var next42 : String? = strFirst
        while (next42 != null) {
            workingStr += next42
            val restString = str.substring(workingStr.length)
            next42 = traverse(data, rule42, restString)
            count42++
        }

        var count31 = 0
        do {
            val restString = str.substring(workingStr.length)
            val next31 = traverse(data, rule31!!, restString) ?: return false
            workingStr += next31
            count31++
        } while (workingStr.length < str.length)

        val isValid = workingStr == str && count31 < count42
        if (isValid) {
            println(str)
        }
        return isValid
    }

    fun isValid(data: Data, str: String) : Boolean {
        val fullTraverse = traverse(data, data.rules[0]!!, str)
        return fullTraverse == str
    }

    fun traverse(data: Data, rule: Rule, str: String) : String? {
        debug("Checking ${rule.key}")
        if (rule.str != null) {
            val leaf = if (str.startsWith(rule.str)) rule.str else null
            if (leaf != null) {
                debug("Leaf ${rule.key} found $leaf")
            }
            return leaf
        }
        var substr: String? = null
        if (rule.ruleSegments != null) {
            var i = 0
            while (substr == null && i < rule.ruleSegments.size) {
                substr = traverseSegment(data, rule.ruleSegments[i++], str)
            }
        }
        if (substr != null) {
            debug("Rule ${rule.key} found $substr")
        }
        return substr
    }

    fun traverseSegment(data: Data, ruleSegment: RuleSegment, str: String) : String? {
        debug("\tSegment $ruleSegment")
        var workingStr: String? = ""
        for (i in ruleSegment.indices) {
            val rule = data.rules[ruleSegment[i]]
            val restString = str.substring(workingStr!!.length)
            val strRule = traverse(data, rule!!, restString)
            if (strRule != null && strRule.length <= restString.length) {
                workingStr += strRule
            } else {
                workingStr = null
                break
            }
            debug("\tWorking string: $workingStr")
        }
        if (workingStr != null) {
            debug("\tSegment ${ruleSegment} found ${workingStr}")
        }
        return workingStr
    }

    fun debug(str: String) {
        if (System.getenv("DEBUG") != null) {
            println(str)
        }
    }
}
