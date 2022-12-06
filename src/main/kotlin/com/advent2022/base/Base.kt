package com.advent2022.base

import java.io.File
import java.io.IOException

abstract class Base<T, V, U> {
    @Throws(IOException::class)
    open fun readInput(filename: String, data: T, parseLineFunc: (String, T) -> Unit): T {
        try {
            val file = File(filename)
            file.readLines().forEach {
                parseLineFunc(it, data)
            }
            return data
        } catch (ex: IOException) {
            ex.printStackTrace()
            throw ex
        }
    }

    abstract fun parseLine(line: String, data: T)
    open fun parseLine2(line: String, data: T) = parseLine(line, data)

    @Throws(IOException::class)
    open fun solvePuzzle(filename: String, data: T) : V {
        val newData = readInput(filename, data, this::parseLine)
        return computeSolution(newData)
    }

    @Throws(IOException::class)
    open fun solvePuzzle2(filename: String, data: T) : U {
        val newData = readInput(filename, data, this::parseLine2)
        return computeSolution2(newData)
    }

    abstract fun computeSolution(data: T): V
    abstract fun computeSolution2(data: T): U
}