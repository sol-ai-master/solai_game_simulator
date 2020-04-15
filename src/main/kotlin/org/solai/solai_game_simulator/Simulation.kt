package org.solai.solai_game_simulator

import org.solai.solai_game_simulator.character_queue.GameSimulationData
import org.solai.solai_game_simulator.character_queue.GameSimulationResult
import sol_game.game.SolGameClient

interface Simulation : Runnable{

    val simulationId: String
    fun calculateResult(): GameSimulationResult
}