package org.solai.solai_game_simulator.players

import sol_engine.ecs.World
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState

interface Player {
    fun onSetup()

    fun onStart(
            controlledCharacterIndex: Int,
            staticGameState: SolStaticGameState,
            gameState: SolGameState
    )

    fun onUpdate(
            controlledCharacterIndex: Int,
            gameState: SolGameState
    ): SolActions

    fun onEnd(
            controlledCharacterIndex: Int,
            gameState: SolGameState
    )
}