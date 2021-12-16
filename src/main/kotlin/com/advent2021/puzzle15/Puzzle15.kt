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

    override fun computeSolution(data: Data): Solution = computeImpl(data, data.rows(), data.cols())
    override fun computeSolution2(data: Data): Solution2 = computeImpl(data, data.rows() * 5, data.cols() * 5)

    fun calcWeight(pt: Point, data: Data): Int {
        val row = pt.row % data.rows()
        val col = pt.col % data.cols()
        return data.value(row, col)!!.num + (pt.row / data.rows()) + (pt.col / data.cols())
    }

    private fun computeImpl(data: Data, rows: Int, cols: Int): Int {
        val graph = buildInitialGraph(data, rows, cols)

        val start = Point(0, 0)
        val end = Point(rows - 1, cols - 1)
        val shortPathTree = dijkstra(graph, start)
        val path = shortestPath(shortPathTree, start, end)
        return path.map { calcWeight(it, data) }.sumOf { it } - calcWeight(start, data)
    }

    private fun buildInitialGraph(data: Data, rows: Int, cols: Int): Graph<Point> {
        val graph = Graph<Point>()

        // build a dijkstra weighted graph
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val pt = Point(r, c)
                graph.vertices.add(pt)

                val neighbors = HashSet<Point>().also {
                    if (r < rows - 1) {
                        it.add(Point(r + 1, c))
                    }
                    if (c < cols - 1) {
                        it.add(Point(r, c + 1))
                    }
                }
                graph.edges[pt] = neighbors
                for (neighbor in neighbors) {
                    graph.weights[Pair(pt, neighbor)] = calcWeight(neighbor, data)
                }
            }
        }
        return graph
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
}

