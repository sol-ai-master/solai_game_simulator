package org.solai.solai_game_simulator.metrics

import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState

class CharacterWonMetric : Metric {

    private var playersCount: Int = 0
    private var winnerIndex = -1

    override fun start(playersCount: Int, gameState: SolGameState) {
        this.playersCount = playersCount
    }

    override fun update(gameState: SolGameState) {
    }

    override fun end(gameState: SolGameState) {
        winnerIndex = gameState.playerIndexWon
    }

    override fun calculate(): List<Float> = (0 until playersCount).map { index -> if (index == winnerIndex) 1f else 0f}
}