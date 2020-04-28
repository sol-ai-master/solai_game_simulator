package org.solai.solai_game_simulator

import org.joml.Vector2f
import org.solai.solai_game_simulator.simulation.Simulation
import sol_engine.ecs.World
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState

class FixedIterationsSimulation(
        val iterations: Int = 100
) : Simulation {

    private var currentIterations = 0

    override fun setup(headless: Boolean){}

    override fun start() {}

    override fun update() {
        currentIterations++
    }

    override fun isFinished(): Boolean {
        return currentIterations >= iterations
    }

    override fun end() {}

    override fun getState(): SolGameState {
        return SolGameState(
                gameStarted = true,
                gameEnded = false,
                playerIndexWon = -1,
                staticGameState = SolStaticGameState(Vector2f(100f, 100f), listOf(), listOf()),
                charactersState = listOf(),
                world = World()
        )
    }
    override fun setInputs(inputs: List<SolActions>) {}

}