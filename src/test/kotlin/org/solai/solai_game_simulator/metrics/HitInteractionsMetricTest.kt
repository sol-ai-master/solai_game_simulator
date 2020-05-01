package org.solai.solai_game_simulator.metrics

import org.joml.Vector2f
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import sol_game.game_state.*

class HitInteractionsMetricTest {

    @Test
    fun testHitInteractionMetric() {

        val hitInteractionMetric = HitInteractionsMetric()
        hitInteractionMetric.setup()
        hitInteractionMetric.start(2, gameStateWithHitbox(false, false))
        hitInteractionMetric.update(gameStateWithHitbox(false, false))

        assertEquals(listOf(0f, 0f), hitInteractionMetric.calculate()) { "Metric was not 0 when no hits are applied" }

        hitInteractionMetric.update(gameStateWithHitbox(true, false))
        assertEquals(listOf(1f, 0f), hitInteractionMetric.calculate()) { "Char1 was not registered with one hit" }
        hitInteractionMetric.update(gameStateWithHitbox(false, true))
        assertEquals(listOf(1f, 1f), hitInteractionMetric.calculate()) { "Char2 was not registered with one hit" }

        hitInteractionMetric.update(gameStateWithHitbox(true, true))
        hitInteractionMetric.update(gameStateWithHitbox(true, false))
        hitInteractionMetric.update(gameStateWithHitbox(false, false))
        hitInteractionMetric.update(gameStateWithHitbox(false, true))

        assertEquals(listOf(3f, 3f), hitInteractionMetric.calculate()) { "char1 and char2 should have 3 hits registered" }
    }

    private fun gameStateWithHitbox(char1Hit: Boolean, char2Hit: Boolean): SolGameState {
        val singleHitboxState = {hit: Boolean ->
            if (hit)
                listOf(emptyHitboxState().copy(
                        hitsGivenNow = listOf(HitboxHitState(Vector2f(), 0f))
                ))
            else
                listOf()
        }


        val singleHitState = emptyGameState().copy(
                charactersState = listOf(
                        emptyCharacterState().copy(
                                currentHitboxes = singleHitboxState(char1Hit)
                        ),
                        emptyCharacterState().copy(
                                currentHitboxes = singleHitboxState(char2Hit)
                        )
                )
        )

        return singleHitState
    }
}