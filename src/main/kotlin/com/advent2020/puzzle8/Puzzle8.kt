package com.advent.advent2020.puzzle8

import java.io.File
import java.util.Stack

enum class InstructionType { ACC, JMP, NOP }
data class State(var acc: Int, var current: Int, var done: Boolean = false)

fun main() {
    val puzzle = Puzzle8()
    try {
        val instructions = puzzle.readInputs("inputs.txt")
        val run = Stack<Instruction>()
        val acc = puzzle.findJustBeforeRepeat(instructions, run)
        println("Accumulator is $acc")

        val acc2 = puzzle.findJustBeforeRepeatWithRecover(instructions, run, acc)
        println("After recover is $acc2")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}


class Instruction(
    var type: InstructionType,
    val num: Int)

class Puzzle8 {
    fun readInputs(filename: String): List<Instruction> {
        val file = File(filename)
        val lines = file.readLines().map { parseLine(it) }
        return lines
    }

    fun parseLine(line: String) : Instruction {
        val split = line.split(" ")
        val type = InstructionType.valueOf(split[0].toUpperCase())
        val plus = split[1][0] == '+'
        var num = split[1].substring(1).toInt()

        if (!plus) {
            num *= -1
        }
        return Instruction(type, num)
    }

    fun findJustBeforeRepeat(instructions: List<Instruction>, run: Stack<Instruction>) : State {
        val seen = HashSet<Int>()
        var state = State(0, 0)

        var repeat = false
        while (!repeat && !state.done) {
            val instruction = instructions[state.current]
            run.add(instruction)
            val newState = getNewState(state, instruction.type, instruction.num)
            newState.done = newState.current == instructions.size
            repeat = !seen.add(newState.current)
            state = newState
        }
        return state
    }

    fun findJustBeforeRepeatWithRecover(instructions: List<Instruction>, run: Stack<Instruction>, state: State) : State {
        var newState = State(state.acc, state.current, state.done)
        while (!newState.done && !run.empty()) {
            var topInstruction = run.peek()
            while (!run.isEmpty() && topInstruction.type == InstructionType.ACC) {
                topInstruction = run.pop()
                newState = undo(state, topInstruction.type, topInstruction.num)
            }

            val originalType = topInstruction.type
            val flipType = if (originalType == InstructionType.NOP) InstructionType.JMP else InstructionType.NOP
            topInstruction.type = flipType
            val maybeState = findJustBeforeRepeat(instructions, Stack())
            if (maybeState.done == true) {
                newState = maybeState
            } else {
                topInstruction.type = originalType
                newState = undo(newState, topInstruction.type, topInstruction.num)
                run.pop()
            }
        }
        return newState
    }

    private fun getNewState(state: State, type: InstructionType, num: Int) : State {
        return when (type) {
            InstructionType.ACC -> {
                State(state.acc + num, state.current + 1)
            }
            InstructionType.JMP -> {
                State(state.acc, state.current + num)
            }
            InstructionType.NOP -> {
                State(state.acc, state.current + 1)
            }
        }
    }

    private fun undo(state: State, type: InstructionType, num: Int) : State {
        return when (type) {
            InstructionType.ACC -> {
                State(state.acc - num, state.current - 1)
            }
            InstructionType.JMP -> {
                State(state.acc, state.current - num)
            }
            InstructionType.NOP -> {
                State(state.acc, state.current - 1)
            }
        }
    }
}