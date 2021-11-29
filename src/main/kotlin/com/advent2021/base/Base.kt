package com.advent2021.base

import java.io.File

abstract class Base<T, V, U> {
    open fun readInput(filename: String, data: T): T {
        val file = File(filename)
        file.readLines().forEach {
            parseLine(it, data)
        }
        return data
    }

    abstract open fun parseLine(line: String, data: T)

    open fun solvePuzzle(filename: String, data: T) : V {
        val newData = readInput(filename, data)
        return computeSolution(data)
    }

    open fun solvePuzzle2(filename: String, data: T) : U {
        val newData = readInput(filename, data)
        return computeSolution2(data)
    }

    abstract open fun computeSolution(data: T): V
    abstract open fun computeSolution2(data: T): U
}