package org.solai.solai_game_simulator.metrics

import org.solai.solai_game_simulator.simulator_core.Metric
import sol_game.game_state.SolGameState

class HitInteractionsMetric : Metric {

    private var hitInteractions: MutableList<Int> = mutableListOf()

    override fun start(playersCount: Int, gameState: SolGameState) {
        hitInteractions = (0 until playersCount).map { 0 }.toMutableList()
    }

    override fun update(gameState: SolGameState) {
        gameState.charactersState.forEachIndexed { index, charState ->
            val hitCountNow = charState.currentHitboxes
                    .flatMap { it.hitsGivenNow }
                    .count()
            hitInteractions[index] += hitCountNow
        }
    }

    override fun calculate(): List<Float> {
        return hitInteractions.map { it.toFloat() }
    }
}