package org.solai.solai_game_simulator.metrics

import sol_game.game_state.SolGameState
import sol_game.game_state.SolGameStateFuncs
import sol_game.game_state.SolStaticGameState

class NearDeathFramesMetric : Metric {

    // near death frames per player
    private var nearDeathFrames: MutableList<Int>? = null
    private lateinit var staticGameState: SolStaticGameState

    private val DANGER_DISTANCE = 100f

    override fun start(staticGameState: SolStaticGameState, gameState: SolGameState) {
        this.staticGameState = staticGameState
        nearDeathFrames = gameState.charactersState.map { 0 }.toMutableList()
    }

    override fun update(gameState: SolGameState) {
        gameState.charactersState
                .map { SolGameStateFuncs.closestHole(it.physicalObject, staticGameState).length() }
                .map { distanceFromHole -> distanceFromHole <= DANGER_DISTANCE }
                .forEachIndexed { index, nearDeath -> if (nearDeath) nearDeathFrames?.let { it[index]++ } }
    }

    override fun calculate(): Float {
        return nearDeathFrames?.let { it.sum().toFloat() / it.size.toFloat() } ?: 0f
    }
}