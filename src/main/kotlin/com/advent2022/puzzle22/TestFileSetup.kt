package com.advent2022.puzzle22

class TestFileSetup : FileSetup() {
    override fun breakIntoFaces(data: Data): Map<FaceSide, Face> {
        val top = Face(FaceSide.TOP, 1)
        val first = data.getLine(1).str.trim()
        for (row in data.rowRange) {
            val line = data.getLine(row)
            val trimmed = line.str.trim()
            if (trimmed.length != first.length) {
                break
            }
            top.addLine(trimmed)
        }

        val back = Face(FaceSide.BACK,2)
        val left = Face(FaceSide.LEFT,3)
        val front = Face(FaceSide.FRONT,4)
        var startRow = top.height + 1
        var endRow = startRow + top.height - 1
        for (row in startRow..endRow) {
            val line = data.getLine(row)
            back.addLine(line.str.substring(0, top.width))
            left.addLine(line.str.substring(top.width, top.width * 2))
            front.addLine(line.str.substring(top.width * 2))
        }

        startRow += top.height
        endRow += top.height
        val right = Face(FaceSide.RIGHT,5)
        val bottom = Face(FaceSide.BOTTOM,6)
        for (row in startRow..endRow) {
            val lineStr = data.getLine(row).str.trim()
            bottom.addLine(lineStr.substring(0, top.width))
            right.addLine(lineStr.substring(top.width, top.width * 2))
        }

        return listOf(top, bottom, left, right, front, back).associateBy { it.side }
    }

    override fun connectFacesPart1(faces: Map<FaceSide, Face>) {
        val top = faces[FaceSide.TOP]!!
        val bottom = faces[FaceSide.BOTTOM]!!
        val left = faces[FaceSide.LEFT]!!
        val right = faces[FaceSide.RIGHT]!!
        val front = faces[FaceSide.FRONT]!!
        val back = faces[FaceSide.BACK]!!

        connectFaceToFace(top, Dir.LEFT, top, Dir.RIGHT)
        connectFaceToFace(top, Dir.UP, bottom, Dir.DOWN)
        connectFaceToFace(top, Dir.DOWN, front, Dir.UP)
        top.run {
            connections[Dir.LEFT] = FaceConnection(this, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(this, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(bottom, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(front, wrapToTopLine)
        }
        bottom.run {
            connections[Dir.LEFT] = FaceConnection(right, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(right, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(front, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(top, wrapToTopLine)
        }
        back.run {
            connections[Dir.LEFT] = FaceConnection(front, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(left, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(this, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(this, wrapToTopLine)
        }
        front.run {
            connections[Dir.LEFT] = FaceConnection(left, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(back, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(top, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(bottom, wrapToTopLine)
        }
        left.run {
            connections[Dir.LEFT] = FaceConnection(back, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(front, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(this, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(this, wrapToTopLine)
        }
        right.run {
            connections[Dir.LEFT] = FaceConnection(bottom, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(bottom, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(this, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(this, wrapToTopLine)
        }
    }

    override fun connectFacesPart2(faces: Map<FaceSide, Face>) {
        val wrapToBeginLine = { pos: Pos, face: Face -> Pos(pos.row, face.colRange.first) }
        val wrapToEndLine = { pos: Pos, face: Face -> Pos(pos.row, face.colRange.last) }
        val wrapToTopLine = { pos: Pos, face: Face -> Pos(face.rowRange.first, pos.col) }
        val wrapToBottomLine = { pos: Pos, face: Face -> Pos(face.rowRange.last, pos.col) }
        val wrapFlipRowCol = { pos: Pos, _: Face -> Pos(pos.col, pos.row) }

        val top = faces[FaceSide.TOP]!!
        val bottom = faces[FaceSide.BOTTOM]!!
        val left = faces[FaceSide.LEFT]!!
        val right = faces[FaceSide.RIGHT]!!
        val front = faces[FaceSide.FRONT]!!
        val back = faces[FaceSide.BACK]!!

        top.run {
            connections[Dir.LEFT] = FaceConnection(left, wrapFlipRowCol, returnThis(Dir.DOWN))
            connections[Dir.RIGHT] = FaceConnection(right, wrapFlipRowCol, returnThis(Dir.LEFT))
            connections[Dir.UP] = FaceConnection(back, wrapFlipRowCol, returnThis(Dir.DOWN))
            connections[Dir.DOWN] = FaceConnection(front, wrapToTopLine)
        }
        right.run {
            connections[Dir.LEFT] = FaceConnection(bottom, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(top, wrapFlipRowCol, returnThis(Dir.LEFT))
            connections[Dir.UP] = FaceConnection(front, wrapToBottomLine, returnThis(Dir.LEFT))
            connections[Dir.DOWN] = FaceConnection(back, wrapFlipRowCol, returnThis(Dir.LEFT))
        }
        front.run {
            connections[Dir.LEFT] = FaceConnection(left, wrapFlipRowCol, returnThis(Dir.DOWN))
            connections[Dir.RIGHT] = FaceConnection(right, wrapFlipRowCol, returnThis(Dir.UP))
            connections[Dir.UP] = FaceConnection(top, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(bottom, wrapToTopLine)
        }
        bottom.run {
            connections[Dir.LEFT] = FaceConnection(left, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(right, wrapFlipRowCol, returnThis(Dir.LEFT))
            connections[Dir.UP] = FaceConnection(front, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(back, wrapFlipRowCol, returnThis(Dir.LEFT))
        }
        left.run {
            connections[Dir.LEFT] = FaceConnection(top, wrapFlipRowCol, returnThis(Dir.RIGHT))
            connections[Dir.RIGHT] = FaceConnection(bottom, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(front, wrapFlipRowCol, returnThis(Dir.RIGHT))
            connections[Dir.DOWN] = FaceConnection(back, wrapToTopLine)
        }
        back.run {
            connections[Dir.LEFT] = FaceConnection(top, wrapFlipRowCol, returnThis(Dir.DOWN))
            connections[Dir.RIGHT] = FaceConnection(bottom, wrapFlipRowCol, returnThis(Dir.UP))
            connections[Dir.UP] = FaceConnection(left, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(right, wrapToTopLine)
        }
    }

    override fun adjustPos(pos: Pos, face: Face): Pos {
        return pos + when (face.side) {
            FaceSide.TOP -> Pos(0,0)
            FaceSide.BACK -> Pos(face.height, 0)
            FaceSide.LEFT -> Pos(face.height, face.width)
            FaceSide.FRONT -> Pos(face.height, face.width * 2)
            FaceSide.BOTTOM -> Pos(face.height * 2, face.width * 2)
            FaceSide.RIGHT -> Pos(face.height * 2, face.width * 3)
        }
    }
}