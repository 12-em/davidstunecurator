package de.petakis.musicgraph

import de.petakis.musicgraph.data.Library
import de.petakis.musicgraph.data.Song

class LibraryParser {
    fun parse(contents: List<String>): Library {
        val songs: MutableList<Song> = mutableListOf()
        var title = "unnamed"
        var atSongList = false

        contents.forEach {
            if (it.trim().startsWith("#")) return@forEach
            if (it.startsWith("title") && !atSongList) {
                title = it.split('=')[1]
            }
            if (it.startsWith("songs=[")) {
                atSongList = true
                // start of song list
            }
            if (it.startsWith("]")) {
                // end of song list
                atSongList = false
            }

            if (atSongList && !it.startsWith("songs=[") && !it.startsWith("]") && it.trim().isNotEmpty()) {
                // parse song
                println("AT: $it")
                songs.add(parseSong(it))
            }
        }

        return Library(title, songs)
    }

    private fun parseSong(content: String): Song {
        println(content)
        var (title, artist, metadata) = content.split('-')

        title = title.trim()
        artist = artist.trim()
        metadata = metadata.trim()

        val metaArr = metadata.split(',').map { it.toFloat() }

        val song = Song(title, artist, metaArr[0], metaArr[1], metaArr[2], metaArr[3], metaArr[4])
        println(song)
        return song
    }
}