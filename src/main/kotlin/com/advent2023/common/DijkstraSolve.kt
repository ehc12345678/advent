//package com.advent2023.common
//
//import java.util.*
//
//abstract class DijkstraSolve<NodeType> {
//    // return true if we have reached the end
//    abstract fun atEndNode(node: NodeType): Boolean
//
//    // return the next set of nodes to visit
//    abstract fun neighbors(node: NodeType): Iterable<NodeType>
//
//    // return the cost of a node (use negative for when you want the cost to come out the highest)
//    abstract fun cost(node: NodeType, nextNode: NodeType): Long
//
//    // heuristic for breaking ties or boosting a score
//    open fun heuristic(node: NodeType): Long = 0L
//
//    // find the shortest path using the above functions
//    fun findShortestPathByPredicate(startNode: NodeType): GraphSearchResult<NodeType> {
//        val toVisit = PriorityQueue(listOf(ScoredNode(startNode, 0, heuristic(startNode))))
//        var endNode: NodeType? = null
//        val seenPoints: MutableMap<NodeType, SeenNode<NodeType>> = mutableMapOf(startNode to SeenNode(0, null))
//
//        while (endNode == null) {
//            if (toVisit.isEmpty()) {
//                return GraphSearchResult(startNode, null, seenPoints)
//            }
//
//            val (currentNode, currentScore) = toVisit.remove()
//            endNode = if (atEndNode(currentNode)) currentNode else null
//
//            val nextPoints = neighbors(currentNode)
//                .filter { it !in seenPoints }
//                .map { next -> ScoredNode(next, currentScore + cost(currentNode, next), heuristic(next)) }
//
//            toVisit.addAll(nextPoints)
//            seenPoints.putAll(nextPoints.associate { it.node to SeenNode(it.score, currentNode) })
//        }
//
//        return GraphSearchResult(startNode, endNode, seenPoints)
//    }
//}
//
//data class SeenNode<NodeType>(val cost: Long, val prev: NodeType?)
//
//data class ScoredNode<NodeType>(val node: NodeType, val score: Long, val heuristic: Long)
//    : Comparable<ScoredNode<NodeType>> {
//    override fun compareTo(other: ScoredNode<NodeType>) = (score + heuristic).compareTo(other.score + other.heuristic)
//}
//
//class GraphSearchResult<NodeType>(val seen: Set<NodeType>) {
//}