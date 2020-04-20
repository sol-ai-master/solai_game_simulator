package org.solai.solai_game_simulator.character_queue

import sol_game.core_game.CharacterConfig


data class GameSimulationData(
        val simulationId: String,
        val charactersConfigs: List<CharacterConfig>,
        val metrics: List<String>
)



data class GameSimulationResult(
        val simulationId: String,
        val simulationData: GameSimulationData,
        val metrics: Map<String, List<Float>>
)