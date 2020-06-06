package org.solai.solai_game_simulator.players.helpers

import org.joml.Vector2f
import org.solai.solai_game_simulator.MathFuncs
import sol_engine.utils.math.MathF
import sol_game.core_game.SolActions
import java.util.*

object PlayerUtils {
    // output directions from index 0 - 3: left, right, up, down
    fun directionToMoveInput(normalizedDirection: Vector2f): List<Boolean> {
        val coordThreshold = MathF.sin(MathF.PI / (4f * 2f))// ~0.7

        val mvLeft = normalizedDirection.x <= -coordThreshold
        val mvRight = normalizedDirection.x >= coordThreshold
        val mvUp = normalizedDirection.y <= -coordThreshold
        val mvDown = normalizedDirection.y >= coordThreshold

        return listOf(mvLeft, mvRight, mvUp, mvDown)
    }

    fun directionToMoveInputAsAction(normalizedDirection: Vector2f): SolActions {
        val inputs = directionToMoveInput(normalizedDirection)
        return SolActions(mvLeft = inputs[0], mvRight = inputs[1], mvUp = inputs[2], mvDown = inputs[3])
    }

    fun applyFuzziness(
            actions: SolActions,
            movementFuzzyness: Float,  // the probability of swapping a movement input
            abilityFuzzyness: Float, // the probability of swapping an ability input
            aimVariance: Float  // the variance of a normal distribution around the given aim to sample fuzziness
    ): SolActions {
        val random = Random()
        return SolActions(
                mvLeft = if (MathF.random() < movementFuzzyness) !actions.mvLeft else actions.mvLeft,
                mvRight = if (MathF.random() < movementFuzzyness) !actions.mvRight else actions.mvRight,
                mvUp = if (MathF.random() < movementFuzzyness) !actions.mvUp else actions.mvUp,
                mvDown = if (MathF.random() < movementFuzzyness) !actions.mvDown else actions.mvDown,

                ability1 = if (MathF.random() < abilityFuzzyness) !actions.ability1 else actions.ability1,
                ability2 = if (MathF.random() < abilityFuzzyness) !actions.ability2 else actions.ability2,
                ability3 = if (MathF.random() < abilityFuzzyness) !actions.ability3 else actions.ability3,

                aimX = actions.aimX + random.nextGaussian().toFloat() * aimVariance,
                aimY = actions.aimY + random.nextGaussian().toFloat() * aimVariance
        )
    }

}