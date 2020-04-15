package org.solai.solai_game_simulator.sol_simulation

import sol_game.game.SolGameState

class GameLengthMetric : SolGameMetric {

    private var gameLength: Int = 0

    override fun update(gameState: SolGameState) {
        gameLength ++
    }

    override fun getCalculation(): Float {
        return gameLength.toFloat()
    }
}