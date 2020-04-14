package org.solai.solai_game_simulator.sol_simulation

import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_engine.network.network_sol_module.NetworkServerModule
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolGameSimulationServer
import sol_game.core_game.SolGameStateUtils
import sol_game.game.SolGameState

class SolCustomServerSimulation(
        charactersConfigs: List<CharacterConfig>,
        val metrics: List<SolGameMetric>,
        allowObservers: Boolean,
        headless: Boolean
) : SolGameSimulationServer(
        charactersConfigs,
        allowObservers = allowObservers,
        headless = headless,
        debugUI = !headless,
        allowGui = !headless
) {

    private var hasStarted: Boolean = false
    private var hasEnded: Boolean = false
    lateinit var gameState: SolGameState

    private fun retrieveGameState(): SolGameState {
        return SolGameStateUtils.retrieveSolGameState(world)
    }

    fun getConnectionData(): ServerConnectionData {
        return getModulesHandler().getModule(NetworkServerModule::class.java).connectionData
    }

    override fun onStart() {
        super.onStart()
        gameState = retrieveGameState()
    }

    override fun onStepEnd() {
        super.onStepEnd()

        gameState = retrieveGameState()

        if (!hasStarted) {
            if (gameState.gameStarted) {
                hasStarted = true
                metrics.forEach { it.start(gameState) }
            }
        } else if (!hasEnded) {
            if (gameState.gameEnded) {
                hasEnded = true
                metrics.forEach { it.end(gameState) }
            } else {
                metrics.forEach { it.update(gameState) }
            }
        }
    }
}