package pt.isel.shipconquest.repo.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pt.isel.shipconquest.domain.space.Vector2
import pt.isel.shipconquest.domain.world.islands.Island
import pt.isel.shipconquest.domain.world.islands.OwnedIsland
import pt.isel.shipconquest.repo.IslandRepository
import pt.isel.shipconquest.repo.jdbi.dbmodel.island.GenericIslandDBModel
import pt.isel.shipconquest.repo.jdbi.dbmodel.island.toIsland
import java.time.Instant

class IslandRepositoryJDBI(private val handle: Handle): IslandRepository {
    override val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun get(tag: String, uid: String, islandId: Int): Island? {
        logger.info("Getting island from db with tag = {} and islandId = {}", tag, islandId)

        return handle.createQuery(
            """
                SELECT i.islandId, i.tag, i.x, i.y, i.radius, o.incomePerHour, o.instant,
                o.uid, username
                FROM dbo.Island i
                LEFT JOIN dbo.OwnedIsland o ON i.islandId = o.islandId
                LEFT JOIN dbo.User ON o.uid = id
                WHERE i.tag = :tag AND i.islandId = :id
            """
        )
            .bind("tag", tag)
            .bind("id", islandId)
            .mapTo<GenericIslandDBModel>()
            .singleOrNull()
            ?.toIsland(uid)
    }

    override fun getAll(tag: String, uid: String): List<Island> {
        logger.info("Getting all island's from db with tag = {}", tag)

        return handle.createQuery(
            """
                SELECT i.islandId, i.tag, i.x, i.y, i.radius, o.incomePerHour, o.instant,
                o.uid, username
                FROM dbo.Island i
                LEFT JOIN dbo.OwnedIsland o ON i.islandId = o.islandId
                LEFT JOIN dbo.User ON o.uid = id
                WHERE i.tag = :tag
            """
        )
            .bind("tag", tag)
            .mapTo<GenericIslandDBModel>()
            .map { it.toIsland(uid) }
            .list()
    }

    override fun getVisitedIslands(tag: String, uid: String): List<Island> {
        logger.info("Getting visited islands by user with uid = {} on game with tag = {}", uid, tag)

        return handle.createQuery(
            """
                SELECT i.islandId, i.tag, i.x, i.y, i.radius, o.incomePerHour, o.instant,
                o.uid, username
                FROM dbo.Island i
                LEFT JOIN dbo.OwnedIsland o ON i.islandId = o.islandId
                LEFT JOIN dbo.User ON i.uid = id
                WHERE i.tag = :tag AND i.islandId IN (
                    SELECT e.islandId FROM dbo.IslandEvent e
                    INNER JOIN dbo.Ship S ON e.sid = S.shipId
                    WHERE e.tag = :tag AND S.uid = :uid
                )
            """
        )
            .bind("tag", tag)
            .bind("uid", uid)
            .mapTo<GenericIslandDBModel>()
            .map { it.toIsland(uid) }
            .list()
    }

    override fun getVisitedIslandsBeforeInstant(tag: String, uid: String, instant: Instant): List<Island> {
        logger.info("Getting visited islands by user with uid = {} on game with tag = {}", uid, tag)

        return handle.createQuery(
            """
                SELECT i.islandId, i.tag, i.x, i.y, i.radius, o.incomePerHour, o.instant,
                o.uid, username
                FROM dbo.Island i
                LEFT JOIN dbo.OwnedIsland o ON i.islandId = o.islandId
                LEFT JOIN dbo.User ON i.uid = id
                WHERE i.tag = :tag AND i.islandId IN (
                    SELECT e.islandId FROM dbo.IslandEvent e
                    INNER JOIN dbo.Ship S ON e.sid = S.shipId
                    WHERE e.tag = :tag AND S.uid = :uid AND e.instant <= :instant
                )
            """
        )
            .bind("tag", tag)
            .bind("uid", uid)
            .bind("instant", instant.epochSecond)
            .mapTo<GenericIslandDBModel>()
            .map { it.toIsland(uid) }
            .list()
    }

    override fun getUnvisitedIslands(tag: String, uid: String): List<Island> {
        logger.info("Getting unvisited islands by user with uid = {} on game with tag = {}", uid, tag)

        return handle.createQuery(
            """
                SELECT i.islandId, i.tag, i.x, i.y, i.radius, o.incomePerHour, o.instant,
                o.uid, username
                FROM dbo.Island i
                LEFT JOIN dbo.OwnedIsland o ON i.islandId = o.islandId
                LEFT JOIN dbo.User ON i.uid = id
                WHERE i.tag = :tag AND i.islandId NOT IN (
                    SELECT e.islandId FROM dbo.IslandEvent e
                    INNER JOIN dbo.Ship S ON e.sid = S.shipId
                    WHERE e.tag = :tag AND S.uid = :uid
                )
            """
        )
            .bind("tag", tag)
            .bind("uid", uid)
            .mapTo<GenericIslandDBModel>()
            .map { it.toIsland(uid) }
            .list()
    }

    override fun create(tag: String, origin: Vector2, radius: Int) {
        logger.info("Creating island on game with tag = {}", tag)

        handle.createUpdate(
            """
                insert into dbo.Island(tag, x, y, radius) values(:tag, :x, :y, :radius)
            """
        )
            .bind("tag", tag)
            .bind("x", origin.x)
            .bind("y", origin.y)
            .bind("radius", radius)
            .execute()
    }

    override fun updateOwnedIsland(tag: String, island: OwnedIsland) {
        logger.info("Island with id = {} on game = {} is now owned by = {}",
            island.islandId, tag, island.uid)

        handle.createUpdate(
            """
                UPDATE dbo.OwnedIsland SET 
                incomePerHour = :incomePerHour
                instant = :instant
                uid = :uid
                WHERE tag = :tag AND islandId = :id
            """
        )
            .bind("tag", tag)
            .bind("id", island.islandId)
            .bind("incomePerHour", island.incomePerHour)
            .bind("instant", island.conquestDate.epochSecond)
            .bind("uid", island.uid)
            .execute()
    }

    override fun wildToOwnedIsland(tag: String, island: OwnedIsland) {
        logger.info("Wild island with id = {} on game = {} is now owned by = {}", island.islandId, tag, island.uid)

        handle.createUpdate(
            """
               DELETE FROM dbo.Island WHERE tag = :tag AND islandId = :id
            """
        )
            .bind("tag", tag)
            .bind("id", island.islandId)
            .execute()

        handle.createUpdate(
            """
                INSERT INTO dbo.OwnedIsland(islandId, tag, x, y, radius, incomePerHour, instant, uid) 
                values(:islandId, :tag, :x, :y, :radius, :incomePerHour, :instant, :uid)
            """
        )
            .bind("islandId", island.islandId)
            .bind("tag", tag)
            .bind("x", island.coordinate.x)
            .bind("y", island.coordinate.y)
            .bind("radius", island.radius)
            .bind("incomePerHour", island.incomePerHour)
            .bind("instant", island.conquestDate.epochSecond)
            .bind("uid", island.uid)
            .execute()
    }
}