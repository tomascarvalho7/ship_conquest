package com.example.shipconquest.controller.model.output

data class CreateLobbyOutputModel(val tag: String) {
    val info: String = "Lobby with tag = $tag was created."
}