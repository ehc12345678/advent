package com.advent.puzzle12

import java.io.File
import kotlin.math.abs

enum class Direction(var value: Int) { E(0), S(1), W(2), N(3) }
enum class InstructionType { E, S, W, N, L, R, F }
class Instruction(val type: InstructionType, val num: Int)

fun main() {
    val puzzle = Puzzle12()
    try {
        val instructions = puzzle.readInputs("test.txt")
        val robot = Robot()
        instructions.forEach {
            robot.doInstruction(it)
        }
        println("Final pos ${abs(robot.x) + abs(robot.y)}")

        val waypoint = Robot(10, 1)
        robot.x = 0
        robot.y = 0
        instructions.forEach {
            robot.doInstruction2(it, waypoint)
        }
        println("Final pos ${abs(robot.x) + abs(robot.y)}")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
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
    var x: Int = 0,
    var y: Int = 0,
    var dir: Direction = Direction.E
) {
    fun doInstruction(instruction: Instruction) {
        when (instruction.type) {
            InstructionType.E -> x += instruction.num
            InstructionType.S -> y -= instruction.num
            InstructionType.W -> x -= instruction.num
            InstructionType.N -> y += instruction.num
            InstructionType.L -> turn(-instruction.num / 90)
            InstructionType.R -> turn(instruction.num / 90)
            InstructionType.F -> doInstruction(Instruction(dirToType(dir), instruction.num))
        }
    }

    fun doInstruction2(instruction: Instruction, waypoint: Robot) {
        when (instruction.type) {
            InstructionType.E, InstructionType.S, InstructionType.N, InstructionType.W ->
                waypoint.doInstruction(instruction)
            InstructionType.L -> rotateWaypointAroundRobot(-instruction.num / 90, waypoint)
            InstructionType.R -> rotateWaypointAroundRobot(instruction.num / 90, waypoint)
            InstructionType.F -> moveTowardsWaypoint(instruction.num, waypoint)
        }
        println("${instruction.type} ${instruction.num} ($x,$y) waypoint (${waypoint.x}, ${waypoint.y})")
    }

    private fun rotateWaypointAroundRobot(steps: Int, waypoint: Robot) {
        for (i in 0 until (((steps + 4) % 4))) {
            rotatePointOnce(waypoint)
        }
    }

    private fun rotatePointOnce(waypoint: Robot) {
        when {
            (waypoint.x >= 0 && waypoint.y >= 0) -> waypoint.y *= -1
            (waypoint.x >= 0 && waypoint.y <= 0) -> waypoint.x *= -1
            (waypoint.x <= 0 && waypoint.y <= 0) -> waypoint.y *= -1
            else -> waypoint.x *= -1
        }
    }

    private fun moveTowardsWaypoint(num: Int, waypoint: Robot) {
        x += num * waypoint.x
        y += num * waypoint.y
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


