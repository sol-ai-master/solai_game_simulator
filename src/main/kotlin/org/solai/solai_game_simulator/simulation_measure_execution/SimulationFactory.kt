package org.solai.solai_game_simulator.simulation_measure_execution

import org.solai.solai_game_simulator.simulation.Simulation
import sol_game.core_game.CharacterConfig

interface SimulationFactory {
    fun getSimulation(charactersConfig: List<CharacterConfig>): Simulation
}