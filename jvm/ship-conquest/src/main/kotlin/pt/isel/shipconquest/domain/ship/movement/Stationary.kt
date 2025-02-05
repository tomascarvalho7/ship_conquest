package pt.isel.shipconquest.domain.ship.movement

import pt.isel.shipconquest.domain.space.Vector2
import java.time.Instant

/**
 * The [Stationary] class implement's the [Movement] interface, and represents
 * a stationary movement.
 * This movement is fixed at the given [coordinate].
 */
class Stationary(val coordinate: Vector2): Movement {
    override fun getCoordinateFromInstant(instant: Instant) = coordinate
}