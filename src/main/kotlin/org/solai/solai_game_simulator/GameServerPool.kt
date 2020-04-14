package org.solai.solai_game_simulator

import sol_engine.network.network_game.GameHost
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.CharacterConfigLoader
import sol_game.core_game.CharacterConfig
import sol_game.game.SolGameServer
import sol_game.game.TerminationCallback
import java.util.*
import kotlin.collections.LinkedHashMap

data class ConnectedPlayersData(
        val clientData: GameHost,
        val health: Float
)

//data class RunningGameData(
//        val
//)

class GameServerPool {

    private val runningGameServers: MutableMap<String, SolGameServer> = Collections.synchronizedMap(LinkedHashMap())


    fun startNewGame(
            characterConfigs: List<CharacterConfig>? = null,
            terminationCallback: TerminationCallback = {},
            headless: Boolean = true
    ): ServerConnectionData {
        val useCharacterConfigs = characterConfigs ?: listOf(
                CharacterConfigLoader.fromResourceFile("{\n  \"name\": \"Frank\",\n  \"radius\": 48,\n  \"moveVelocity\": 48,\n  \"abilities\": [\n    {\n      \"name\": \"rapid shot\",\n      \"type\": \"PROJECTILE\",\n      \"radius\": 10,\n      \"distanceFromChar\": 48,\n      \"speed\": 600,\n      \"activeTime\": 100,\n      \"startupTime\": 1,\n      \"executionTime\": 0,\n      \"endlagTime\": 0,\n      \"rechargeTime\": 10,\n      \"damage\": 20,\n      \"baseKnockback\": 100,\n      \"knockbackRatio\": 1,\n      \"knockbackPoint\": 32,\n      \"knockbackTowardPoint\": false\n    },\n    {\n      \"name\": \"mega shot\",\n      \"type\": \"PROJECTILE\",\n      \"radius\": 64,\n      \"distanceFromChar\": 128,\n      \"speed\": 1000,\n      \"activeTime\": 100,\n      \"startupTime\": 15,\n      \"executionTime\": 1,\n      \"endlagTime\": 5,\n      \"rechargeTime\": 10,\n      \"damage\": 20,\n      \"baseKnockback\": 100,\n      \"knockbackRatio\": 1,\n      \"knockbackPoint\": 32,\n      \"knockbackTowardPoint\": false\n    },\n    {\n      \"name\": \"get off\",\n      \"type\": \"MELEE\",\n      \"radius\": 256,\n      \"distanceFromChar\": 0,\n      \"speed\": 0,\n      \"activeTime\": 1,\n      \"startupTime\": 3,\n      \"executionTime\": 3,\n      \"endlagTime\": 7,\n      \"rechargeTime\": 10,\n      \"damage\": 20,\n      \"baseKnockback\": 100,\n      \"knockbackRatio\": 1,\n      \"knockbackPoint\": 32,\n      \"knockbackTowardPoint\": false\n    }\n  ]\n}\n"),
                CharacterConfigLoader.fromResourceFile("{\n  \"name\": \"Schmatias\",\n  \"radius\": 64,\n  \"moveVelocity\": 36,\n  \"abilities\": [\n    {\n      \"name\": \"punch\",\n      \"type\": \"MELEE\",\n      \"radius\": 32,\n      \"distanceFromChar\": 16,\n      \"speed\": 0,\n      \"activeTime\": 4,\n      \"startupTime\": 6,\n      \"executionTime\": 2,\n      \"endlagTime\": 3,\n      \"rechargeTime\": 50,\n      \"damage\": 80,\n      \"baseKnockback\": 200,\n      \"knockbackRatio\": 1,\n      \"knockbackPoint\": 32,\n      \"knockbackTowardPoint\": false\n    },\n    {\n      \"name\": \"hook\",\n      \"type\": \"PROJECTILE\",\n      \"radius\": 48,\n      \"distanceFromChar\": 64,\n      \"speed\": 700,\n      \"activeTime\": 100,\n      \"startupTime\": 10,\n      \"executionTime\": 0,\n      \"endlagTime\": 5,\n      \"rechargeTime\": 90,\n      \"damage\": 150,\n      \"baseKnockback\": 300,\n      \"knockbackRatio\": 0.2,\n      \"knockbackPoint\": -32,\n      \"knockbackTowardPoint\": false\n    },\n    {\n      \"name\": \"mega punch\",\n      \"type\": \"MELEE\",\n      \"radius\": 16,\n      \"distanceFromChar\": 32,\n      \"speed\": 0,\n      \"activeTime\": 1,\n      \"startupTime\": 15,\n      \"executionTime\": 3,\n      \"endlagTime\": 7,\n      \"rechargeTime\": 10,\n      \"damage\": 20,\n      \"baseKnockback\": 100,\n      \"knockbackRatio\": 1,\n      \"knockbackPoint\": 32,\n      \"knockbackTowardPoint\": false\n    }\n  ]\n}\n")
        )
        val gameServer = SolGameServer(
                useCharacterConfigs,
                -1,
                true,
                headless = headless,
                debugUI = !headless,
                allowGui = !headless
        )
        gameServer.onTermination { terminationCallback.invoke(it) }
        val connectionData = gameServer.setup()
        gameServer.onTermination { runningGameServers.remove(connectionData.gameId) }
        gameServer.start()
        runningGameServers[connectionData.gameId] = gameServer
        return connectionData
    }

    fun getPlayersData(gameId: String): List<ConnectedPlayersData>? {
        val gameServer = runningGameServers[gameId]
        if (gameServer != null) {
            println("retrieveing players data")
            val playersConnectionData = gameServer.getPlayersConnectionData()
            val playersDamage = gameServer.getPlayersDamage()
            return playersConnectionData.mapIndexed { i, gameHost ->
                ConnectedPlayersData(gameHost, playersDamage[i])
            }
        }
        else {
            return null
        }

    }

    fun getAllRunningGameIds(): List<String> {
        return runningGameServers.keys.toList()
    }

//    fun getAllRunningGamesData() {
//        runningGameServers.values.forEach() {it.}
//    }

    fun stopGame(gameId: String): Boolean {
        runningGameServers[gameId]?.let {
            it.terminate()
            return@stopGame true
        }
        return false
    }

}