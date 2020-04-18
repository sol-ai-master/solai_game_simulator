package org.solai.solai_game_simulator.metrics

import org.solai.solai_game_simulator.metrics.Metric
import sol_game.game_state.SolGameState

class GameLengthMetric : Metric {

    private var gameLength: Int = 0

    override fun update(gameState: SolGameState) {
        gameLength ++
    }

    override fun calculate(): Float {
        return gameLength.toFloat()
    }
}