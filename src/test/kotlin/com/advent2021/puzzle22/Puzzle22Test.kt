package com.advent2021.puzzle22

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
        
        puz.splitZ(cube1, Cube3D(cube1.xRange, cube1.yRange, 20..50)).run {
            assertThat(totalVolume(), equalTo(BigInteger.valueOf(11L * 21L * 26L + 11L * 21L * 31L)))
        }

        puz.splitZ(cube1, Cube3D(cube1.xRange, cube1.yRange, 25..80)).run {
            assertThat(totalVolume(), equalTo(Cube3D(cube1.xRange, cube1.yRange, 25..100).volume()))
        }

        puz.splitZ(cube1, Cube3D(cube1.xRange, cube1.yRange, 85..125)).run {
            assertThat(totalVolume(), equalTo(Cube3D(cube1.xRange, cube1.yRange, 75..125).volume()))
        }
    }

    @Test
    fun testSplitY() {
        val cube1 = Cube3D(10..20,30..50, 75..100)
        puz.splitY(cube1, Cube3D(cube1.xRange, 45..85, cube1.zRange)).run {
            assertThat(totalVolume(), equalTo(Cube3D(cube1.xRange, 30..85, cube1.zRange).volume()))
        }
        puz.splitY(cube1, Cube3D(cube1.xRange, 20..85, cube1.zRange)).run {
            assertThat(totalVolume(), equalTo(Cube3D(cube1.xRange, 20..85, cube1.zRange).volume()))
        }
        puz.splitY(cube1, Cube3D(cube1.xRange, 20..40, cube1.zRange)).run {
            assertThat(totalVolume(), equalTo(Cube3D(cube1.xRange, 20..50, cube1.zRange).volume()))
        }
    }

    @Test
    fun testSplitX() {
        val cube1 = Cube3D(10..20,30..50, 75..100)
        puz.splitX(cube1, Cube3D(0..15, cube1.yRange, cube1.zRange)).run {
            assertThat(totalVolume(), equalTo(Cube3D(0..20, cube1.yRange, cube1.zRange).volume()))
        }
        puz.splitX(cube1, Cube3D(15..30, cube1.yRange, cube1.zRange)).run {
            assertThat(totalVolume(), equalTo(Cube3D(10..30, cube1.yRange, cube1.zRange).volume()))
        }
        puz.splitX(cube1, Cube3D(-5..40, cube1.yRange, cube1.zRange)).run {
            assertThat(totalVolume(), equalTo(Cube3D(-5..40, cube1.yRange, cube1.zRange).volume()))
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

    private fun compareUnionSimple(cube1: Cube3D, cube2: Cube3D): BigInteger {
        val cube3Ds: List<Cube3D> = puz.union(cube1, cube2)
        val cube1Simple = cube1.toSimple()
        val cube2Simple = cube2.toSimple()
        val simple = cube1Simple.union(cube2Simple).volume().toLong()
        assertThat(cube3Ds.totalVolume(), equalTo(BigInteger.valueOf(simple)))
        return BigInteger.valueOf(simple)
    }

    private fun List<Cube3D>.totalVolume() =
        fold(BigInteger.ZERO) { acc, cube3D -> acc.add(cube3D.volume()) }

    private fun HashSet<Cube3D>.totalVolume() = toList().totalVolume()

    private fun Cube3D.toSimple() = Cube(xRange, yRange, zRange)
}