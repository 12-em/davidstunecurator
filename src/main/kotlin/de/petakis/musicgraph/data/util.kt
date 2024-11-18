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
    tempoDiff *= WEIGHT_TEMPO
    energyDiff *= WEIGHT_ENERGY
    moodDiff *= WEIGHT_MOOD
    instrumentationDiff *= WEIGHT_INSTRUMENTATION
    danceabilityDiff *= WEIGHT_DANCEABILITY

    // also subject to change
    return tempoDiff + energyDiff + moodDiff + instrumentationDiff + danceabilityDiff
}