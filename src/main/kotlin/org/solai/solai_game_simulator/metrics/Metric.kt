package org.solai.solai_game_simulator.metrics

import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState


interface Metric {
    fun setup() {}
    fun start(staticGameState: SolStaticGameState, gameState: SolGameState) {}
    fun update(gameState: SolGameState)
    fun end(gameState: SolGameState) {}

    fun calculate(): Float
}