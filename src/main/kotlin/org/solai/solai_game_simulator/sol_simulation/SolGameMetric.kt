package org.solai.solai_game_simulator.sol_simulation

import sol_game.game.SolGameState

interface SolGameMetric {
    fun setup() {}
    fun start(gameState: SolGameState) {}
    fun update(gameState: SolGameState)
    fun end(gameState: SolGameState) {}

    fun getCalculation(): Float
}