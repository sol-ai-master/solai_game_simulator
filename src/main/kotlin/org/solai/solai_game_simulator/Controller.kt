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
        val charConfig = CharacterConfigLoader.fromResourceFile("{\n  \"name\": \"Frank\",\n  \"radius\": 48,\n  \"moveVelocity\": 48,\n  \"abilities\": [\n    {\n      \"name\": \"rapid shot\",\n      \"type\": \"PROJECTILE\",\n      \"radius\": 10,\n      \"distanceFromChar\": 48,\n      \"speed\": 600,\n      \"activeTime\": 100,\n      \"startupTime\": 1,\n      \"executionTime\": 0,\n      \"endlagTime\": 0,\n      \"rechargeTime\": 10,\n      \"damage\": 20,\n      \"baseKnockback\": 100,\n      \"knockbackRatio\": 1,\n      \"knockbackPoint\": 32,\n      \"knockbackTowardPoint\": false\n    },\n    {\n      \"name\": \"mega shot\",\n      \"type\": \"PROJECTILE\",\n      \"radius\": 64,\n      \"distanceFromChar\": 128,\n      \"speed\": 1000,\n      \"activeTime\": 100,\n      \"startupTime\": 15,\n      \"executionTime\": 1,\n      \"endlagTime\": 5,\n      \"rechargeTime\": 10,\n      \"damage\": 20,\n      \"baseKnockback\": 100,\n      \"knockbackRatio\": 1,\n      \"knockbackPoint\": 32,\n      \"knockbackTowardPoint\": false\n    },\n    {\n      \"name\": \"get off\",\n      \"type\": \"MELEE\",\n      \"radius\": 256,\n      \"distanceFromChar\": 0,\n      \"speed\": 0,\n      \"activeTime\": 1,\n      \"startupTime\": 3,\n      \"executionTime\": 3,\n      \"endlagTime\": 7,\n      \"rechargeTime\": 10,\n      \"damage\": 20,\n      \"baseKnockback\": 100,\n      \"knockbackRatio\": 1,\n      \"knockbackPoint\": 32,\n      \"knockbackTowardPoint\": false\n    }\n  ]\n}\n")

        simulationQueue.pushSimulationData(GameSimulationData(
                UUID.randomUUID().toString(),
                listOf(charConfig, charConfig),
                listOf("gameLength", "nearDeathScenerios")
        ))

        return true
    }
}