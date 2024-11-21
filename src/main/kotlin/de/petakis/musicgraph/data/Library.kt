package de.petakis.musicgraph.data

/**
 * Represents a collection of songs, for example an album or a playlist
 */
class Library(
    val name: String,
    val songs: List<Song>
) {
}