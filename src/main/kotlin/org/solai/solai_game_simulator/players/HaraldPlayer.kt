package org.solai.solai_game_simulator.players

import sol_engine.ecs.World
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState

class HaraldPlayer: Player {

    override fun onSetup() {
    }

    override fun onEnd(controlledCharacterIndex: Int, gameState: SolGameState) {
    }

    override fun onStart(controlledCharacterIndex: Int, staticGameState: SolStaticGameState, gameState: SolGameState) {
    }

    override fun onUpdate(controlledCharacterIndex: Int, gameState: SolGameState): SolActions {
        return SolActions(mvLeft=true, ability1 = true)
    }

}