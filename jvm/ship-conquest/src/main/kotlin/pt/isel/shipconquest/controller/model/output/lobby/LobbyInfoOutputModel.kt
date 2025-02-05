package pt.isel.shipconquest.controller.model.output.lobby

import pt.isel.shipconquest.domain.lobby.LobbyInfo


data class LobbyInfoOutputModel(
    val tag: String,
    val name: String,
    val uid: String,
    val username: String,
    val creationTime: Long,
    val isFavorite: Boolean,
    val players: Int
)

fun LobbyInfo.toLobbyInfoOutputModel() =
    LobbyInfoOutputModel(tag, name, uid, username, creationTime, isFavorite, lobbyCount)