package com.advent2022.puzzle22

import com.advent2021.base.Base

data class Line(val str: String, val colsRange: IntRange) {
    fun getCh(col: Int) = if (col <= str.length && col > 0) str[col - 1] else ' '
}
data class Pos(val row: Int, val col: Int) {
    operator fun plus(pos: Pos) = Pos(row + pos.row, col + pos.col)
}
enum class FaceSide { TOP, BOTTOM, LEFT, RIGHT, BACK, FRONT }
typealias PosTransform = (pos: Pos, destFace: Face) -> Pos
typealias DirTransform = (dir: Dir) -> Dir

val identity = { dir: Dir -> dir }
fun returnThis(dir: Dir): DirTransform = { _: Dir -> dir }

data class FaceConnection(val face: Face, val posTransform: PosTransform, val dirTransform: DirTransform = identity)
class Face(val side: FaceSide, val index: Int, var lines: ArrayList<Line> = ArrayList()) {
    var connections = HashMap<Dir, FaceConnection>()
    val rowRange: IntRange
        get() = 1 .. lines.size
    val colRange: IntRange
        get() = getLine(1).colsRange

    fun getLine(row: Int) = lines[row - 1]
    fun getCh(pos: Pos): Char {
        if (pos.row !in rowRange || pos.col !in colRange) {
            return ' '
        }
        return getLine(pos.row).getCh(pos.col)
    }

    fun addLine(str: String) {
        var trimmed = str.trim()
        lines.add(Line(trimmed, 1 .. trimmed.length))
    }

    // returns the direction the position is out of bounds, null if it is in bounds
    fun outOfBounds(pos: Pos): Dir? {
        return when {
            pos.col < colRange.first -> Dir.LEFT
            pos.col > colRange.last -> Dir.RIGHT
            pos.row < colRange.first -> Dir.UP
            pos.row > colRange.last -> Dir.DOWN
            else -> null
        }
    }

    fun addConnection(direction: Dir, faceConnection: FaceConnection) {
        connections[direction] = faceConnection
    }

    val height: Int
        get() = rowRange.last - rowRange.first + 1
    val width: Int
        get() = colRange.last - colRange.first + 1

}
data class Instruction(val move: Int?, val rotate: Rotate?)
enum class Rotate { L, R }
class Data {
    val lines = ArrayList<Line>()
    val robot = Robot()
    val instructions = ArrayList<Instruction>()

    val rowRange: IntRange
        get() = 1 .. lines.size

    fun addLine(str: String) {
        var begin = 0
        while (str[begin] == ' ') {
            ++begin
        }
        var end = str.indexOf(' ', begin)
        if (end < 0) {
            end = str.length
        }
        lines.add(Line(str, begin + 1 .. end))
    }

    fun doInstruction(instruction: Instruction) {
        if (instruction.move != null) {
            moveRobot(instruction.move)
        } else if (instruction.rotate != null) {
            rotateRobot(instruction.rotate)
        }
    }

    fun moveRobot(steps: Int) {
        repeat(steps) {
            val newPos = getPos(robot.pos, robot.direction)
            if (canStep(newPos)) {
                robot.pos = newPos.pos
                robot.face = newPos.face
                robot.direction = newPos.dir
            } else {
                // once we cannot move, we are done
                return
            }
        }
    }

    fun getLine(row: Int) = lines[row - 1]

    data class NewPosState(val pos: Pos, val face: Face, val dir: Dir)
    fun getPos(pos: Pos, direction: Dir): NewPosState {
        var newPos = getPosUnwrapped(pos, direction)
        var face = robot.face!!
        val outBoundsDir = face.outOfBounds(newPos)
        var newDir = direction

        if (outBoundsDir != null) {
            val connection = robot.face!!.connections[outBoundsDir]!!
            face = connection.face
            newPos = connection.posTransform(pos, connection.face)
            newDir = connection.dirTransform(direction)
        }
        return NewPosState(newPos, face, newDir)
    }

    private fun getPosUnwrapped(pos: Pos, direction: Dir): Pos {
        return when (direction) {
            Dir.UP -> pos + Pos(-1, 0)
            Dir.DOWN -> pos + Pos(1, 0)
            Dir.LEFT -> pos + Pos(0, -1)
            Dir.RIGHT -> pos + Pos(0, 1)
        }
    }

    fun canStep(newPosState: NewPosState): Boolean {
        return when (newPosState.face.getCh(newPosState.pos)) {
            '#' -> false
            '.' -> true
            else -> throw IllegalArgumentException("Something went wrong $newPosState")
        }
    }

    fun rotateRobot(rotate: Rotate) {
        robot.direction = if (rotate == Rotate.L)
            rotateClockwise(robot.direction)
        else
            rotateCounterClockwise(robot.direction)
    }

    fun rotateClockwise(direction: Dir): Dir {
        return when (direction) {
            Dir.UP -> Dir.LEFT
            Dir.DOWN -> Dir.RIGHT
            Dir.LEFT -> Dir.DOWN
            Dir.RIGHT -> Dir.UP
        }
    }

    fun rotateCounterClockwise(direction: Dir): Dir {
        return when (direction) {
            Dir.UP -> Dir.RIGHT
            Dir.DOWN -> Dir.LEFT
            Dir.LEFT -> Dir.UP
            Dir.RIGHT -> Dir.DOWN
        }
    }
}
enum class Dir { UP, DOWN, LEFT, RIGHT }
data class Robot(var pos: Pos = Pos(1, 1), var direction: Dir = Dir.RIGHT, var face: Face? = null)

typealias Solution = Int
typealias Solution2 = Solution

val isTest = true

fun main() {
    try {
        val puz = Puzzle22()
        val file = if (isTest) "inputsTest.txt" else "inputs.txt"
        val solution1 = puz.solvePuzzle(file, Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2(file, Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle22 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        if (line.contains(".") || line.contains("#")) {
            data.addLine(line)
        } else if (line.isNotEmpty()) {
            var startNumber = 0
            while (startNumber < line.length) {
                var endNumber = startNumber
                while (endNumber < line.length && line[endNumber].isDigit()) {
                    ++endNumber
                }
                val moves = line.substring(startNumber, endNumber).toInt()
                data.instructions.add(Instruction(moves, null))

                startNumber = endNumber
                if (startNumber < line.length) {
                    val dir = if (line[startNumber++] == 'L') Rotate.L else Rotate.R
                    data.instructions.add(Instruction(null, dir))
                }
            }
        }
    }

    override fun computeSolution(data: Data): Solution {
        val setup: FileSetup = if (isTest) { TestFileSetup() } else { RealFileSetup() }
        setup.setup1(data)

        data.instructions.forEach {
            data.doInstruction(it)
        }
        return password(data.robot, setup)
    }

    override fun computeSolution2(data: Data): Solution2 {
        val setup: FileSetup = if (isTest) { TestFileSetup() } else { RealFileSetup() }
        setup.setup2(data)

        data.instructions.forEach {
            data.doInstruction(it)
        }
        return password(data.robot, setup)
    }

    fun password(robot: Robot, setup: FileSetup): Solution {
        val face = robot.face!!
        val pos = setup.adjustPos(robot.pos, face)
        return pos.row * 1000 + pos.col * 4 + when (robot.direction) {
            Dir.UP -> 3
            Dir.DOWN -> 1
            Dir.LEFT -> 2
            Dir.RIGHT -> 0
        }
    }
}

