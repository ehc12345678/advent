package com.advent2021.puzzle22

import com.advent2021.puzzle19.Point3D
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

class Puzzle22Test {
    val puz = Puzzle22()

    @Test
    fun testSplitZ() {
        val cube1 = Cube3D(10..20,30..50, 75..100)
        assertThat(cube1.volume(), equalTo(BigInteger.valueOf(11L * 21L * 26L)))
        
        puz.splitZ(cube1, 20..50).run {
            assertThat(totalVolume(), equalTo(cube1.volume()))
        }

        puz.splitZ(cube1, 25..80).run {
            assertThat(totalVolume(), equalTo(cube1.volume()))
        }

        puz.splitZ(cube1, 85..125).run {
            assertThat(totalVolume(), equalTo(cube1.volume()))
        }
    }

    @Test
    fun testSplitY() {
        val cube1 = Cube3D(10..20,30..50, 75..100)
        puz.splitY(cube1,45..85).run {
            assertThat(totalVolume(), equalTo(cube1.volume()))
        }
        puz.splitY(cube1, 20..85).run {
            assertThat(totalVolume(), equalTo(cube1.volume()))
        }
        puz.splitY(cube1, 20..40).run {
            assertThat(totalVolume(), equalTo(cube1.volume()))
        }
    }

    @Test
    fun testSplitX() {
        val cube1 = Cube3D(10..20,30..50, 75..100)
        puz.splitX(cube1, 0..15).run {
            assertThat(totalVolume(), equalTo(cube1.volume()))
        }
        puz.splitX(cube1, 15..30).run {
            assertThat(totalVolume(), equalTo(cube1.volume()))
        }
        puz.splitX(cube1,-5..40).run {
            assertThat(totalVolume(), equalTo(cube1.volume()))
        }
    }

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
        compareSubtract(base, leftThruExact, puz.subtractOneOrOppositeFaces(base, leftThruExact, Puzzle22.Face.LEFT))

        val leftThru = Cube3D(-3..13, 4..7, 5..10)
        compareSubtract(base, leftThru, puz.subtractOneOrOppositeFaces(base, leftThru, Puzzle22.Face.LEFT))

        val left = Cube3D(-3..3, 4..7, 5..10)
        compareSubtract(base, left, puz.subtractOneOrOppositeFaces(base, left, Puzzle22.Face.LEFT))

        val right = Cube3D(6..12, 4..7, 5..10)
        compareSubtract(base, right, puz.subtractOneOrOppositeFaces(base, right, Puzzle22.Face.RIGHT))

        val frontThruExact = Cube3D(3..8, 4..7, 3..12)
        compareSubtract(base, frontThruExact, puz.subtractOneOrOppositeFaces(base, frontThruExact, Puzzle22.Face.FRONT))

        val frontThru = Cube3D(3..8, 4..7, 3..10)
        compareSubtract(base, frontThru, puz.subtractOneOrOppositeFaces(base, frontThru, Puzzle22.Face.FRONT))

        val front = Cube3D(3..8, 4..7, -3..6)
        compareSubtract(base, front, puz.subtractOneOrOppositeFaces(base, front, Puzzle22.Face.FRONT))

        val back = Cube3D(3..8, 4..7, 5..13)
        compareSubtract(base, back, puz.subtractOneOrOppositeFaces(base, back, Puzzle22.Face.BACK))

        //val base = Cube3D(0..10, 2..8, 3..12)

        val topThruExact = Cube3D(3..7, 2..8, 5..10)
        compareSubtract(base, topThruExact, puz.subtractOneOrOppositeFaces(base, topThruExact, Puzzle22.Face.TOP))

        val topThru = Cube3D(3..7, -4..12, 5..10)
        compareSubtract(base, topThru, puz.subtractOneOrOppositeFaces(base, topThru, Puzzle22.Face.TOP))

        val top = Cube3D(3..7, 0..5, 5..10)
        compareSubtract(base, top, puz.subtractOneOrOppositeFaces(base, top, Puzzle22.Face.TOP))

        val bottom = Cube3D(3..7, 5..13, 5..10)
        compareSubtract(base, bottom, puz.subtractOneOrOppositeFaces(base, bottom, Puzzle22.Face.BOTTOM))
    }

    private fun compareUnionSimple(cube1: Cube3D, cube2: Cube3D): BigInteger {
        val cube3Ds: List<Cube3D> = puz.union(cube1, cube2)
        val cube1Simple = cube1.toSimple()
        val cube2Simple = cube2.toSimple()
        val simple = cube1Simple.union(cube2Simple).volume().toLong()
        assertThat(cube3Ds.totalVolume(), equalTo(BigInteger.valueOf(simple)))
        return BigInteger.valueOf(simple)
    }

    private fun compareSubtractSimple(cube1: Cube3D, cube2: Cube3D): BigInteger {
        val cube3Ds: List<Cube3D> = puz.subtract(cube1, cube2)
        return compareSubtract(cube1, cube2, cube3Ds)
    }

    private fun compareSubtract(
        cube1: Cube3D,
        cube2: Cube3D,
        cube3Ds: List<Cube3D>
    ): BigInteger {
        val cube1Simple = cube1.toSimple()
        val cube2Simple = cube2.toSimple()
        val subtract = cube1Simple.subtract(cube2Simple)
        val simple = subtract.volume().toLong()
        val totalVolume = cube3Ds.totalVolume()
        if (BigInteger.valueOf(simple) != totalVolume) {
            cube3Ds.forEach() { cube ->
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
                val pointsStillIn = subtract.points.filter { pt -> cube3Ds.none() { it.contains(pt) } }
                pointsStillIn.forEach {
                    println("Missing $it.x,$it.y,$it.z")
                }
            }
        }
        assertThat(cube3Ds.totalVolume(), equalTo(BigInteger.valueOf(simple)))
        return BigInteger.valueOf(simple)
    }


    private fun List<Cube3D>.totalVolume() =
        fold(BigInteger.ZERO) { acc, cube3D -> acc.add(cube3D.volume()) }

    private fun HashSet<Cube3D>.totalVolume() = toList().totalVolume()

    private fun Cube3D.toSimple() = Cube(xRange, yRange, zRange)

    companion object {
        const val DEBUG = true
    }
}