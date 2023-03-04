package com.example.shipconquest.domain.world

import com.example.shipconquest.domain.Position
import java.util.HashMap

class HeightMapBuilder(val size: Int) {
    private val hashMap = HashMap<Position, Int>()

    fun add(x: Int, y: Int, height: Int) {
        hashMap[Position(x = x, y = y)] = height
    }

    fun build(builderFn: (builder: HeightMapBuilder) -> Unit): HeightMap {
        builderFn(this) // build mutable instance of height map
        return HeightMap(data = hashMap.toMap(), size = size) // build immutable instance of heightmap from mutable instance
    }
}