package org.solai.solai_game_simulator.metrics

import sol_game.game_state.SolGameState
import sol_game.game_state.SolGameStateFuncs
import sol_game.game_state.SolStaticGameState

class NearDeathFramesMetric : Metric {

    // near death frames per player
    private var nearDeathFrames: MutableList<Int> = mutableListOf()
    private lateinit var staticGameState: SolStaticGameState
    private var playersCount: Int = 0

    private val DANGER_DISTANCE = 100f

    override fun start(playersCount: Int, staticGameState: SolStaticGameState, gameState: SolGameState) {
        this.playersCount = playersCount
        this.staticGameState = staticGameState
        nearDeathFrames = (0 until playersCount).map { 0 }.toMutableList()
    }

    override fun update(gameState: SolGameState) {
        gameState.charactersState
                .map { SolGameStateFuncs.closestHole(it.physicalObject, staticGameState).length() }
                .map { distanceFromHole -> distanceFromHole <= DANGER_DISTANCE }
                .forEachIndexed { index, nearDeath -> if (nearDeath) nearDeathFrames?.let { it[index]++ } }
    }

    override fun calculate(): List<Float> {
        return nearDeathFrames.map { it.toFloat() }
    }
}