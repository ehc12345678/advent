package com.advent2022.puzzle22

import com.advent2021.puzzle22.Puzzle22

abstract class FileSetup() {
    val wrapToBeginLine = { pos: Pos, face: Face -> Pos(pos.row, face.colRange.first) }
    val wrapToEndLine = { pos: Pos, face: Face -> Pos(pos.row, face.colRange.last) }
    val wrapToTopLine = { pos: Pos, face: Face -> Pos(face.rowRange.first, pos.col) }
    val wrapToBottomLine = { pos: Pos, face: Face -> Pos(face.rowRange.last, pos.col) }
    val wrapFlipRowCol = { pos: Pos, _: Face -> Pos(pos.col, pos.row) }
    fun setup1(data: Data) {
        val faces = breakIntoFaces(data)
        connectFacesPart1(faces)
        data.robot.face = faces[FaceSide.TOP]
    }

    fun setup2(data: Data) {
        val faces = breakIntoFaces(data)
        connectFacesPart2(faces)
        data.robot.face = faces[FaceSide.TOP]
    }

    abstract fun breakIntoFaces(data: Data): Map<FaceSide, Face>

    abstract fun connectFacesPart1(faces: Map<FaceSide, Face>)

    abstract fun adjustPos(pos: Pos, face: Face): Pos
    abstract fun connectFacesPart2(faces: Map<FaceSide, Face>)
    
    fun connectFaceToFace(face1: Face, dir1: Dir, face2: Face, dir2: Dir) {
        when (dir1) {
            Dir.UP -> {
                when (dir2) {
                    Dir.UP -> TODO()
                    Dir.DOWN -> {
                        face1.addConnection(dir2, FaceConnection(face2, wrapToBottomLine))
                        face2.addConnection(dir1, FaceConnection(face1, wrapToTopLine))
                    }
                    Dir.LEFT -> TODO()
                    Dir.RIGHT -> TODO()
                }
            }
            Dir.DOWN -> {
                when (dir2) {
                    Dir.UP -> connectFaceToFace(face2, dir2, face1, dir2)
                    Dir.DOWN -> TODO()
                    Dir.LEFT -> TODO()
                    Dir.RIGHT -> TODO()
                }
            }
            Dir.LEFT -> {
                when (dir2) {
                    Dir.UP -> TODO()
                    Dir.DOWN -> TODO()
                    Dir.LEFT -> TODO()
                    Dir.RIGHT -> {
                        face1.addConnection(dir2, FaceConnection(face2, wrapToEndLine))
                        face2.addConnection(dir1, FaceConnection(face1, wrapToBeginLine))
                    }
                }
            }
            Dir.RIGHT -> {
                when (dir2) {
                    Dir.UP -> TODO()
                    Dir.DOWN -> TODO()
                    Dir.LEFT -> connectFaceToFace(face2, dir2, face1, dir2)
                    Dir.RIGHT -> TODO()
                }
            }
        }
    }

    private fun getOpposite(dir: Dir): Dir {
        return when(dir) {
            Dir.UP -> Dir.DOWN
            Dir.DOWN -> Dir.UP
            Dir.LEFT -> Dir.RIGHT
            Dir.RIGHT -> Dir.LEFT
        }
    }

}