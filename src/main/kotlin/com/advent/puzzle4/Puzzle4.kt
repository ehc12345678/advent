package com.advent.puzzle4

import java.io.File

fun main() {
    val puzzle = Puzzle4()
    val inputs = puzzle.readInputs("inputs.txt")
    try {
        val valid = inputs.filter { puzzle.isValid(it) }
        println("Number of valids = ${valid.size}")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle4 {
    private val validFields = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid", "cid")

    fun isValid(passport: Map<String, String>) : Boolean {
        if (validate4Num(passport["byr"], 1920, 2002) &&
            validate4Num(passport["iyr"], 2010, 2020) &&
            validate4Num(passport["eyr"], 2020, 2030) &&
            validateHeight(passport["hgt"]) &&
            validateRegex(passport["hcl"], """#[\d|a-f]{6}""") &&
            validateRegex(passport["ecl"], """amb|blu|brn|gry|grn|hzl|oth""") &&
            validateRegex(passport["pid"], """\d{9}""")) {
                for (key in passport.keys) {
                    if (!validFields.contains(key)) {
                        return false
                    }
                }
            return true
        }
        return false
    }

    private fun validate4Num(str: String?, min: Int, max: Int) : Boolean {
        if (str.isNullOrEmpty()) {
            return false
        }
        if (!validateRegex(str, """\d{4}""")) {
            return false
        }
        val i = str.toInt()
        return i in min..max
    }

    private fun validateHeight(str: String?) : Boolean {
        if (str.isNullOrEmpty()) {
            return false
        }
        val matchResult = """(\d*)(cm|in)""".toRegex().find(str) ?: return false
        val num = matchResult.groups[1]?.value?.toInt()!!
        val isCm = matchResult.groups[2]?.value == "cm"
        return (isCm && num in 150..193) || (!isCm && num in 59..76)
    }

    private fun validateRegex(str: String?, regex: String): Boolean {
        if (!str.isNullOrEmpty() && str.matches(Regex(regex))) {
            return true
        }
        return false
    }

    fun readInputs(filename: String): List<Map<String, String>> {
        val file = File(filename)
        val ret : MutableList<MutableMap<String, String>> = ArrayList()
        ret.add(HashMap())
        file.readLines().forEach { parseLine(it, ret) }
        return ret
    }

    private fun parseLine(line: String, ret: MutableList<MutableMap<String, String>>) : List<MutableMap<String, String>> {
        if (line.isEmpty()) {
            ret.add(HashMap())
        } else {
            val current : MutableMap<String, String> = ret.last()
            val keyValues = line.split(" ")
            keyValues.forEach {
                val kv = it.split(":")
                val key: String = kv[0]
                val value: String = kv[1]
                current[key] = value
            }
        }
        return ret
    }

}