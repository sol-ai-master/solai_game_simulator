package org.solai.solai_game_simulator.character_queue

data class AbilityData(
        val type: String
)

data class CharacterData(
        val characterId: String,
        val moveAcceleration: Float,
        val characterRadius: Float,
        val abilitiesData: List<AbilityData>
)

data class GameSimulationData(
        val simulationId: String,
        val charactersData: List<CharacterData>,
        val metrics: List<String>
)



data class GameSimulationResult(
        val simulationId: String,
        val simulationData: GameSimulationData,
        val metric: List<String>,
        val measurements: List<Float>
)