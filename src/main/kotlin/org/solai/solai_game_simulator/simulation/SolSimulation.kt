package org.solai.solai_game_simulator.simulation

import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolActions
import sol_game.core_game.SolGameSimulationOffline
import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState

class SolSimulation(
        private val characterConfigs: List<CharacterConfig>
) : Simulation {

    private lateinit var solSimulation: SolGameSimulationOffline

    override fun setup(headless: Boolean) {
        solSimulation = SolGameSimulationOffline(
                charactersConfigs = characterConfigs,
                graphicsSettings = SolGameSimulationOffline.GraphicsSettings(
                        headless = headless,
                        graphicalInput = false,
                        allowGui = true
                )
        )

        solSimulation.setup()
    }

    override fun start() {
        solSimulation.start()

        // step once to add all entities into the world
        solSimulation.step()
    }

    override fun update() {
        solSimulation.step()
    }

    override fun isFinished(): Boolean {
        return getState().gameEnded || solSimulation.isFinished
    }

    override fun end() {
        solSimulation.terminate()
    }

    override fun getState(): SolGameState {
        return solSimulation.retrieveGameState()
    }

    override fun getStaticState(): SolStaticGameState {
        return solSimulation.retrieveStaticGameState()
    }

    override fun setInputs(inputs: List<SolActions>) {
        inputs.forEachIndexed { index, input -> solSimulation.setInputs(index, input) }
    }

}