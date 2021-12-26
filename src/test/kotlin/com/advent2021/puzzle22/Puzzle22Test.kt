package com.advent2021.puzzle22

import com.advent2021.puzzle19.Point3D
import com.advent2021.puzzle22.Puzzle22.Face.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

class Puzzle22Test {
    private val puz = Puzzle22()

    @Test
    fun testSimpleUnion() {
        val cube1 = Cube3D(10..20,30..50, 75..100)
        compareUnionSimple(cube1, Cube3D(-20..-10, 55..85, 200..230))
        compareUnionSimple(cube1, cube1.copy())
        compareUnionSimple(cube1, Cube3D(12..18, 30..40, 80..100))
        compareUnionSimple(Cube3D(12..18, 30..40, 80..100), cube1)
    }

    @Test
    fun testUnionAbutting() {
        val cube1 = Cube3D(2..5,8..11, 14..17)
        compareUnionSimple(cube1, Cube3D(cube1.xRange, cube1.yRange, 18..20))
        compareUnionSimple(cube1, Cube3D(cube1.xRange, 4..7, cube1.zRange))
        compareUnionSimple(cube1, Cube3D(6..10, cube1.yRange, cube1.zRange))
    }

    @Test
    fun testUnionOneDimension() {
        val cube1 = Cube3D(2..8, 8..23, 14..24)
        compareUnionSimple(cube1, Cube3D(4..6, 11..15, 12..22)) // front
        compareUnionSimple(cube1, Cube3D(4..6, 11..15, 20..26)) // back
        compareUnionSimple(cube1, Cube3D(4..6, 11..15, 10..26)) // front/back

        compareUnionSimple(cube1, Cube3D(-4..7, 11..15, 17..22)) // left
        compareUnionSimple(cube1, Cube3D(5..10, 11..15, 17..22)) // right
        compareUnionSimple(cube1, Cube3D(-2..11, 11..15, 17..22)) // left/right

        compareUnionSimple(cube1, Cube3D(4..6, 5..15, 12..22)) // top
        compareUnionSimple(cube1, Cube3D(4..6, 17..25, 17..22)) // bottom
        compareUnionSimple(cube1, Cube3D(4..6, 2..30, 17..22)) // top/bottom
    }

    @Test
    fun testSimpleVolume() {
        val cube1 = Cube3D(2..5,8..11, 14..17)
        assertThat(cube1.volume(), equalTo(BigInteger.valueOf(cube1.toSimple().volume().toLong())))
    }

    @Test
    fun testSimpleOverlap() {
        val cube1 = Cube3D(10..12,20..24, 100..103)
        compareUnionSimple(cube1, Cube3D(cube1.xRange, cube1.yRange, 102..105))
        compareUnionSimple(cube1, Cube3D(cube1.xRange, 18..21, cube1.zRange))
        compareUnionSimple(cube1, Cube3D(8..10, cube1.yRange, cube1.zRange))
    }

    @Test
    fun testYAndZOverlap() {
        val cube1 = Cube3D(10..20,30..50, 75..100)
        val other = Cube3D(cube1.xRange, 35..60, 50..85)
        compareUnionSimple(cube1, other)
    }

    @Test
    fun testOverlap() {
        assertThat(puz.overlap(0..5, 5..10), equalTo(true))
        assertThat(puz.overlap(5..10, 10..15), equalTo(true))
        assertThat(puz.overlap(5..10, 10..15), equalTo(true))

        assertThat(puz.overlap(0..3, 5..10), equalTo(false))
        assertThat(puz.overlap(5..10, 12..15), equalTo(false))
    }

    @Test
    fun testOverlapCubes() {
        val cube1 = Cube3D(0..5, 10..15, 25..35)
        val cube2 = Cube3D(5..8, 15..25, 35..50)
        val cube3 = Cube3D(-3..0, 6..10, 18..25)
        val cube4 = Cube3D(2..7, 13..17, 27..52)
        assertThat(puz.overlap(cube1, cube2), equalTo(true))
        assertThat(puz.overlap(cube1, cube3), equalTo(true))

        assertThat(puz.overlap(cube1, cube4), equalTo(true))
        assertThat(puz.overlap(cube3, cube4), equalTo(false))

        val cubeAbut = Cube3D(cube1.xRange, cube1.yRange, 20..25)
        assertThat(puz.overlap(cube1, cubeAbut), equalTo(true))
    }

    @Test
    fun testFindCommonRange() {
        assertThat(puz.findCommon(1..5, 6..10).isEmpty(), equalTo(true))
        assertThat(puz.findCommon(6..10, 1..5).isEmpty(), equalTo(true))
        assertThat(puz.findCommon(1..5, 2..4), equalTo(2..4))
        assertThat(puz.findCommon(2..4, 1..5), equalTo(2..4))
        assertThat(puz.findCommon(10..15, 12..18), equalTo(12..15))
        assertThat(puz.findCommon(12..18, 10..15), equalTo(12..15))
        assertThat(puz.findCommon(10..18, 12..15), equalTo(12..15))
        assertThat(puz.findCommon(12..15, 10..18), equalTo(12..15))
    }

    @Test
    fun testSubtractOneDim() {
        val baseSmall = Cube3D(0..4, 7..12, 14..20)
        compareSubtractSimple(baseSmall, Cube3D(baseSmall.xRange, baseSmall.yRange, 16..19))

        val base = Cube3D(0..10, 15..27, 32..46)
        compareSubtractSimple(base, Cube3D(12..15, 28..32, 48..54))

        compareSubtractSimple(Cube3D(12..15, 28..32, 48..54), base)

        compareSubtractSimple(base, Cube3D(0..10, 15..27, 30..34))
        compareSubtractSimple(base, Cube3D(0..10, 15..27, 32..34))
        compareSubtractSimple(base, Cube3D(0..10, 15..27, 34..36))
        compareSubtractSimple(base, Cube3D(0..10, 15..27, 44..46))
        compareSubtractSimple(base, Cube3D(0..10, 15..27, 44..48))

        compareSubtractSimple(base, Cube3D(-2..5, 15..27, 32..46))
        compareSubtractSimple(base, Cube3D(0..4, 15..27, 32..46))
        compareSubtractSimple(base, Cube3D(3..8, 15..27, 32..46))
        compareSubtractSimple(base, Cube3D(8..10, 15..27, 32..46))
        compareSubtractSimple(base, Cube3D(8..12, 15..27, 32..46))

        compareSubtractSimple(base, Cube3D(0..10, 13..24, 32..46))
        compareSubtractSimple(base, Cube3D(0..10, 15..19, 32..46))
        compareSubtractSimple(base, Cube3D(0..10, 19..24, 32..46))
        compareSubtractSimple(base, Cube3D(0..10, 23..27, 32..46))
        compareSubtractSimple(base, Cube3D(0..10, 25..30, 32..46))
    }

    @Test
    fun testSubtractTwoDim() {
        val base = Cube3D(0..6, 2..7, 10..13)
        compareSubtractSimple(base, Cube3D(0..2, 2..5, zRange = base.zRange))
        compareSubtractSimple(base, Cube3D(1..4, 2..5, zRange = base.zRange))
        compareSubtractSimple(base, Cube3D(1..4, 3..6, zRange = base.zRange))
    }

    @Test
    fun testSubtractProblem() {
        val baseTrivial = Cube3D(0..4, 2..5, 10..13)
        compareSubtractSimple(baseTrivial, Cube3D(2..9, 1..3, zRange = baseTrivial.zRange))

        val base = Cube3D(0..6, 2..7, 10..13)
        compareSubtractSimple(base, Cube3D(3..9, 1..5, zRange = base.zRange))
        compareSubtractSimple(base, Cube3D(2..8, 5..9, zRange = base.zRange))
    }

    @Test
    fun testSubtractOneOppositeFace() {
        val base = Cube3D(0..10, 2..8, 3..12)

        val leftThruExact = Cube3D(0..10, 4..7, 5..10)
        compareSubtract(base, leftThruExact, puz.subtractOneOrOppositeFaces(base, leftThruExact, LEFT))

        val leftThru = Cube3D(-3..13, 4..7, 5..10)
        compareSubtract(base, leftThru, puz.subtractOneOrOppositeFaces(base, leftThru, LEFT))

        val left = Cube3D(-3..3, 4..7, 5..10)
        compareSubtract(base, left, puz.subtractOneOrOppositeFaces(base, left, LEFT))

        val right = Cube3D(6..12, 4..7, 5..10)
        compareSubtract(base, right, puz.subtractOneOrOppositeFaces(base, right, RIGHT))

        val frontThruExact = Cube3D(3..8, 4..7, 3..12)
        compareSubtract(base, frontThruExact, puz.subtractOneOrOppositeFaces(base, frontThruExact, FRONT))

        val frontThru = Cube3D(3..8, 4..7, 3..10)
        compareSubtract(base, frontThru, puz.subtractOneOrOppositeFaces(base, frontThru, FRONT))

        val front = Cube3D(3..8, 4..7, -3..6)
        compareSubtract(base, front, puz.subtractOneOrOppositeFaces(base, front, FRONT))

        val back = Cube3D(3..8, 4..7, 5..13)
        compareSubtract(base, back, puz.subtractOneOrOppositeFaces(base, back, BACK))

        //val base = Cube3D(0..10, 2..8, 3..12)

        val topThruExact = Cube3D(3..7, 2..8, 5..10)
        compareSubtract(base, topThruExact, puz.subtractOneOrOppositeFaces(base, topThruExact, TOP))

        val topThru = Cube3D(3..7, -4..12, 5..10)
        compareSubtract(base, topThru, puz.subtractOneOrOppositeFaces(base, topThru, TOP))

        val top = Cube3D(3..7, 0..5, 5..10)
        compareSubtract(base, top, puz.subtractOneOrOppositeFaces(base, top, TOP))

        val bottom = Cube3D(3..7, 5..13, 5..10)
        compareSubtract(base, bottom, puz.subtractOneOrOppositeFaces(base, bottom, BOTTOM))
    }

    @Test
    fun testFourFaces() {
        val base = Cube3D(0..10, 2..8, 3..12)

        val sliceIntoLeftRight = Cube3D(-5..15, base.yRange, base.zRange)
        compareSubtract(base, sliceIntoLeftRight, puz.sliceIntoTwoCubes(base, sliceIntoLeftRight, FRONT))

        val sliceIntoTopBottom = Cube3D(base.xRange, 4..7, base.zRange)
        compareSubtract(base, sliceIntoTopBottom, puz.sliceIntoTwoCubes(base, sliceIntoTopBottom, LEFT))

        val sliceIntoFrontBack = Cube3D(base.xRange, base.yRange, 6..9)
        compareSubtract(base, sliceIntoFrontBack, puz.sliceIntoTwoCubes(base, sliceIntoFrontBack, TOP))
    }

    @Test
    fun testThreeFaceChunk() {
        val base = Cube3D(0..10, 2..8, 3..12)
        
        val topLeftFrontCorner = Cube3D(-3..3, 0..4, 1..5)
        compareSubtract(base, topLeftFrontCorner, puz.subtractThreeFaceChunk(base, topLeftFrontCorner, listOf(TOP, LEFT, FRONT)))

        val topLeftBackCorner = Cube3D(-3..3, 0..4, 8..14)
        compareSubtract(base, topLeftBackCorner, puz.subtractThreeFaceChunk(base, topLeftBackCorner, listOf(TOP, LEFT, BACK)))

        val topRightFrontCorner = Cube3D(5..15, 0..4, 1..5)
        compareSubtract(base, topRightFrontCorner, puz.subtractThreeFaceChunk(base, topRightFrontCorner, listOf(TOP, RIGHT, FRONT)))

        val topRightBackCorner = Cube3D(5..12, 0..4, 8..14)
        compareSubtract(base, topRightBackCorner, puz.subtractThreeFaceChunk(base, topRightBackCorner, listOf(TOP, RIGHT, BACK)))

        val bottomLeftFrontCorner = Cube3D(-3..3, 5..12, 1..5)
        compareSubtract(base, bottomLeftFrontCorner, puz.subtractThreeFaceChunk(base, bottomLeftFrontCorner, listOf(BOTTOM, LEFT, FRONT)))

        val bottomLeftBackCorner = Cube3D(-3..3, 3..13, 8..14)
        compareSubtract(base, bottomLeftBackCorner, puz.subtractThreeFaceChunk(base, bottomLeftBackCorner, listOf(BOTTOM, LEFT, BACK)))

        val bottomRightFrontCorner = Cube3D(5..15, 7..10, 1..5)
        compareSubtract(base, bottomRightFrontCorner, puz.subtractThreeFaceChunk(base, bottomRightFrontCorner, listOf(BOTTOM, RIGHT, FRONT)))

        val bottomRightBackCorner = Cube3D(5..12, 4..10, 8..14)
        compareSubtract(base, bottomRightBackCorner, puz.subtractThreeFaceChunk(base, bottomRightBackCorner, listOf(BOTTOM, RIGHT, BACK)))
    }

    @Test
    fun testChainSubtracts() {
        val base = Cube3D(0..50, 75..115, 200..240)
        var working = listOf(base)

        val cubes = listOf(
            Cube3D(-4..5, 50..70, 235..255),
            Cube3D(8..10, 40..80, 180..240)
        )
        cubes.forEach {
            working = puz.subtract(working, it)
        }
        val volume = cubes.totalVolume()
        val simpleVolume = cubes.fold(0) { acc, cube3D -> acc + cube3D.toSimple().volume() }
        assertThat(volume, equalTo(BigInteger.valueOf(simpleVolume.toLong())))
    }

    @Test
    fun testChainUnions() {
        val base = Cube3D(0..50, 75..115, 200..240)
        var working = listOf(base)

        val cubes = listOf(
            Cube3D(-4..5, 50..70, 235..255),
            Cube3D(8..10, 40..80, 180..240)
        )
        cubes.forEach {
            working = puz.union(working, it)
        }
        val volume = cubes.totalVolume()
        val simpleVolume = cubes.fold(0) { acc, cube3D -> acc + cube3D.toSimple().volume() }
        assertThat(volume, equalTo(BigInteger.valueOf(simpleVolume.toLong())))
    }

    @Test
    fun testAllSubtracts() {
        val xRange = 0..3
        val yRange = 5..8
        val zRange = 10..13
        val base = Cube3D(xRange, yRange, zRange)

        var totalTested = 0
        var totalError = 0
        for (xStart in xRange.first - 1..xRange.last+1) {
            for (xEnd in xStart..xRange.last+1) {
                for (yStart in yRange.first - 1..yRange.last+1) {
                    for (yEnd in yStart..yRange.last+1) {
                        for (zStart in zRange.first - 1..zRange.last+1) {
                            for (zEnd in zStart..zRange.last+1) {
                                totalTested++
                                if (!compareSubtractSimple(base, Cube3D(xStart..xEnd, yStart..yEnd, zStart..zEnd), false)) {
                                    ++totalError
                                }
                            }
                        }
                    }
                }
            }
        }

        println("Tested: $totalTested, errors=$totalError")
    }

    @Test
    fun testMoreProblems() {
        compareSubtractSimple(
            Cube3D(xRange=0..3, yRange=5..8, zRange=10..13),
            Cube3D(xRange=-1..0, yRange=6..8, zRange=11..11))

        compareSubtractSimple(
            Cube3D(xRange=0..3, yRange=5..8, zRange=10..13),
            Cube3D(xRange=-1..0, yRange=6..7, zRange=11..13))

        compareSubtractSimple(
            Cube3D(xRange=0..3, yRange=5..8, zRange=10..13),
            Cube3D(xRange=-1..0, yRange=6..7, zRange=9..10))

        compareSubtractSimple(
            Cube3D(xRange=0..3, yRange=5..8, zRange=10..13),
            Cube3D(xRange=-1..0, yRange=6..6, zRange=11..13))

        compareSubtractSimple(
            Cube3D(xRange=0..3, yRange=5..8, zRange=10..13),
            Cube3D(xRange=-1..0, yRange=6..6, zRange=9..11)
        ) //front


        compareSubtractSimple(
            Cube3D(xRange=0..3, yRange=5..8, zRange=10..13),
            Cube3D(xRange=-1..0, yRange=4..6, zRange=11..11)
        ) //top

        compareSubtractSimple(
            Cube3D(xRange=0..3, yRange=5..8, zRange=10..13),
            Cube3D(xRange=-1..0, yRange=4..5, zRange=11..11)
        )

        compareSubtractSimple(
            Cube3D(xRange=0..3, yRange=5..8, zRange=10..13),
            Cube3D(xRange=1..1, yRange=4..5, zRange=10..13)
        )

        compareSubtractSimple(
            Cube3D(xRange = 0..3, yRange = 5..8, zRange = 10..13),
            Cube3D(xRange = -1..0, yRange = 6..6, zRange = 10..13)
        ) //front

    }

    private fun compareUnionSimple(cube1: Cube3D, cube2: Cube3D): BigInteger {
        val cube3Ds: List<Cube3D> = puz.union(cube1, cube2)
        val cube1Simple = cube1.toSimple()
        val cube2Simple = cube2.toSimple()
        val simple = cube1Simple.union(cube2Simple).volume().toLong()
        assertThat(cube3Ds.totalVolume(), equalTo(BigInteger.valueOf(simple)))
        return BigInteger.valueOf(simple)
    }

    private fun compareSubtractSimple(cube1: Cube3D, cube2: Cube3D, assertIfNotSame: Boolean = true): Boolean {
        val cube3Ds: List<Cube3D> = puz.subtract(cube1, cube2)
        return compareSubtract(cube1, cube2, cube3Ds, assertIfNotSame)
    }

    private fun compareSubtract(
        cube1: Cube3D,
        cube2: Cube3D,
        cube3Ds: List<Cube3D>,
        assertIfNotSame: Boolean = true
    ): Boolean {
        val cube1Simple = cube1.toSimple()
        val cube2Simple = cube2.toSimple()
        val subtract = cube1Simple.subtract(cube2Simple)
        val simple = subtract.volume().toLong()
        val totalVolume = cube3Ds.totalVolume()
        if (BigInteger.valueOf(simple) != totalVolume) {
            println(
            """
        compareSubtractSimple(
            Cube3D($cube1),
            Cube3D($cube2)
        )
            """)

            cube3Ds.forEach { cube ->
                println("$cube")
                if (DEBUG) {
                    for (x in cube.xRange) {
                        for (y in cube.yRange) {
                            for (z in cube.zRange) {
                                if (!subtract.points.contains(Point3D(x, y, z))) {
                                    println("\t$x,$y,$z")
                                }
                            }
                        }
                    }
                }
            }
            if (DEBUG) {
                val pointsStillIn = subtract.points.filter { pt -> cube3Ds.none { it.contains(pt) } }
                pointsStillIn.forEach {
                    println("Missing $it.x,$it.y,$it.z")
                }
            }
        }
        if (assertIfNotSame) {
            assertThat(cube3Ds.totalVolume(), equalTo(BigInteger.valueOf(simple)))
        }
        return BigInteger.valueOf(simple) == totalVolume
    }

    companion object {
        const val DEBUG = false
    }
}