package org.solai.solai_game_simulator.sol_simulation

import org.solai.solai_game_simulator.Simulation
import org.solai.solai_game_simulator.character_queue.GameSimulationData
import sol_game.game.SolGameClient
import sol_game.game.SolRandomTestPlayer

class SolSimulation(
        private val simulationData: GameSimulationData
) : Simulation {

    override val simulationId = simulationData.simulationId

    private lateinit var gameServerSimulation: SolCustomServerSimulation
    private lateinit var clients: List<SolGameClient>
    private lateinit var metrics: List<SolGameMetric>

    override fun run() {
        metrics = simulationData.metrics.mapNotNull { ExistingMetrics.getMeasure(it) }
        metrics.forEach { it.setup() }

        gameServerSimulation = SolCustomServerSimulation(
                simulationData.charactersConfigs,
                metrics,
                allowObservers = true,
                headless = true
        )
        gameServerSimulation.setup()
        val connectionData = gameServerSimulation.getConnectionData()

        val client1 = SolGameClient(
                "localhost",
                connectionData .port,
                connectionData.gameId,
                connectionData.teamsPlayersKeys[0][0],  // connect as player 1
                false,
                SolRandomTestPlayer::class.java,
                updateFrameTime = 0f,
                headless = true,
                debugUI = false,
                allowGui = false
        )

        val client2 = SolGameClient(
                "localhost",
                connectionData.port,
                connectionData.gameId,
                connectionData.teamsPlayersKeys[1][0],  // connect as player 2
                false,
                SolRandomTestPlayer::class.java,
                updateFrameTime = 0f,
                headless = true,
                debugUI = false,
                allowGui = false
        )

        clients = listOf(client1, client2)
        clients.forEach { it.setup() }

        runSimulation()
    }

    private fun runSimulation() {
        gameServerSimulation.start()
        clients.forEach { it.start() }

        while (!gameServerSimulation.gameState.gameEnded) {
            gameServerSimulation.step()
        }

        clients.forEach { it.terminate() }
        gameServerSimulation.terminate()
    }

    override fun calculateMetrics(): Map<String, Float> {
        return metrics.map { it.metricName to it.getCalculation() }.toMap()
    }
}