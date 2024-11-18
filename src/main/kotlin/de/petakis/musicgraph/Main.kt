package de.petakis.musicgraph

import de.petakis.musicgraph.data.Graph
import de.petakis.musicgraph.data.Song
import de.petakis.musicgraph.data.computeEdgeWeight
import de.petakis.musicgraph.data.songs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

const val APP_NAME = "David's TuneCurator"

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
}

fun main() {
    println("Hello World!")

    val main = Main()
    val graph = main.buildParallelKNearestGraph(songs, 5, 2f)
    graph.display()
}