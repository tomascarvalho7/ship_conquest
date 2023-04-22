package com.example.shipconquest.service.result

import com.example.shipconquest.Either
import com.example.shipconquest.controller.model.output.ShipPathOutputModel
import com.example.shipconquest.domain.Vector2
import com.example.shipconquest.domain.ShipPathTime
import com.example.shipconquest.domain.Vector3
import com.example.shipconquest.domain.world.Horizon

sealed class GetChunksError {
    object GameNotFound: GetChunksError()
    object ShipPositionNotFound: GetChunksError()
}

typealias GetChunksResult = Either<GetChunksError, Horizon>

sealed class GetMinimapError {
    object GameNotFound: GetMinimapError()
    object NoTrackedRecord: GetMinimapError()
}

typealias GetMinimapResult = Either<GetMinimapError, List<Vector3>>

sealed class NavigationError {
    object InvalidNavigationPath: NavigationError()
}

typealias NavigationResult = Either<NavigationError, ShipPathTime>

sealed class GetShipPositionError {
    object ShipNotFound: GetShipPositionError()
}

typealias GetShipPositionResult = Either<GetShipPositionError, Vector2>

sealed class GetShipPathError {
    object ShipNotFound: GetShipPathError()
}

typealias GetShipPathResult = Either<GetShipPathError, ShipPathOutputModel>