package de.petakis.musicgraph.data

/**
 * Represents a weighted graph.
 */
class Graph<T> {
    private val adjacencyList: MutableMap<T, MutableMap<T, Float>> = mutableMapOf()


    // Add a vertex to the graph
    fun addVertex(vertex: T) {
        adjacencyList.putIfAbsent(vertex, mutableMapOf())
    }

    fun addEdge(from: T, to: T, weight: Float) {
        adjacencyList.computeIfAbsent(from) { mutableMapOf() }[to] = weight
        adjacencyList.computeIfAbsent(to) { mutableMapOf() }[from] = weight
    }

    // Remove an edge between two vertices (bidirectional by default)
    fun removeEdge(from: T, to: T) {
        adjacencyList[from]?.remove(to)
        adjacencyList[to]?.remove(from)
    }

    // Remove a vertex and all its edges
    fun removeVertex(vertex: T) {
        adjacencyList.remove(vertex)?.keys?.forEach { neighbor ->
            adjacencyList[neighbor]?.remove(vertex)
        }
    }

    // Get neighbors and weights of a vertex
    fun getNeighbors(vertex: T): Map<T, Float> {
        return adjacencyList[vertex] ?: emptyMap()
    }

    // Check if an edge exists between two vertices
    fun hasEdge(from: T, to: T): Boolean {
        return adjacencyList[from]?.contains(to) == true
    }

    // Get the weight of an edge (returns null if no edge exists)
    fun getEdgeWeight(from: T, to: T): Float {
        return adjacencyList[from]?.get(to) ?: 0f
    }

    // Display the graph
    fun display() {
        adjacencyList.forEach { (vertex, neighbors) ->
            val edges = neighbors.entries.joinToString { "${it.key}(${it.value})" }
            println("$vertex -> $edges")
        }
    }
}