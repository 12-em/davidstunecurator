package de.petakis.musicgraph

import de.petakis.musicgraph.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.PriorityQueue
import java.util.concurrent.ConcurrentHashMap

const val APP_NAME = "David's TuneCurator"
const val MAX_DEPTH = 10

class Main {
    private val graph: Graph<Song> = Graph()

    fun buildParallelKNearestGraph(songs: List<Song>, k: Int, diffThreshold: Float): Graph<Song> {
        val graph = Graph<Song>()
        val adjacencyMap = ConcurrentHashMap<Song, MutableMap<Song, Float>>()

        runBlocking {
            val jobs = songs.map { song1 ->
                launch(Dispatchers.Default) {
                    val neighbors = songs
                        .asSequence()
                        .filter { it != song1 }
                        .map { song2 -> song2 to computeEdgeWeight(song1, song2) }
                        .sortedBy { it.second }
                        // priority queue: only take the k nearest
                        .take(k)
                        .toList()

                    adjacencyMap[song1] = neighbors.toMap() as MutableMap<Song, Float>
                }
            }

            jobs.forEach { it.join() }
        }

        adjacencyMap.forEach { (song, neighbors) ->
            graph.addVertex(song)
            neighbors.forEach { (neighbor, weight) ->
                // filter out too high costs
                if (weight < diffThreshold)
                graph.addEdge(song, neighbor, weight)
            }
        }

        return graph
    }

    // FIXME some paths still return null even though they should be valid
    fun findPathByDijkstra(graph: Graph<Song>, start: Song, end: Song, minLength: Int): Pair<List<Song>, Float>? {
        val distances = mutableMapOf<Song, Float>().withDefault { Float.MAX_VALUE }
        distances[start] = 0f

        val pathLengths = mutableMapOf<Song, Int>().withDefault { 0 }
        pathLengths[start] = 1

        val priorityQueue = PriorityQueue<Triple<Song, Float, Int>>(compareBy { it.second} )
        priorityQueue.add(Triple(start, 0f, 1))

        val predecessors = mutableMapOf<Song, Song>()

        while (priorityQueue.isNotEmpty()) {
            val (currentSong, currentDist, currentLength) = priorityQueue.poll()

            if (currentSong == end && currentLength >= minLength) break

            val neighbors = graph.getNeighbors(currentSong)
            for((neighbor, weight) in neighbors) {
                val newDist = currentDist + weight
                val newLength = currentLength + 1

                if (newDist < distances.getValue(neighbor)) {
                    distances[neighbor] = newDist
                    pathLengths[neighbor] = newLength
                    predecessors[neighbor] = currentSong
                    priorityQueue.add(Triple(neighbor, newDist, newLength))
                }
            }
        }

        if(end !in distances || distances[end] == Float.MAX_VALUE || pathLengths[end]!! < minLength) {
            return null
        }

        val path = mutableListOf<Song>()
        var current: Song? = end
        while (current != null) {
            path.add(current)
            current = predecessors[current]
        }
        path.reverse()

        return path to distances[end]!!
    }

    /**
     * DOES NOT WORK, PLEASE FIX
     * Finds a playlist by constructing a curve in a 5-dimensional space of songs.
     * @param distThreshold The threshold for continuing curve subdivision. Paths with lengths below this won't be subdivided anymore as they're considered smooth enough.
     */
    fun findPathByCurve(
        songs: List<Song>,
        start: Song,
        end: Song,
        distThreshold: Float,
        visited: MutableSet<Song> = mutableSetOf(),
        depth: Int = 0
    ): List<Song> {
        if(depth >= 2 || start.getDistance(end) < distThreshold) {
            return listOf(start, end).filter { it !in visited }.also { visited.addAll(it) }
        }

        val midpoint = start.getMidpoint(end)

        var closest: Song? = null
        var minDist = Float.MAX_VALUE
        for (song in songs) {
            if (song == start || song == end || song == midpoint || song in visited) continue
            val dist = song.getDistance(midpoint)

            if(dist < minDist) {
                closest = song
                minDist = dist
            }
        }

        if (closest == null) return listOf(start, end).filter { it !in visited }.also { visited.addAll(it) }

        val a = findPathByCurve(songs, start, closest, distThreshold, visited, depth + 1)
        val b = findPathByCurve(songs, closest, end, distThreshold, visited, depth + 1)

        val alist = arrayListOf<Song>()
        alist.addAll(a.dropLast(1))
        alist.addAll(b)

        return alist
    }
}

fun main() {
    println("Hello World!")
    val main = Main()
    val graph = main.buildParallelKNearestGraph(songs, 5, 1f)
    graph.display()

    val playlist = main.findPathByDijkstra(graph, songs[9], songs[8], 4)
    println(playlist?.first)
    println(playlist?.second)
}