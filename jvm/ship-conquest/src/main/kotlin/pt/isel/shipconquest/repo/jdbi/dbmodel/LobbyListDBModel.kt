package pt.isel.shipconquest.repo.jdbi.dbmodel

import com.example.shipconquest.domain.lobby.Lobby

data class LobbyListDBModel(val lobbies: List<LobbyDBModel>)

fun List<LobbyDBModel>.toLobbyList() = map { lobby -> Lobby(lobby.tag, lobby.name, lobby.uid, lobby.username, lobby.creationTime) }