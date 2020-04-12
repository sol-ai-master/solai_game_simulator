package org.solai.solai_game_simulator

import org.solai.solai_game_simulator.character_queue.GameSimulationData
import org.solai.solai_game_simulator.character_queue.GameSimulationResult
import sol_game.game.SolGameClient
import sol_game.game.SolGameServer
import java.util.*
import kotlin.collections.LinkedHashMap

typealias SimulationResultListener = (simulationResult: GameSimulationResult) -> Unit

class Simulator {
    data class Simulation(
            val simulationId: String,
            val gameId: String,
            val clients: List<SolGameClient>,
            val simulationData: GameSimulationData
    // measure observer
    )

    private var simulationResultListener: SimulationResultListener = {}

    private val runningSimulations: MutableMap<String, Simulation> = Collections.synchronizedMap(LinkedHashMap())

    fun onSimulationResult(listener: SimulationResultListener) {
        simulationResultListener = listener
    }

    fun simulate(
            serverPool: GameServerPool,
            gameSimulationData: GameSimulationData
            ) {

        val testSim = Simulation(
                gameSimulationData.simulationId,
                "some-game-id",
                clients = listOf(),
                simulationData = gameSimulationData
        )
        runningSimulations[testSim.simulationId] = testSim
        onSimulationEnd(testSim.simulationId)

//        startSimulation(serverPool, gameSimulationData)
    }

    private fun startSimulation(
            serverPool: GameServerPool,
            gameSimulationData: GameSimulationData
    ) {
        val simulationId = gameSimulationData.simulationId

        val connectionData = serverPool.startNewGame(
                characterConfigs = gameSimulationData.charactersConfigs,
                terminationCallback = {onSimulationEnd(simulationId)}
        )

        val gameId = connectionData.gameId

        val client1 = SolGameClient(
                "localhost",
                connectionData.port,
                gameId,
                connectionData.teamsPlayersKeys[0][0],
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
                connectionData.teamsPlayersKeys[1][0],
                false,
                null,
                headless = true,
                debugUI = false,
                allowGui = false
        )

        client1.setup()
        client2.setup()

        Thread.sleep(200)

        client1.start()

        Thread.sleep(200)

        client2.start()

        val simulation = Simulation(
                simulationId,
                gameId,
                listOf(client1, client2),
                gameSimulationData
        )
        runningSimulations[simulationId] = simulation
    }


    private fun onSimulationEnd(simulationId: String) {
        runningSimulations[simulationId]?.let {simulation ->

            simulation.clients.forEach {it.terminate()}

            val simRes = GameSimulationResult(
                    simulation.simulationId,
                    simulation.simulationData,
                    simulation.simulationData.metrics,
                    simulation.simulationData.metrics.map { (Math.random() * 100).toFloat() }
            )

            runningSimulations.remove(simulation.simulationId)

            simulationResultListener.invoke(simRes)
        }

    }

}