package de.petakis.musicgraph.data

import kotlin.math.abs


const val WEIGHT_ENERGY = 1f
const val WEIGHT_TEMPO = 0.4f
const val WEIGHT_MOOD =  1f
const val WEIGHT_INSTRUMENTATION = 0.6f
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

fun Song.getMidpoint(other: Song): Song {
    val t = (tempo + other.tempo) / 2
    val e = (energy + other.energy) / 2
    val m = (mood + other.mood) / 2
    val i = (instrumentation + other.instrumentation) / 2
    val d = (danceability + other.danceability) / 2

    return Song("", "", t, e, m, i, d)
}

fun Song.getDistance(other: Song): Float {
    val tempoDiff = abs(tempo - other.tempo)
    val energyDiff = abs(energy - other.energy)
    val moodDiff = abs(mood - other.mood)
    val instrumentationDiff = abs(mood - other.mood)
    val danceabilityDiff = abs(danceability - other.danceability)

    return tempoDiff + energyDiff + moodDiff + instrumentationDiff + danceabilityDiff
}

// TODO change to modular defs
val songs = listOf(
    Song("Jesus Walks", "Kanye West", 0.6f, 0.7f, 0.4f, 0.6f, 0.3f),
    Song("Claire de Lune", "Debussy", 0.4f, 0.5f, 0.2f, 0.1f, 0f),
    Song("Untitled 1", "Unknown Artist", 0.7f, 0.5f, 0.6f, 0.4f, 0.6f),
    Song("Something About Us", "Daft Punk", 0.4f, 0.1f, 0.2f, 0.3f, 0f),
    Song("Gypsy Woman", "Crystal Waters", 0.7f, 0.6f, 0.7f, 0.3f, 0.8f),
    Song("Pump Up The Jam", "Technotronic", 0.8f, 0.9f, 0.9f, 0.3f, 0.9f),
    Song("Street Scene (4 Shazz)", "St Germain", 0.6f, 0.4f, 0.6f, 0.4f, 0.6f),
    Song("I'm Not In Love", "10cc", 0.3f, 0.2f, 0.2f, 0.4f, 0f),
    Song("The Weekend", "Michael Gray", 0.7f, 0.85f, 0.7f, 0.3f, 0.8f),
    Song("Lady", "Modjo", 0.7f, 0.9f, 0.9f, 0.3f, 0.9f),
    Song("Good Life", "Inner City", 0.7f, 0.95f, 0.9f, 0.35f, 0.85f)
)