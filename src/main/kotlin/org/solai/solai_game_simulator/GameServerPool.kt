package org.solai.solai_game_simulator

import sol_engine.network.network_game.GameHost
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.CharacterConfigLoader
import sol_game.core_game.CharacterConfig
import sol_game.game.SolGameServer
import sol_game.game.TerminationCallback
import java.util.*
import kotlin.collections.HashMap
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
                CharacterConfigLoader.fromResourceFile("frankCharacterConfig.json"),
                CharacterConfigLoader.fromResourceFile("schmathiasCharacterConfig.json")
        )
        val gameServer = SolGameServer(
                useCharacterConfigs,
                -1,
                true,
                headless,
                !headless,
                !headless
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