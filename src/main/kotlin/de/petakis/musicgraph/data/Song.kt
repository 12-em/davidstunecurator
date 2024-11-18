package de.petakis.musicgraph.data


const val WEIGHT_ENERGY = 1f
const val WEIGHT_TEMPO = 0.7f
const val WEIGHT_MOOD =  1f
const val WEIGHT_INSTRUMENTATION = 0.3f
const val WEIGHT_DANCEABILITY = 0.7f


data class Song(
    val title: String,
    val artist: String,
    val tempo: Float,
    val energy: Float,
    /**
     * Mood of the song. Goes from -1 (negative mood) to 1 (positive mood)
     */
    val mood: Float,
    /**
     * Instrumentation of the song. From 0 (sparse) to 1 (rich)
     */
    val instrumentation: Float,
    val danceability: Float
)

// TODO change to modular defs
val songs = listOf(
    Song("Jesus Walks", "Kanye West", 0.6f, 0.7f, 0.4f, 0.6f, 0.3f),
    Song("Claire de Lune", "Debussy", 0.4f, 0.5f, 0.2f, 0.1f, 0f),
    Song("Untitled 1", "Unknown Artist", 0.7f, 0.5f, 0.6f, 0.4f, 0.6f),
    Song("Something About Us", "Daft Punk", 0.4f, 0.2f, 0.2f, 0.3f, 0f),
    Song("Gypsy Woman", "Crystal Waters", 0.7f, 0.6f, 0.7f, 0.3f, 0.8f),
    Song("Pump Up The Jam", "Technotronic", 0.8f, 0.9f, 0.9f, 0.3f, 0.9f)
)