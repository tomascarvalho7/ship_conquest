package pt.isel.shipconquest.repo.jdbi.dbmodel.island

import pt.isel.shipconquest.domain.space.Vector2
import pt.isel.shipconquest.domain.user.statistics.IslandIncome
import pt.isel.shipconquest.domain.world.islands.OwnedIsland
import pt.isel.shipconquest.domain.world.islands.OwnershipDetails
import java.time.Instant

data class OwnedIslandDBModel(
    val tag: String,
    val islandId: Int,
    val x: Int,
    val y: Int,
    val radius: Int,
    val incomePerHour: Int,
    val instant: Instant,
    val uid: String,
    val username: String
)

fun OwnedIslandDBModel.toOwnedIsland(userId: String) =
    OwnedIsland(
        islandId = islandId,
        coordinate = Vector2(x = x, y = y),
        radius = radius,
        incomePerHour = incomePerHour,
        conquestDate = instant,
        uid = uid,
        ownershipDetails = OwnershipDetails(owned = userId == uid, username = username)
    )

fun OwnedIslandDBModel.toIslandIncome() =
    IslandIncome(
        incomePerHour = incomePerHour,
        conquestDate = instant
    )