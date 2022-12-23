package com.advent2022.puzzle23

class RealFileSetup: FileSetup() {
    override fun breakIntoFaces(data: Data): Map<FaceSide, Face> {
        val top = Face(FaceSide.TOP)
        val right = Face(FaceSide.RIGHT)
        val height = 50
        val width = 50
        for (row in 1..height) {
            val line = data.getLine(row).str.trim()
            top.addLine(line.substring(0, width))
            right.addLine(line.substring(width))
        }

        val front = Face(FaceSide.FRONT)
        var startRow = top.height + 1
        var endRow = startRow + top.height - 1
        for (row in startRow..endRow) {
            val line = data.getLine(row).str.trim()
            front.addLine(line)
        }

        val left = Face(FaceSide.LEFT)
        val bottom = Face(FaceSide.BOTTOM)
        startRow += top.height
        endRow += top.height
        for (row in startRow..endRow) {
            val line = data.getLine(row).str.trim()
            left.addLine(line.substring(0, top.width))
            bottom.addLine(line.substring(top.width, top.width * 2))
        }

        val back = Face(FaceSide.BACK)
        startRow += top.height
        endRow += top.height
        for (row in startRow..endRow) {
            val line = data.getLine(row).str.trim()
            back.addLine(line)
        }

        return listOf(top, bottom, left, right, front, back).associateBy { it.side }
    }

    override fun connectFacesPart1(faces: Map<FaceSide, Face>) {
        val wrapToBeginLine = { pos: Pos, face: Face -> Pos(pos.row, face.colRange.first) }
        val wrapToEndLine = { pos: Pos, face: Face -> Pos(pos.row, face.colRange.last) }
        val wrapToTopLine = { pos: Pos, face: Face -> Pos(face.rowRange.first, pos.col) }
        val wrapToBottomLine = { pos: Pos, face: Face -> Pos(face.rowRange.last, pos.col) }

        val top = faces[FaceSide.TOP]!!
        val bottom = faces[FaceSide.BOTTOM]!!
        val left = faces[FaceSide.LEFT]!!
        val right = faces[FaceSide.RIGHT]!!
        val front = faces[FaceSide.FRONT]!!
        val back = faces[FaceSide.BACK]!!

        top.run {
            connections[Dir.LEFT] = FaceConnection(right, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(right, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(bottom, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(front, wrapToTopLine)
        }
        right.run {
            connections[Dir.LEFT] = FaceConnection(top, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(top, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(this, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(this, wrapToTopLine)
        }
        front.run {
            connections[Dir.LEFT] = FaceConnection(this, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(this, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(top, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(bottom, wrapToTopLine)
        }
        bottom.run {
            connections[Dir.LEFT] = FaceConnection(left, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(left, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(front, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(top, wrapToTopLine)
        }
        left.run {
            connections[Dir.LEFT] = FaceConnection(bottom, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(bottom, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(back, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(back, wrapToTopLine)
        }
        back.run {
            connections[Dir.LEFT] = FaceConnection(this, wrapToEndLine)
            connections[Dir.RIGHT] = FaceConnection(this, wrapToBeginLine)
            connections[Dir.UP] = FaceConnection(left, wrapToBottomLine)
            connections[Dir.DOWN] = FaceConnection(left, wrapToTopLine)
        }
    }

    override fun adjustPos(pos: Pos, face: Face): Pos {
        return pos + when (face.side) {
            FaceSide.TOP -> Pos(0,face.width)
            FaceSide.RIGHT -> Pos(0, face.width * 2)
            FaceSide.FRONT -> Pos(face.height, face.width)
            FaceSide.BOTTOM -> Pos(face.height * 2, face.width)
            FaceSide.LEFT -> Pos(face.height * 2, 0)
            FaceSide.BACK -> Pos(face.height * 3, 0)
        }
    }


}