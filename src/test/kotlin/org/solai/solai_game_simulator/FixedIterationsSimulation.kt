package org.solai.solai_game_simulator

import org.solai.solai_game_simulator.simulation.Simulation
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState

class FixedIterationsSimulation(
        val iterations: Int = 100
) : Simulation {

    private var currentIterations = 0

    override fun setup() {
    }

    override fun start() {}

    override fun update() {
        currentIterations++
    }

    override fun isFinished(): Boolean {
        return currentIterations >= iterations
    }

    override fun end() {}

    override fun getState(): SolGameState {
        return SolGameState(true, false, -1, listOf())
    }

    override fun getStaticState(): SolStaticGameState {
        return SolStaticGameState(listOf(), listOf())
    }

    override fun setInputs(inputs: List<SolActions>) {}

}