package pt.isel.shipconquest.controller.model.output.lobby

import pt.isel.shipconquest.domain.lobby.Lobby


data class LobbyOutputModel(
    val tag: String,
    val name: String,
    val uid: String,
    val username: String,
    val creationTime: Long
)

fun Lobby.toLobbyOutputModel() =
    LobbyOutputModel(tag = tag, name = name, uid = uid, username = username, creationTime = creationTime)