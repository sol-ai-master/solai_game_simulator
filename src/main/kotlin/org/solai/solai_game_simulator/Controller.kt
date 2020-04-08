package org.solai.solai_game_simulator

import org.springframework.web.bind.annotation.*
import sol_engine.network.network_game.game_server.ServerConnectionData


val gameServerPool = GameServerPool()


@RestController()
class Controller {

    @GetMapping("/hello")
    fun hello() = "hello"

    // creates a game server with default characters
    @PostMapping("/createGame")
    fun createGameServer(): ServerConnectionData {
        return gameServerPool.startNewGame()
    }

    @GetMapping("/runningGames")
    fun runningGames(): List<String> {
        return gameServerPool.getAllRunningGameIds()
    }

    @DeleteMapping("/terminateGame/{gameId}")
    fun terminateGame(@PathVariable gameId: String): Boolean {
        return gameServerPool.stopGame(gameId)
    }

    @GetMapping("/playersData/{gameId}")
    fun playersData(@PathVariable gameId: String): List<ConnectedPlayersData>? {
        return gameServerPool.getPlayersData(gameId)
    }
}