package com.advent.puzzle19

import java.io.File

fun main() {
    val puzzle = Puzzle19()
    try {
        val data = puzzle.readInputs("test2.txt")
//        val countValids = data.strs.count { puzzle.isValid(data, it) }
//        println("valid count is $countValids")

        data.rules[8] = puzzle.parseRule("8: 42 8 | 42")
        data.rules[11] = puzzle.parseRule("11: 42 11 31 | 42 31")
        val countValids2 = data.strs.count { puzzle.isValid(data, it) }
        println("valid count2 is $countValids2")
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

    fun isValid(data: Data, str: String) : Boolean {
        val fullTraverse = traverse(data, data.rules[0]!!, str)
        val isValid = fullTraverse == str
        if (!isValid) {
            println(str)
        }
        return isValid
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
                substr = traverseSegment(data, rule, rule.ruleSegments[i++], str)
            }
        }
        if (substr != null) {
            debug("Rule ${rule.key} found $substr")
        }
        return substr
    }

    fun traverseSegment(data: Data, parent: Rule, ruleSegment: RuleSegment, str: String) : String? {
        // all rule segments have exactly two rule references, so this is safe
        debug("\tSegment $ruleSegment")
        if (ruleSegment.contains(parent.key)) {
            return handleSefRefs(data, ruleSegment, str)
        }
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

    fun handleSefRefs(data: Data, ruleSegment: RuleSegment, str: String): String? {
        // we know that we end with the self ref, it is as a reg exp (a)+
        val first = data.rules[ruleSegment[0]]
        val strFirst = traverse(data, first!!, str) ?: return null
        if (ruleSegment.size == 2) {
            var workingStr = strFirst
            var other = ""
            while (workingStr.length <= str.length) {
                val restString = str.substring(workingStr.length)
                other = traverse(data, first, restString) ?: break
                workingStr += other
            }
            // remove the last match so that the next one can pick it up
            return workingStr.substring(0, workingStr.length - other.length)
        }
        // we know that this is a (a b)* b, so we can look for that
        else if (ruleSegment.size == 3) {
            var workingStr = strFirst
            var countFirst = 1
            while (workingStr.length < str.length) {
                val restString = str.substring(workingStr.length)
                val nextOfFirst = traverse(data, first, restString) ?: break
                workingStr += nextOfFirst
                countFirst++
            }

            // need to find just as many lasts as firsts
            for (countLast in 0 until countFirst + 1) {
                val last = data.rules[ruleSegment[2]]
                val restString = str.substring(workingStr.length)
                val strLast = traverse(data, last!!, restString) ?: return null
                workingStr += strLast
            }
            return workingStr
        }
        return null
    }

    fun debug(str: String) {
        if (System.getenv("DEBUG") != null) {
            println(str)
        }
    }
}
