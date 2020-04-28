package org.solai.solai_game_simulator.metrics

import sol_game.game_state.SolGameState

class LeadChangeMetric : Metric {

    var leadChangeCount = 0
    var previousEvaluations: List<Float> = listOf()
    var playersCount: Int = 0

    override fun start(playersCount: Int, gameState: SolGameState) {
        this.playersCount = playersCount
        previousEvaluations = StateEvaluation.evaluate(gameState)
    }

    override fun update(gameState: SolGameState) {
        val prevLeadIndex = previousEvaluations.indices.maxBy { previousEvaluations[it] }
        val newEvaluations = StateEvaluation.evaluate(gameState)
        val newLeadIndex = newEvaluations.indices.maxBy { newEvaluations[it] }
        if (prevLeadIndex != newLeadIndex) {
            leadChangeCount++
        }
        previousEvaluations = newEvaluations
    }

    override fun calculate(): List<Float> {
        return (0 until playersCount).map { leadChangeCount.toFloat() }
    }
}