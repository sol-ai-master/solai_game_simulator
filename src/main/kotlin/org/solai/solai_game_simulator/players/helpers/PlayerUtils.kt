package org.solai.solai_game_simulator.players.helpers

import org.joml.Vector2f
import sol_engine.utils.math.MathF

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

}