package de.petakis.musicgraph

import de.petakis.musicgraph.data.Library
import de.petakis.musicgraph.data.Song
import de.petakis.musicgraph.data.getDistance
import de.petakis.musicgraph.data.getMidpoint
import java.io.File

class Curator {
    private var libraries: MutableList<Library> = mutableListOf()

    fun getLibraries(): List<Library> {
        return libraries
    }

    fun getAllSongsFromLibraries(): List<Song> = libraries.flatMap { it.songs }

    val parser = LibraryParser()


    fun loadLibraryFile(path: String): Boolean {
        val file = File(path)
        val lines = file.readLines()

        if (lines.isEmpty()) return false
        else {
            libraries.add(parser.parse(lines))
            println("done")
            return true
        }
    }
    /**
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
        visited.apply { add(start); add(end) }

        if(depth <= 0 || start.getDistance(end) < distThreshold) {
            return listOf(start, end)
        }

        val midpoint = start.getMidpoint(end)

        var closest: Song? = null
        var minDist = Float.MAX_VALUE
        for (song in songs) {
            if (song == start || song == end || song == midpoint || song in visited) continue
            if (song.artist == start.artist || song.artist == end.artist) continue
            val dist = song.getDistance(midpoint)

            if(dist < minDist) {
                closest = song
                minDist = dist
            }
        }

        if (closest == null) return listOf(start, end)

        visited.add(closest)

        val a = findPathByCurve(songs, start, closest, distThreshold, visited, depth-1)
        val b = findPathByCurve(songs, closest, end, distThreshold, visited, depth-1)

        return a.dropLast(1) + b
    }
}