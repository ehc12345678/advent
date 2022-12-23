package com.advent2022.puzzle23

abstract class FileSetup {
    fun setup(data: Data) {
        val faces = breakIntoFaces(data)
        connectFacesPart1(faces)
        data.robot.face = faces[FaceSide.TOP]
    }

    abstract fun breakIntoFaces(data: Data): Map<FaceSide, Face>

    abstract fun connectFacesPart1(faces: Map<FaceSide, Face>)

    abstract fun adjustPos(pos: Pos, face: Face): Pos
}