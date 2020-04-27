package org.solai.solai_game_simulator.players

import org.joml.Vector2f
import sol_engine.ecs.World
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolActions
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

        val aimX = otherChar.physicalObject.position.x
        val aimY = otherChar.physicalObject.position.y

        val rulesOutput: List<WeightedRuleOutput> = weightedRules
                .map { WeightedRuleOutput(it.value.invoke(myChar, otherChar, staticState, myCharConfig, otherCharConfig), it.key) }

        val resultMoveDirection: Vector2f = rulesOutput
                .asSequence()
                .filter { it.ruleOutput.moveDirection != null }
                .map {
                    val weightedMoveDirection = it.ruleOutput.moveDirection!!.mul(it.weight, Vector2f())
                    weightedMoveDirection.mul(it.ruleOutput.urgency)
                }
                .map {
                    if (it.lengthSquared() == 0f) it
                    else it.normalize()
                }
                .fold(Vector2f()) { acc, new ->
                    acc.add(new)
                }
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
}