package com.example.shipconquest.domain.world

import com.example.shipconquest.domain.Factor
import com.example.shipconquest.domain.space.Vector2
import com.example.shipconquest.domain.generators.Falloff
import com.example.shipconquest.domain.generators.SimplexNoise
import com.example.shipconquest.domain.generators.get
import kotlin.math.max
import kotlin.math.roundToInt


const val frequency = .1
const val seaLevel = 2

/**
 * World Generator class is a tool to generate:
 * - islands: generating random points in a 2D grid;
 * - terrain: using the [SimplexNoise] generator;
 * - world: using both these generator it generates the entire
 * world. Building a terrain for each random point created.
 */
class WorldGenerator(
    private val worldSize: Int
) {
    val islandSize = 25
    private val falloffGrid = Falloff.generateFalloffMap(islandSize)

    fun generateIslandCoordinates(
        islandDensity: Factor, // 0 a 100
    ): List<Vector2> {
        if (worldSize == 0) return emptyList()

        val numIslands = 4//calculateNumberOfIslands(islandDensity)
        val gridSize = worldSize / max(1, numIslands)
        val offset = gridSize / 2

        return buildList {
            for (y in 0 until numIslands) {
                for (x in 0 until numIslands) {
                    val position = Vector2(x = offset + x * gridSize, y = offset + y * gridSize)
                    val safeOffset = max(offset - islandSize, islandSize)
                    val randomOffset = Vector2(
                        x = (-safeOffset..safeOffset).random(),
                        y = (-safeOffset..safeOffset).random()
                    )
                    add(position + randomOffset)
                }
            }
        }
    }

    fun generate(origins: List<Vector2>) =
        HeightMapBuilder(worldSize).build {
            for (origin in origins) {
                generateIslandTerrain(origin = origin, builder = it)
            }
        }

    private fun calculateNumberOfIslands(density: Factor): Int {
        val numIslands = (worldSize * (density.value / 100.0)) / (islandSize * 2)

        return numIslands.toInt()
    }

    private fun generateIslandTerrain(origin: Vector2, builder: HeightMapBuilder) {
        val noiseMap = SimplexNoise.generateSimplexNoise(size = islandSize, offset = origin, frequency = frequency)
        val offset = origin - Vector2(islandSize / 2, islandSize / 2)
        // apply falloff, scale noise
        for (y in 0 until islandSize) {
            for (x in 0 until islandSize) {
                val noiseValue = noiseMap.get(x = x, y = y)
                val falloffValue = 1 - falloffGrid.get(x = x, y = y)
                val value = remapNoiseValue(value = noiseValue * falloffValue)

                if (value > seaLevel)
                    builder.add(x = offset.x + x, y = offset.y + y, height = value)
            }
        }
    }

    // in value is between -1 and 1 (float)
    // out value is between -100 and 100 (int)
    private fun remapNoiseValue(value: Double) = (value * 100).roundToInt()
}
