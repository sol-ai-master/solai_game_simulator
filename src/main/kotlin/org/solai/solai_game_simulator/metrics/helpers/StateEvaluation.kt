package org.solai.solai_game_simulator.metrics.helpers

import org.solai.solai_game_simulator.MathFuncs
import sol_game.game_state.SolGameState
import sol_game.game_state.SolGameStateFuncs

object StateEvaluation {

    fun evaluate(state: SolGameState): List<Float> {
        return state.charactersState.mapIndexed { index, _ -> evaluateSingleCharacter(state, index) }
    }

    fun evaluateSingleCharacter(state: SolGameState, characterIndex: Int): Float {
        val charConfig = state.charactersState[characterIndex]

        val maxStocks = 3f
        val stockEvaluation = (charConfig.stocks.toFloat()-1) / (maxStocks-1)

        val maxDamage = 5000f  // what we consider max
        val damageEvaluation = MathFuncs.linearBetween(maxDamage, 0f, charConfig.damage)

//        val holeDistance = SolGameStateFuncs.closestHole(charConfig.physicalObject, state.staticGameState).length()
//        val maxHoleDistance = 300f
//        val holeDistanceEvaluation = MathFuncs.linearBetween(0f, maxHoleDistance, holeDistance)

        val weightedEvaluation = 0.67f * stockEvaluation + 0.33f * damageEvaluation // + 0.20f * holeDistanceEvaluation
        return weightedEvaluation
    }

}