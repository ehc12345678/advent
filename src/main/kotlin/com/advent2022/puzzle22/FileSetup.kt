package com.advent2022.puzzle22

abstract class FileSetup() {
    val wrapToBeginLine = { pos: Pos, face: Face -> Pos(pos.row, face.colRange.first) }
    val wrapToEndLine = { pos: Pos, face: Face -> Pos(pos.row, face.colRange.last) }
    val wrapToTopLine = { pos: Pos, face: Face -> Pos(face.rowRange.first, pos.col) }
    val wrapToBottomLine = { pos: Pos, face: Face -> Pos(face.rowRange.last, pos.col) }
    val flipRowCol = { pos: Pos, _: Face -> Pos(pos.col, pos.row) }
    val rowStayColWrap = { pos: Pos, face: Face -> Pos(pos.row, face.width - pos.col + 1) }
    val colStayRowFlip = { pos: Pos, face: Face -> Pos(face.height - pos.row + 1, pos.col) }
    val colToEndWrapRow = { pos: Pos, face: Face -> Pos(face.height - pos.col + 1, face.colRange.last) }
    val colToBeginWrapRow = { pos: Pos, face: Face -> Pos(face.height - pos.col + 1, face.colRange.first) }
    val rowToBeginWrapCol = { pos: Pos, face: Face -> Pos(face.rowRange.first, face.width - pos.row + 1) }
    val rowToEndWrapCol = { pos: Pos, face: Face -> Pos(face.rowRange.last, face.width - pos.row + 1) }

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
                    Dir.UP -> {     // check
                        face1.addConnection(dir1, FaceConnection(face2, colStayRowFlip, returnThis(Dir.DOWN)))
                        face2.addConnection(dir2, FaceConnection(face1, colStayRowFlip, returnThis(Dir.DOWN)))
                    }
                    Dir.DOWN -> {   // check
                        face1.addConnection(dir1, FaceConnection(face2, wrapToBottomLine))
                        face2.addConnection(dir2, FaceConnection(face1, wrapToTopLine))
                    }
                    Dir.LEFT -> {   // check
                        face1.addConnection(dir1, FaceConnection(face2, flipRowCol, returnThis(Dir.RIGHT)))
                        face2.addConnection(dir2, FaceConnection(face1, flipRowCol, returnThis(Dir.DOWN)))
                    }
                    Dir.RIGHT -> {  // check
                        face1.addConnection(dir1, FaceConnection(face2, colToEndWrapRow, returnThis(Dir.LEFT)))
                        face2.addConnection(dir2, FaceConnection(face1, rowToBeginWrapCol, returnThis(Dir.DOWN)))
                    }
                }
            }
            Dir.DOWN -> {
                when (dir2) {
                    Dir.UP -> connectFaceToFace(face2, dir2, face1, dir1)
                    Dir.DOWN -> { // check
                        face1.addConnection(dir1, FaceConnection(face2, rowStayColWrap, returnThis(Dir.UP)))
                        face2.addConnection(dir2, FaceConnection(face1, rowStayColWrap, returnThis(Dir.UP)))
                    }
                    Dir.LEFT -> { // check
                        face1.addConnection(dir1, FaceConnection(face2, colToBeginWrapRow, returnThis(Dir.RIGHT)))
                        face2.addConnection(dir2, FaceConnection(face1, rowToEndWrapCol, returnThis(Dir.UP)))
                    }
                    Dir.RIGHT -> { //
                        face1.addConnection(dir1, FaceConnection(face2, flipRowCol, returnThis(Dir.LEFT)))
                        face2.addConnection(dir2, FaceConnection(face1, flipRowCol, returnThis(Dir.UP)))
                    }
                }
            }
            Dir.LEFT -> {
                when (dir2) {
                    Dir.UP -> connectFaceToFace(face2, dir2, face1, dir1)
                    Dir.DOWN -> connectFaceToFace(face2, dir2, face1, dir1)
                    Dir.LEFT -> { //
                        face1.addConnection(dir1, FaceConnection(face2, colStayRowFlip, returnThis(Dir.RIGHT)))
                        face2.addConnection(dir2, FaceConnection(face1, colStayRowFlip, returnThis(Dir.RIGHT)))
                    }
                    Dir.RIGHT -> { // check
                        face1.addConnection(dir1, FaceConnection(face2, wrapToEndLine))
                        face2.addConnection(dir2, FaceConnection(face1, wrapToBeginLine))
                    }
                }
            }
            Dir.RIGHT -> {
                when (dir2) {
                    Dir.UP -> connectFaceToFace(face2, dir2, face1, dir1)
                    Dir.DOWN -> connectFaceToFace(face2, dir2, face1, dir1)
                    Dir.LEFT -> connectFaceToFace(face2, dir2, face1, dir1)
                    Dir.RIGHT -> { // check
                        face1.addConnection(dir1, FaceConnection(face2, colStayRowFlip, returnThis(Dir.LEFT)))
                        face2.addConnection(dir2, FaceConnection(face1, colStayRowFlip, returnThis(Dir.LEFT)))
                    }
                }
            }
        }
    }

    // T=Top, B=Bottom, L=Left, R=Right, F=Front, K=bacK
    // d=down, u=up, l=left, r=right
    fun connect(str: String, facesMap: Map<FaceSide, Face>) {
        val map = mapOf('T' to facesMap[FaceSide.TOP]!!, 'B' to facesMap[FaceSide.BOTTOM]!!,
            'L' to facesMap[FaceSide.LEFT]!!, 'R' to facesMap[FaceSide.RIGHT]!!,
            'F' to facesMap[FaceSide.FRONT]!!, 'K' to facesMap[FaceSide.BACK]!!).toMap()
        val dirs = mapOf('d' to Dir.DOWN, 'u' to Dir.UP, 'l' to Dir.LEFT, 'r' to Dir.RIGHT)
        val face1 = map[str[0]]!!
        val dir1 = dirs[str[1]]!!
        val face2 = map[str[4]]!!
        val dir2 = dirs[str[5]]!!
        connectFaceToFace(face1, dir1, face2, dir2)
    }

}