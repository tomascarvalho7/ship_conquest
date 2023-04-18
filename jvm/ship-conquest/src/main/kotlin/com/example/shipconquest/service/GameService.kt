package com.example.shipconquest.service

import com.example.shipconquest.domain.*
import com.example.shipconquest.domain.path_finding.calculateEuclideanDistance
import com.example.shipconquest.domain.ship_navigation.CubicBezier
import com.example.shipconquest.domain.world.pulse
import com.example.shipconquest.left
import com.example.shipconquest.repo.TransactionManager
import com.example.shipconquest.right
import com.example.shipconquest.service.result.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

//take these out of here
const val chunkSize = 20.0
const val viewDistance = 15
@Service
class GameService(
    override val transactionManager: TransactionManager
) : ServiceModule {
    override val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun getChunks(tag: String, shipId: String, googleId: String): GetChunksResult {
        return transactionManager.run { transaction ->
            val staticPosition = transaction.gameRepo.getShipStaticPosition(tag, shipId, googleId)
            val position = if(staticPosition != null) {
                staticPosition.toPosition()
            } else {
                val path = transaction.gameRepo.getShipPath(tag, shipId, googleId)
                    ?: return@run left(GetChunksError.ShipPositionNotFound)
                val pos = path.getPositionFromTime()
                if(pos.toCoord2D() == path.landmarks.last().p3) {
                    transaction.gameRepo.deleteShipEntry(tag, shipId, googleId) // vou mudar para um update depois
                    transaction.gameRepo.createShipStaticPosition(tag, shipId, googleId, pos.toCoord2D())
                }
                pos
            }.toCoord2D()

            val visitedPoints = transaction.gameRepo.getVisitedPoints(tag, googleId)
            if (visitedPoints == null) {
                transaction.gameRepo.createVisitedPoint(tag, googleId, position)
            }
            if(visitedPoints != null && visitedPoints.all { point -> calculateEuclideanDistance(position, point) >= chunkSize }) {
                transaction.gameRepo.addVisitedPoint(tag, googleId, position)
            }
            val game = transaction.gameRepo.get(tag = tag)

            if (game != null) right(game.map.pulse(origin = position, radius = viewDistance))
            else left(GetChunksError.GameNotFound)
        }
    }

    fun getMinimap(tag: String, uid: String): GetMinimapResult {
        return transactionManager.run { transaction ->
            val visitedPoints = transaction.gameRepo.getVisitedPoints(tag, uid)
            val game = transaction.gameRepo.get(tag) ?: return@run left(GetMinimapError.GameNotFound)

            val pointList = visitedPoints?.flatMap { point ->
                val pulseResult = game.map.pulse(origin = point, radius = viewDistance)
                pulseResult + Vector3(point.x, point.y, 0)
            }?.distinct()
                ?: emptyList()
            if (pointList.isEmpty()) return@run left(GetMinimapError.NoTrackedRecord)
            else right(pointList)
        }
    }

    fun navigate(tag: String, uid: String, shipId: String, points: List<Coord2D>): NavigationResult {
        val startTime = LocalDateTime.now()
        var distance = 0.0;
        for(i in 0 until points.size - 1) {
            val a = points[i];
            val b = points[i + 1];
            distance += calculateEuclideanDistance(a, b)
        }
        val duration = Duration.ofSeconds((distance * 10).roundToLong())
        val landmarks = buildBeziers(points)

        val durationMillis = duration.toMillis()

        val dateFormat = SimpleDateFormat("mm:ss.SSS")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val formattedDuration = dateFormat.format(Date(durationMillis))

        if(!validateNavigationPath(landmarks)) {
            return left(NavigationError.InvalidNavigationPath)
        }

        return transactionManager.run { transaction ->
            transaction.gameRepo.deleteShipEntry(tag, shipId, uid)
            transaction.gameRepo.createShipPath(tag, shipId, uid, landmarks, startTime, duration)
            right(ShipPathTime(startTime.toString(), formattedDuration))
        }
    }

    fun printMap(tag: String) {
        return transactionManager.run { transaction ->
            val game = transaction.gameRepo.get(tag = tag) ?: return@run

            for (y in 0 until game.map.size) {
                for (x in 0 until game.map.size) {
                    val pos = Coord2D(x = x, y = y)
                    val tile = game.map.data[pos]?.div(10)

                    if (tile == null)
                        print("---")
                    else
                        print(tile.toString().padStart(2, '0') + '-')
                }
                println()
            }
        }
    }
}

fun validateNavigationPath(landmarks: List<CubicBezier>): Boolean {
    //validate path
    return true
}

fun buildBeziers(points: List<Coord2D>): List<CubicBezier> {
    if (points.size % 4 != 0) return emptyList()

    return List(points.size / 4) { index ->
        CubicBezier(
            p0 = points[index * 4],
            p1 = points[(index * 4) + 1],
            p2 = points[(index * 4) + 2],
            p3 = points[(index * 4) + 3]
        )
    }
}