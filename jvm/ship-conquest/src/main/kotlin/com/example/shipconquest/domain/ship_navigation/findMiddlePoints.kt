package com.example.shipconquest.domain.ship_navigation

import com.example.shipconquest.domain.Position


/**
 * Define middle points in the path to prepare the usage of Bézier curves at equal distance
 * @param path the shortest path to be used
 * @param distance the desired distance between points
 * @return a list with the points of interest
 */
fun defineMiddlePoints(path: List<Position>, numPoints: Int): List<Position> {
    val extractedPoints = mutableListOf<Position>()
    val step = (path.size - 1).toDouble() / (numPoints - 1).toDouble()
    for (i in 0 until numPoints) {
        val index = (i * step).toInt()
        extractedPoints.add(path[index])
    }
    return extractedPoints
}
