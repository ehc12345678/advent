package com.advent.puzzle25

import com.advent.puzzle13.Bus
import java.io.File
import java.lang.RuntimeException
import java.math.BigInteger

typealias Data = Pair<Long, Long>

fun main() {
    val puzzle = Puzzle25()
    try {
        val data = puzzle.readInputs("inputs.txt")
        val answerA = puzzle.solutionA(data)
        println("Answer A is $answerA")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class Puzzle25 {
    fun readInputs(filename: String): Data {
        val file = File(filename)
        val lines = file.readLines()
        return Data(lines[0].toLong(), lines[1].toLong())
    }

    fun solutionA(data: Data) : Long {
        val cardPublicKey = data.first
        val doorPublicKey = data.second

        val mod: Long = 20201227
        val cardLoopSize = transformSubjectNumber(7, cardPublicKey, mod)
        val doorLoopSize = transformSubjectNumber(7, doorPublicKey, mod)

        val doorEncrypt = invert(doorPublicKey, cardLoopSize, mod)
        val cardEncrypt = invert(cardPublicKey, doorLoopSize, mod)
        if (doorEncrypt != cardEncrypt) {
            throw RuntimeException("Oops")
        }
        return doorEncrypt
    }

    fun transformSubjectNumber(subject: Long, target: Long, mod: Long) : Long {
        var now = 1L
        var step = 0L
        do {
            now = (now * subject) % mod
            ++step
        } while (now != target)

        return step
    }

    fun invert(subject: Long, loopSize: Long, mod: Long) : Long {
        var now = 1L
        for (i in 0 until loopSize) {
            now = (now * subject) % mod
        }
        return now
    }

}