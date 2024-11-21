package de.petakis.musicgraph

import de.petakis.musicgraph.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.PriorityQueue
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*

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
    /**
     * PLEASE DO NOT USE (DOESN'T WORK AND NOT REQUIRED
     */
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

            if (currentSong == end && currentLength <= minLength) break

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
}

fun getDepthForSongCount(count: Int) = log2(count.toFloat()).toInt()

fun main() {
    println("Hello World!")

    val curator = Curator()

    curator.loadLibraryFile("PATH_TO_LIBRARY")

    val songs = curator.getAllSongsFromLibraries()
    println(songs)

    // tests
    val playlist = curator.findPathByCurve(songs, songs[0], songs.last(), 0.01f, mutableSetOf(), getDepthForSongCount(3))
    println(playlist)
}