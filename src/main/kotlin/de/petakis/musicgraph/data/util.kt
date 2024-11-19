package de.petakis.musicgraph.data

import kotlin.math.abs

fun computeEdgeWeight(a: Song, b: Song): Float {
    var tempoDiff = abs(a.tempo - b.tempo)
    var energyDiff = abs(a.energy - b.energy)
    var moodDiff = abs(a.mood - b.mood)
    var instrumentationDiff = abs(a.mood - b.mood)
    var danceabilityDiff = abs(a.danceability - b.danceability)

    // TODO scaling function subject to change
    // squaring: penalize stark differences in one factor
    tempoDiff *= WEIGHT_TEMPO * tempoDiff
    energyDiff *= WEIGHT_ENERGY * energyDiff
    moodDiff *= WEIGHT_MOOD * moodDiff
    instrumentationDiff *= WEIGHT_INSTRUMENTATION * instrumentationDiff
    danceabilityDiff *= WEIGHT_DANCEABILITY * danceabilityDiff

    // also subject to change
    return tempoDiff + energyDiff + moodDiff + instrumentationDiff + danceabilityDiff
}