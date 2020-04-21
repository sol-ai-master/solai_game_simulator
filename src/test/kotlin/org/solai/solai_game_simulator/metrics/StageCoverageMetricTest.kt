package org.solai.solai_game_simulator.metrics

import org.joml.Vector2f
import org.junit.jupiter.api.Test
import sol_game.game_state.CircleObjectState
import sol_game.game_state.SolCharacterState
import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState
import java.lang.IllegalArgumentException
import org.junit.jupiter.api.Assertions.*


class StageCoverageMetricTest {
    private fun boardCoverageMetricWithCharacterPositions(worldSize: Vector2f, positions: List<List<Vector2f>>): StageCoverageMetric {
        if (positions.isEmpty() || positions.map { it.size }.distinct().count() != 1) {
            throw IllegalArgumentException("each position list must have the same size")
        }

        val solGameStateWithPositions = { positions: List<Vector2f> ->
            SolGameState(
                    true,
                    false,
                    -1,
                    positions.map { position -> SolCharacterState(CircleObjectState(position, 1f)) }
            )}

        val staticGameState = SolStaticGameState(listOf(), listOf())
        var gameState = solGameStateWithPositions(positions[0])

        val bcMetric = StageCoverageMetric()
        bcMetric.worldSize = worldSize
        bcMetric.start(positions[0].size, staticGameState, gameState)

        positions.forEach {
            gameState = solGameStateWithPositions(it)
            bcMetric.update(gameState)
        }

        return bcMetric
    }

    @Test
    fun testBoardCoverageMetric() {

        val metric1 = boardCoverageMetricWithCharacterPositions(
                Vector2f(100f, 100f),
                listOf(
                    listOf(Vector2f(0f, 0f), Vector2f(0f, 0f)),
                    listOf(Vector2f(0f, 0f), Vector2f(30f, 0f)),
                    listOf(Vector2f(0f, 0f), Vector2f(60f, 0f)),
                    listOf(Vector2f(0f, 0f), Vector2f(90f, 0f))
                )
        )
        val metric1Variances = metric1.calculate()
        println("discreteBoards: ${metric1.boardVisitsPerCharacter}")
        println("variances: $metric1Variances")
        assertTrue(metric1Variances[0] == 0f)
    }
}
