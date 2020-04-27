package org.solai.solai_game_simulator.metrics

import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState

class GameLengthMetric : Metric {

    private var gameLength: Int = 0
    private var playersCount: Int = 0


    override fun start(playersCount: Int, gameState: SolGameState) {
        this.playersCount = playersCount
    }

    override fun update(gameState: SolGameState) {
        gameLength ++
    }

    override fun calculate(): List<Float> {
        return (0 until playersCount).map { gameLength.toFloat() }
    }
}