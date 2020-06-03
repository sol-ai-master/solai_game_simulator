package org.solai.solai_game_simulator.players.helpers

import glm_.pow
import org.joml.Vector2f
import org.solai.solai_game_simulator.MathFuncs
import sol_engine.physics_module.PhysicsConstants
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolActions
import sol_game.game_state.SolCharacterState
import sol_game.game_state.SolGameState

class RulesCalculator(
        val weightedRules: Map<Float, Rule>
) {
    data class WeightedRuleOutput(
            val ruleOutput: RuleOutput,
            val weight: Float
    )

    fun calculateActions(controlledCharacterIndex: Int, gameState: SolGameState, charactersConfig: List<CharacterConfig>): SolActions {
        val otherCharIndex = (controlledCharacterIndex + 1) % 2
        val myChar = gameState.charactersState[controlledCharacterIndex]
        val otherChar = gameState.charactersState[otherCharIndex]
        val staticState = gameState.staticGameState
        val myCharConfig = charactersConfig[controlledCharacterIndex]
        val otherCharConfig = charactersConfig[otherCharIndex]

        val predictFutureFrames = 12
        val predictedMyChar = predictCharState(myChar, predictFutureFrames)
        val predictedOtherChar = predictCharState(otherChar, predictFutureFrames)

        val aimX = predictedOtherChar.physicalObject.position.x
        val aimY = predictedOtherChar.physicalObject.position.y

        val rulesOutput: List<WeightedRuleOutput> = weightedRules
                .map { WeightedRuleOutput(it.value.invoke(predictedMyChar, predictedOtherChar, staticState, myCharConfig, otherCharConfig), it.key) }

        val resultMoveDirection: Vector2f = rulesOutput
                .asSequence()
                .filter { it.ruleOutput.moveDirection != null }
                .map {
                    val moveDirection = it.ruleOutput.moveDirection!!
                    val normalizedMoveDir =
                            if (moveDirection.lengthSquared() == 0f) moveDirection
                            else moveDirection.normalize(Vector2f())
                    val weightedMoveDirection = normalizedMoveDir.mul(it.weight * it.ruleOutput.urgency, Vector2f())
                    weightedMoveDirection
                }
//                .map {
//                    if (it.lengthSquared() == 0f) it
//                    else it.normalize()
//                }
                .fold(Vector2f()) { acc, new ->
                    acc.add(new)
                }
//                .let {
//                    // apply fuzziness
//                    Vector2f(
//                            it.x * MathFuncs.randRange(1 - halfMovementFuzzyness, 1 + halfMovementFuzzyness),
//                            it.y * MathFuncs.randRange(1 - halfMovementFuzzyness, 1 + halfMovementFuzzyness)
//                    )
//                }
                .let {
                    // normalize again to not have a short vector that yields no movement
                    if (it.lengthSquared() == 0f) it
                    else it.normalize()
                }



        val resultAbilities: List<Boolean> = rulesOutput
                .filter { it.ruleOutput.abilities != null }
                .fold(Pair(0f, listOf(false, false, false))) { acc, weightedRuleOutput ->
                    val newUrgency = weightedRuleOutput.ruleOutput.urgency
                    val newWeight = weightedRuleOutput.weight
                    val compareMeasure = newUrgency * newWeight
                    if (compareMeasure < acc.first) acc
                    else Pair(compareMeasure, weightedRuleOutput.ruleOutput.abilities!!)
                }
                .second

        val moveDirectionActions = PlayerUtils.directionToMoveInput(resultMoveDirection)

        return SolActions(
                mvLeft = moveDirectionActions[0],
                mvRight = moveDirectionActions[1],
                mvUp = moveDirectionActions[2],
                mvDown = moveDirectionActions[3],
                ability1 = resultAbilities[0],
                ability2 = resultAbilities[1],
                ability3 = resultAbilities[2],
                aimX = aimX,
                aimY = aimY
        )
    }

    fun predictCharState(charState: SolCharacterState, futureFrames: Int): SolCharacterState {
        val charVeocity = charState.velocity
        val charPos = charState.physicalObject.position

        val predictedChar = charState.copy(
                physicalObject = charState.physicalObject.copy(
                        position = predictPosition(charPos, charVeocity, futureFrames, frictionMultiplier = 0.7f)
                ),
                currentHitboxes = charState.currentHitboxes
                        .map { hitbox ->
                            val pos = hitbox.physicalObject.position
                            val vel = hitbox.velocity
                            hitbox.copy(
                                    physicalObject = hitbox.physicalObject.copy(
                                            position = predictPosition(pos, vel, futureFrames)
                                    )
                            )
                        }
        )
        return predictedChar
    }

    fun predictPosition(
            currPos: Vector2f,
            currVel: Vector2f,
            futureFrames: Int,
            frictionMultiplier: Float = 1f
    ): Vector2f {
        val extrapolationMultiplier = futureFrames * PhysicsConstants.FIXED_UPDATE_TIME
        val frictionMultiplierOverFrames = frictionMultiplier.pow(futureFrames)
        val predictedPos = currPos.add(
                currVel.mul(extrapolationMultiplier * frictionMultiplierOverFrames, Vector2f()))
        return predictedPos
    }
}