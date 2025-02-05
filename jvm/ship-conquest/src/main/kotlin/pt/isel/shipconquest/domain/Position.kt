package pt.isel.shipconquest.domain

import pt.isel.shipconquest.domain.space.Vector2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * The Position class represents a space not limited by a grid with
 * the [x] and [y] axis.
 */
data class Position(val x: Double, val y: Double) {
    operator fun plus(position: Position) =
        Position(x = this.x + position.x, y = this.y + position.y)

    operator fun minus(position: Position) =
        Position(x = this.x - position.x, y = this.y - position.y)

    operator fun times(scalar: Double) =
        Position(x = this.x * scalar, y = this.y * scalar)
}

fun Position.toVector2() =
    Vector2(x.toInt(), y.toInt())

fun Position.distanceTo(other: Position) =
    sqrt((x - other.x).pow(2) + (y - other.y).pow(2))
