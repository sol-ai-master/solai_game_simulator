package org.solai.solai_game_simulator.metrics

import org.solai.solai_game_simulator.metrics.helpers.StateEvaluation
import org.solai.solai_game_simulator.simulator_core.Metric
import sol_game.game_state.SolGameState

class StateEvaluationsAccumulatedMetric : Metric {

    var accumulatedStateEvaluations: MutableList<Float> = mutableListOf()

    override fun start(playersCount: Int, gameState: SolGameState) {
        accumulatedStateEvaluations = (0 until playersCount).map { 0f }.toMutableList()
    }
    override fun update(gameState: SolGameState) {
        StateEvaluation.evaluate(gameState).forEachIndexed { index, eval -> accumulatedStateEvaluations[index] += eval}
    }

    override fun calculate(): List<Float> {
        return accumulatedStateEvaluations.toList()
    }
}