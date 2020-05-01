package org.solai.solai_game_simulator.players.helpers

import org.joml.Vector2f
import org.solai.solai_game_simulator.MathFuncs
import sol_engine.utils.math.MathF
import sol_game.core_game.AbilityConfig
import sol_game.core_game.CharacterConfig
import sol_game.game_state.SolCharacterState
import sol_game.game_state.SolGameStateFuncs
import sol_game.game_state.SolStaticGameState


data class RuleOutput(
        val urgency: Float,
        val moveDirection: Vector2f? = null,
        val abilities: List<Boolean>? = null
)
typealias Rule = (
        myChar: SolCharacterState,
        otherChar: SolCharacterState,
        staticState: SolStaticGameState,
        myCharConfig: CharacterConfig,
        otherCharConfig: CharacterConfig
) -> RuleOutput


object PlayerRules {

    fun createAvoidHolesRule(maxDistance: Float = 500f, minDistance: Float = 100f): Rule {
        val avoidHolesRule: Rule = { myChar, otherChar, staticState, _, _ ->

            val closestHole = SolGameStateFuncs.closestHole(myChar.physicalObject, staticState)
            val closestHoleDistSquared = closestHole.lengthSquared()
            val ruleOutput =
                    if (closestHoleDistSquared != 0f && closestHoleDistSquared < maxDistance * maxDistance) {
                        val holeDistanceLinearRatio = (minDistance / closestHole.length().coerceAtLeast(0.01f))
                        val holeDistanceExpRatio = holeDistanceLinearRatio * holeDistanceLinearRatio
                        val moveDir = closestHole.negate(Vector2f())
                        val urgency = holeDistanceExpRatio.coerceAtMost(1f)

                        RuleOutput(urgency, moveDirection = moveDir, abilities = listOf(false, false, false))
                    } else RuleOutput(0f)

            ruleOutput
        }
        return avoidHolesRule
    }

    fun createApproachRule(maxDistance: Float = 1600f): Rule =
            { myChar, otherChar, staticState, _, _ ->
                val distToOtherChar = SolGameStateFuncs.distanceOuter(myChar.physicalObject, otherChar.physicalObject)
                val moveDir = distToOtherChar
                val urgency = MathFuncs.linearBetween(0f, maxDistance, distToOtherChar.length())
                RuleOutput(urgency, moveDir, listOf(false, false, false))
            }

    fun createRandomAttackRule(): Rule = { myChar, otherChar, staticState, _, _ ->
        val distToOtherChar = SolGameStateFuncs.distanceOuter(myChar.physicalObject, otherChar.physicalObject)
        val urgency = (500f / distToOtherChar.length().coerceAtLeast(0.01f)).coerceAtMost(1f)
        val shouldAttack = MathF.randInt(0, 20) == 0
        val abilities =
                if (shouldAttack) {
                    val abilityIndex = MathF.randInt(0, 2)
                    (0..2).map { it == abilityIndex }
                } else null

        RuleOutput(urgency, abilities = abilities)
    }

    fun createConfigBasedAttackRule(): Rule = { myChar, otherChar, staticState, myCharConfig, _ ->
        val vecToChar = SolGameStateFuncs.distanceOuter(myChar.physicalObject, otherChar.physicalObject)
        val distToChar = vecToChar.length()

        val abilityMaxReachOuter = { abConfig: AbilityConfig ->
            val initialReach = (abConfig.distanceFromChar - myChar.physicalObject.radius) + abConfig.radius
            val additionalReachOverTime = abConfig.speed * abConfig.activeTime / 60f
            initialReach + additionalReachOverTime
        }

        val abilitiesInRange: List<Boolean> = myCharConfig.abilities
                .map { abilityMaxReachOuter(it) }
                .map { distToChar - it }
                .map { it < 0f }

        val fuzzyAbilities = abilitiesInRange.map { if (MathF.random() < 0.01) !it else it }
        val anyAbilities = fuzzyAbilities.any { it }
        val shouldAttack = anyAbilities && MathF.random() > 0.9f
        val urgency = MathFuncs.linearBetween (2000f, 100f, distToChar)

        RuleOutput(
                urgency,
                abilities = if (shouldAttack) fuzzyAbilities else null
        )
    }

    fun createRetreatRule(minDistance: Float, maxDistance: Float): Rule = { myChar, otherChar, staticState, _, _ ->
        val distToOtherChar = SolGameStateFuncs.distanceOuter(myChar.physicalObject, otherChar.physicalObject)
        val moveDir = distToOtherChar.negate(Vector2f())
        val urgency = MathFuncs.linearBetween(maxDistance, minDistance, distToOtherChar.length())
        RuleOutput(urgency, moveDir)
    }

    fun createMoveRandomRule(): Rule {
        val velocity = Vector2f()
        return { myChar, otherChar, staticState, _, _ ->
            val moveDir = velocity.add(Vector2f(MathF.randRange(-0.5f, 0.5f), MathF.randRange(-0.5f, 0.5f))).normalize()
            RuleOutput(1f, moveDir)
        }
    }
}