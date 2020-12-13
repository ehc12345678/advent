package com.advent.puzzle12

import java.io.File
import kotlin.math.abs

enum class Direction(var value: Int) { E(0), S(1), W(2), N(3) }
enum class InstructionType { E, S, W, N, L, R, F }
class Instruction(val type: InstructionType, val num: Int)

fun main() {
    val puzzle = Puzzle12()
    try {
        val instructions = puzzle.readInputs("inputs.txt")
        val robot = Robot()
        instructions.forEach {
            robot.doInstruction(it)
        }
        println("Final pos ${abs(robot.east) + abs(robot.north)}")

        testRotate()

        val waypoint = Robot(10, 1)
        robot.east = 0
        robot.north = 0
        instructions.forEach {
            robot.doInstruction2(it, waypoint)
        }
        println("Final pos ${abs(robot.east) + abs(robot.north)}")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

fun testRotate() {
    testOneRotate(30, 40, 1, 30, -40)
    testOneRotate(30, 40, 2, -30, -40)
    testOneRotate(30, 40, 3, -30, 40)
    testOneRotate(30, 40, -1, -30, 40)
    testOneRotate(30, 40, -2, -30, -40)
    testOneRotate(30, 40, -3, -30, 40)
}

fun testOneRotate(x: Int, y: Int, steps: Int, expectedX: Int, expectedY: Int) {
    val waypoint = Robot(x, y)
    waypoint.rotate(steps)
    assert(waypoint.east == expectedX)
    assert(waypoint.north == expectedY)
}

class Puzzle12 {
    fun readInputs(filename: String): List<Instruction> {
        val file = File(filename)
        val lines = file.readLines().map { parseLine(it) }
        return lines
    }

    fun parseLine(line: String) : Instruction {
        val type = InstructionType.valueOf(line.substring(0 until 1))
        val num = line.substring(1).toInt()
        return Instruction(type, num)
    }
}

class Robot(
    var east: Int = 0,
    var north: Int = 0,
    var dir: Direction = Direction.E
) {
    fun doInstruction(instruction: Instruction) {
        when (instruction.type) {
            InstructionType.E -> east += instruction.num
            InstructionType.S -> north -= instruction.num
            InstructionType.W -> east -= instruction.num
            InstructionType.N -> north += instruction.num
            InstructionType.L -> turn(-instruction.num / 90)
            InstructionType.R -> turn(instruction.num / 90)
            InstructionType.F -> doInstruction(Instruction(dirToType(dir), instruction.num))
        }
    }

    fun doInstruction2(instruction: Instruction, waypoint: Robot) {
        when (instruction.type) {
            InstructionType.E, InstructionType.S, InstructionType.N, InstructionType.W ->
                waypoint.doInstruction(instruction)
            InstructionType.L ->
                waypoint.rotate(4 - (instruction.num / 90))
            InstructionType.R ->
                waypoint.rotate(instruction.num / 90)
            InstructionType.F ->
                moveTowardsWaypoint(instruction.num, waypoint)
        }
        println("${instruction.type} ${instruction.num} (e=$east,n=$north) waypoint (e=${waypoint.east}, n=${waypoint.north})")
    }

    fun rotate(steps: Int) {
        for (i in 0 until steps) {
            when {
                (east >= 0 && north >= 0) -> north *= -1
                (east >= 0 && north <= 0) -> east *= -1
                (east <= 0 && north <= 0) -> north *= -1
                else -> east *= -1
            }
        }
    }

    private fun moveTowardsWaypoint(num: Int, waypoint: Robot) {
        east += num * waypoint.east
        north += num * waypoint.north
    }

    private fun dirToType(dir: Direction) : InstructionType {
        return InstructionType.valueOf(dir.toString())
    }

    private fun turn(steps: Int) {
        val newValue = (dir.value + steps + 4) % 4
        for (e in Direction.values()) {
            if (e.value == newValue) {
                dir = e
            }
        }
    }

}


