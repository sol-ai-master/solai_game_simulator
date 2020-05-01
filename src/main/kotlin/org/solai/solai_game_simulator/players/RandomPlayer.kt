package org.solai.solai_game_simulator.players

import org.joml.Vector2f
import org.solai.solai_game_simulator.simulator_core.Player
import sol_engine.utils.math.MathF
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameState
import sol_game.game_state.SolGameStateFuncs

class RandomPlayer : Player {
    private var moveDirection: Vector2f = Vector2f()

    override fun onSetup() {

    }

    override fun onStart(controlledCharacterIndex: Int, gameState: SolGameState, charactersConfig: List<CharacterConfig>) {
        moveDirection = Vector2f(MathF.randRange(-1f, 1f), MathF.randRange(-1f, 1f))
    }

    override fun onUpdate(controlledCharacterIndex: Int, gameState: SolGameState, charactersConfig: List<CharacterConfig>): SolActions {
        val myChar = gameState.charactersState[controlledCharacterIndex]
        val otherChar = gameState.charactersState.getOrNull((controlledCharacterIndex + 1) % 2)

        val aimX: Float = otherChar?.physicalObject?.position?.x ?: MathF.randRange(0f, 1600f)
        val aimY: Float = otherChar?.physicalObject?.position?.y ?: MathF.randRange(0f, 900f)


        val useAb1 = MathF.randInt(0, 60) == 0
        val useAb2 = MathF.randInt(0, 60 * 2) == 0
        val useAb3 = MathF.randInt(0, 60 * 5) == 0

        moveDirection.add(MathF.randRange(-0.5f, 0.5f), MathF.randRange(-0.5f, 0.5f))

        val closestHole = SolGameStateFuncs.closestHole(myChar.physicalObject, gameState.staticGameState)
        if (closestHole.lengthSquared() != 0f) {
            val holeDistanceLinearRatio = (100f / closestHole.length().coerceAtLeast(0.01f))
            val holeDistanceExpRatio = holeDistanceLinearRatio * holeDistanceLinearRatio
            closestHole.normalize(-holeDistanceExpRatio)
            moveDirection.add(closestHole)
        }

        moveDirection.normalize()

        return SolActions(
                mvLeft = moveDirection.x < -0.7,
                mvRight = moveDirection.x > 0.7,
                mvUp = moveDirection.y < -0.7,
                mvDown = moveDirection.y > 0.7,
                ability1 = useAb1,
                ability2 = useAb2,
                ability3 = useAb3,
                aimX = aimX,
                aimY = aimY
        )
    }

    override fun onEnd(controlledCharacterIndex: Int, gameState: SolGameState, charactersConfig: List<CharacterConfig>) {
    }

}