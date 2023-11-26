package com.advent.advent2020.puzzle2

import java.io.File
import java.lang.IllegalArgumentException

fun main() {
    val passwords = Puzzle2()
    val inputs = passwords.readInputs("inputs.txt")
    val valids = inputs.filter { passwords.isValid(it) }
    println("valids = ${valids.size}")

    val valids2 = inputs.filter { passwords.isValid2(it) }
    println("valids = ${valids2.size}")
}

class PasswordPlusValidation(
    val minNum: Int,
    val maxNum: Int,
    val ch: Char,
    val password: String
)

class Puzzle2 {

    fun readInputs(filename: String) : List<PasswordPlusValidation> {
        val file = File(filename)
        return file.readLines().map { parseLine(it) }
    }

    fun parseLine(line: String) : PasswordPlusValidation {
        val parts = line.split(" ")
        if (parts.size == 3) {
            val nums = parts[0].split("-")
            val min = nums[0].toInt()
            val max = nums[1].toInt()
            val ch = parts[1][0]
            val password = parts[2]
            return PasswordPlusValidation(min, max, ch, password)
        }
        throw IllegalArgumentException("Invalid input ${line}")
    }

    fun isValid(passwordPlusValidation: PasswordPlusValidation) : Boolean {
        val count = passwordPlusValidation.password.count {
            it == passwordPlusValidation.ch
        }
        return count in passwordPlusValidation.minNum..passwordPlusValidation.maxNum
    }

    fun isValid2(passwordPlusValidation: PasswordPlusValidation) : Boolean {
        val password = passwordPlusValidation.password
        val ch1 = password[passwordPlusValidation.minNum - 1]
        val ch2 = password[passwordPlusValidation.maxNum - 1]
        return (ch1 == passwordPlusValidation.ch) xor (ch2 == passwordPlusValidation.ch)
    }

}