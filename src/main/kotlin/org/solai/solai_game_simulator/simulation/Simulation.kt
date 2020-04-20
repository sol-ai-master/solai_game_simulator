package org.solai.solai_game_simulator.simulation

import org.solai.solai_game_simulator.character_queue.GameSimulationResult
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState


interface Simulation {

    fun setup(headless: Boolean = true)

    fun start()

    fun update()

    fun isFinished(): Boolean

    fun end()

    fun getState(): SolGameState
    fun getStaticState(): SolStaticGameState

    fun setInputs(inputs: List<SolActions>)
}