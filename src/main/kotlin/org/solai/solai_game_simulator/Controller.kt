package org.solai.solai_game_simulator

import org.solai.solai_game_simulator.character_queue.GameSimulationData
import org.solai.solai_game_simulator.character_queue.SimulationQueue
import org.springframework.web.bind.annotation.*
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.CharacterConfigLoader
import java.lang.IllegalStateException
import java.util.*


@RestController()
class Controller {

    private val gameServerPool = GameServerPool()
    private val simulationQueue: SimulationQueue
//    private val simulationQueueExecutor: SimulationQueueExecuter

    init {
        simulationQueue = SimulationQueue.getQueue("localhost") ?: run {
            throw IllegalStateException("Could not connect to simulation queue")
        }
//        simulationQueueExecutor = SimulationQueueExecuter(gameServerPool)
//        simulationQueueExecutor.start()
    }

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

    @PostMapping("/pushExampleSimulation")
    fun pushExampleSimulation(): Boolean {
        val charConfig = CharacterConfigLoader.fromResourceFile("frankCharacterConfig.json")

        simulationQueue.pushSimulationData(GameSimulationData(
                UUID.randomUUID().toString(),
                listOf(charConfig, charConfig),
                listOf("gameLength", "nearDeathScenerios")
        ))

        return true
    }
}