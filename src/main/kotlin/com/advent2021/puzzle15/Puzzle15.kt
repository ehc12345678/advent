package com.advent2021.puzzle15

import com.advent2021.base.Base
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet

data class Point(val row: Int, val col: Int)
data class Square(val point: Point, var num: Int)

typealias Line = ArrayList<Square>
class Data {
    val grid: ArrayList<Line> = ArrayList()
    fun value(pt: Point) = value(pt.row, pt.col)
    fun value(r: Int, c: Int): Square? = if (r in grid.indices && c in grid[r].indices) grid[r][c] else null
    fun neighbors(pt: Point): Set<Square> {
        val r = pt.row
        val c = pt.col
        return setOfNotNull(
            value(r - 1, c),
            value(r, c - 1), value(r, c + 1),
            value(r + 1, c)
        )
    }
    fun rows() = grid.size
    fun cols() = grid[0].size
    val endPoint: Point
        get() = Point(rows() - 1, cols() - 1)
}
data class Path(
    val squares: LinkedHashSet<Square>,
    var isDefinitelyShortest: Boolean = false
) {
    fun score() = if (squares.isEmpty()) {
        Integer.MAX_VALUE
    } else {
        squares.sumOf { it.num }
    }

    val endOfPath: Square
        get() = squares.last()

    override fun toString(): String {
        return squares.joinToString("->") { "${it.point.row},${it.point.col}" }
    }
}
typealias Solution = Int
typealias Solution2 = Int

val NO_PATH = Path(LinkedHashSet())
typealias Working = HashMap<Square, Path>

fun main() {
    try {
        val puz = Puzzle15()
        val solution1 = puz.solvePuzzle("inputs.txt", Data())
        println("Solution1: $solution1")

        val solution2 = puz.solvePuzzle2("inputs.txt", Data())
        println("Solution2: $solution2")
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

class Puzzle15 : Base<Data, Solution?, Solution2?>() {
    override fun parseLine(line: String, data: Data) {
        val gridLine = Line()
        var col = 0
        for (element in line) {
            gridLine.add(
                Square(
                    Point(data.rows(), col++),
                    element - '0'
                )
            )
        }
        data.grid.add(gridLine)
    }

    override fun computeSolution(data: Data): Solution {
        val graph = Graph<Square>()

        // build a dijkstra weighted graph
        for (line in data.grid) {
            for (sq in line) {
                graph.vertices.add(sq)

                val neighbors = data.neighbors(sq.point)
                graph.edges[sq] = HashSet(neighbors.filter { !graph.vertices.contains(it) })
                for (neighbor in neighbors) {
                    if (!graph.vertices.contains(neighbor)) {
                        graph.weights[Pair(sq, neighbor)] = neighbor.num
                    }
                }
            }
        }

        val start = data.value(0, 0)!!
        val end = data.value(data.endPoint)!!
        val shortPathTree = dijkstra(graph, start)
        val path = shortestPath(shortPathTree, start, end)
        return path.sumOf { it.num } - path.first().num
    }

    class Graph<T> {
        val vertices: HashSet<T> = HashSet()
        val edges: HashMap<T, HashSet<T>> = HashMap()
        val weights: HashMap<Pair<T, T>, Int> = HashMap()
    }

    fun <T> dijkstra(graph: Graph<T>, start: T): Map<T, T?> {
        val S: MutableSet<T> = mutableSetOf() // a subset of vertices, for which we know the true distance

        val delta = graph.vertices.map { it to Int.MAX_VALUE }.toMap().toMutableMap()
        delta[start] = 0

        val previous: MutableMap<T, T?> = graph.vertices.map { it to null }.toMap().toMutableMap()

        while (S != graph.vertices) {
            val v: T = delta
                .filter { !S.contains(it.key) }
                .minByOrNull { it.value }!!
                .key

            graph.edges.getValue(v).minus(S).forEach { neighbor ->
                val newPath = delta.getValue(v) + graph.weights.getValue(Pair(v, neighbor))

                if (newPath < delta.getValue(neighbor)) {
                    delta[neighbor] = newPath
                    previous[neighbor] = v
                }
            }

            S.add(v)
        }

        return previous.toMap()
    }

    fun <T> shortestPath(shortestPathTree: Map<T, T?>, start: T, end: T): List<T> {
        fun pathTo(start: T, end: T): List<T> {
            if (shortestPathTree[end] == null) return listOf(end)
            return listOf(pathTo(start, shortestPathTree[end]!!), listOf(end)).flatten()
        }
        return pathTo(start, end)
    }

    override fun computeSolution2(data: Data): Solution2 {
        return 0
    }
}

