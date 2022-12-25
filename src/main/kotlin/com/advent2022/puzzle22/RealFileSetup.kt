package com.advent2022.puzzle22

class RealFileSetup: FileSetup() {
    override fun breakIntoFaces(data: Data): Map<FaceSide, Face> {
        val top = Face(FaceSide.TOP, 1)
        val right = Face(FaceSide.RIGHT, 2)
        val height = 50
        val width = 50
        for (row in 1..height) {
            val line = data.getLine(row).str.trim()
            top.addLine(line.substring(0, width))
            right.addLine(line.substring(width))
        }

        val front = Face(FaceSide.FRONT, 3)
        var startRow = top.height + 1
        var endRow = startRow + top.height - 1
        for (row in startRow..endRow) {
            val line = data.getLine(row).str.trim()
            front.addLine(line)
        }

        val left = Face(FaceSide.LEFT, 4)
        val bottom = Face(FaceSide.BOTTOM, 5)
        startRow += top.height
        endRow += top.height
        for (row in startRow..endRow) {
            val line = data.getLine(row).str.trim()
            left.addLine(line.substring(0, top.width))
            bottom.addLine(line.substring(top.width, top.width * 2))
        }

        val back = Face(FaceSide.BACK, 6)
        startRow += top.height
        endRow += top.height
        for (row in startRow..endRow) {
            val line = data.getLine(row).str.trim()
            back.addLine(line)
        }

        return listOf(top, bottom, left, right, front, back).associateBy { it.side }
    }

    override fun connectFacesPart1(faces: Map<FaceSide, Face>) {
        connect("Tl->Rr", faces)
        connect("Tr->Rl", faces)
        connect("Tu->Bd", faces)
        connect("Td->Fu", faces)

        connect("Ru->Rd", faces)

        connect("Fl->Fr", faces)
        connect("Fd->Bu", faces)

        connect("Bl->Lr", faces)
        connect("Br->Ll", faces)

        connect("Ll->Br", faces)
        connect("Lr->Bl", faces)
        connect("Lu->Kd", faces)
        connect("Ld->Ku", faces)

        connect("Kl->Kr", faces)
        connect("Ku->Ld", faces)
        connect("Kd->Lu", faces)
    }

    override fun connectFacesPart2(faces: Map<FaceSide, Face>) {
        // connect all of the top edges (symmetric connections)
        connect("Td->Fu", faces)
        connect("Tu->Kl", faces)    // ?    u -> l *
        connect("Tr->Rl", faces)
        connect("Tl->Ll", faces)    // ?    l -> l *

        // only have to connect three of front edges because top already connected
        connect("Fd->Bu", faces)
        connect("Fr->Rd", faces)    // ?    r -> d *
        connect("Fl->Lu", faces)

        // only have to connect three back edges because top already connected
        connect("Ku->Ld", faces)
        connect("Kr->Bd", faces)
        connect("Kd->Ru", faces)

        // Only one connection now
        connect("Rr->Br", faces)   // ?     r -> r

        // Only one connection now
        connect("Bl->Lr", faces)

        faces.values.forEach { face ->
            Dir.values().forEach { dir ->
                if (!face.connections.containsKey(dir)) {
                    println("Missing $dir for ${face.side}")
                }
            }
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