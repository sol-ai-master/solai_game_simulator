package org.solai.solai_game_simulator

import org.solai.solai_game_simulator.character_queue.GameSimulationData
import sol_game.game.SolGameClient
import sol_game.game.SolGameServer

class SolSimulation(
        val simulationData: GameSimulationData
) : Simulation{

    override val simulationId = simulationData.simulationId

    private lateinit var gameServer: SolGameServer
    private lateinit var clients: List<SolGameClient>


    override fun run() {

    }

    private fun setupSimulation() {
        val simulationId = simulationData.simulationId

        val gameServer = SolGameServer(
                simulationData.charactersConfigs,
                -1,
                allowObservers = true,
                headless = true,
                debugUI = false,
                allowGui = false
        )

        val connectionData = gameServer.setup()

        val gameId = connectionData.gameId

        val client1 = SolGameClient(
                "localhost",
                connectionData.port,
                gameId,
                connectionData.teamsPlayersKeys[0][0],  // connect as player 1
                false,
                null,
                headless = true,
                debugUI = false,
                allowGui = false
        )

        val client2 = SolGameClient(
                "localhost",
                connectionData.port,
                gameId,
                connectionData.teamsPlayersKeys[1][0],  // connect as player 2
                false,
                null,
                headless = true,
                debugUI = false,
                allowGui = false
        )

        client1.setup()
        client2.setup()

        Thread.sleep(200)  // wait a bit because of a bug when clients connect right after the server is created

        client1.start()

        Thread.sleep(200)

        client2.start()

        this.gameServer = gameServer
        this.clients = listOf(client1, client2)
    }

    override fun calculateMetrics(): Map<String, Float> {
        return mapOf(
                "m1" to Math.random().toFloat(),
                "m2" to Math.random().toFloat(),
                "m3" to Math.random().toFloat()
        )
    }
}