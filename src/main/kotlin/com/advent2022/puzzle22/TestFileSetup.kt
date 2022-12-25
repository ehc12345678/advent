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
        connect("Tl->Tr", faces)
        connect("Tu->Bd", faces)
        connect("Td->Fu", faces)

        connect("Bl->Rr", faces)
        connect("Br->Rl", faces)
        connect("Bu->Fd", faces)
        connect("Bd->Tu", faces)

        connect("Kl->Fr", faces)
        connect("Kr->Ll", faces)
        connect("Ku->Kd", faces)

        connect("Fl->Lr", faces)
        connect("Fd->Bu", faces)

        connect("Lr->Fl", faces)
        connect("Lu->Ld", faces)

        connect("Ru->Rd", faces)
    }

    override fun connectFacesPart2(faces: Map<FaceSide, Face>) {
        // connect all of the top edges (symmetric connections)
        connect("Td->Fu", faces)
        connect("Tu->Ku", faces)
        connect("Tr->Rr", faces)
        connect("Tl->Lu", faces)

        // only have to connect three of front edges because top already connected
        connect("Fd->Bu", faces)
        connect("Fr->Ru", faces)
        connect("Fl->Lr", faces)

        // only have to connect three back edges because top already connected
        connect("Kr->Ll", faces)
        connect("Kl->Rd", faces)
        connect("Kd->Bd", faces)

        // Only one connection now
        connect("Rl->Br", faces)

        // Only one connection now
        connect("Bl->Ld", faces)
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