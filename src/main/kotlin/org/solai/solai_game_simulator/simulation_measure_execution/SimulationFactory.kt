package org.solai.solai_game_simulator.simulation_measure_execution

import org.solai.solai_game_simulator.simulation.Simulation
import org.solai.solai_game_simulator.simulation.SolSimulation
import sol_game.core_game.CharacterConfig


class SimulationFactory(
    headless: Boolean = true
) {
    var headless: Boolean = headless
        @Synchronized get
        @Synchronized set

    fun getSimulation(charactersConfig: List<CharacterConfig>): Simulation {
        val sim = SolSimulation(charactersConfig)
        sim.setup(headless)
        return sim;
    }
}