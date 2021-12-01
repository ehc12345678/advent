package com.advent2021.base

import java.io.File
import java.io.IOException

abstract class Base<T, V, U> {
    @Throws(IOException::class)
    open fun readInput(filename: String, data: T): T {
        try {
            val file = File(filename)
            file.readLines().forEach {
                parseLine(it, data)
            }
            return data
        } catch (ex: IOException) {
            ex.printStackTrace()
            throw ex
        }
    }

    abstract fun parseLine(line: String, data: T)

    @Throws(IOException::class)
    open fun solvePuzzle(filename: String, data: T) : V {
        val newData = readInput(filename, data)
        return computeSolution(data)
    }

    @Throws(IOException::class)
    open fun solvePuzzle2(filename: String, data: T) : U {
        val newData = readInput(filename, data)
        return computeSolution2(data)
    }

    abstract fun computeSolution(data: T): V
    abstract fun computeSolution2(data: T): U
}