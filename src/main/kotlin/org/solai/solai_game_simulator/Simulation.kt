package org.solai.solai_game_simulator

import org.solai.solai_game_simulator.character_queue.GameSimulationData
import sol_game.game.SolGameClient

interface Simulation : Runnable{

    val simulationId: String
    fun calculateMetrics(): Map<String, Float>
}