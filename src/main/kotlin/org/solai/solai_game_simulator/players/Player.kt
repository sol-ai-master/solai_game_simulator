package org.solai.solai_game_simulator.players

import sol_engine.ecs.World
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState

interface Player {
    fun onSetup() {}

    fun onStart(
            controlledCharacterIndex: Int,
            gameState: SolGameState,
            charactersConfig: List<CharacterConfig>
    ) {}

    fun onUpdate(
            controlledCharacterIndex: Int,
            gameState: SolGameState,
            charactersConfig: List<CharacterConfig>
    ): SolActions

    fun onEnd(
            controlledCharacterIndex: Int,
            gameState: SolGameState,
            charactersConfig: List<CharacterConfig>
    ) {}
}