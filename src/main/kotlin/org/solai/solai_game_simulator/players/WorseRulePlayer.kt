package org.solai.solai_game_simulator.players

import org.solai.solai_game_simulator.players.helpers.PlayerRules
import org.solai.solai_game_simulator.players.helpers.PlayerUtils
import org.solai.solai_game_simulator.players.helpers.RulesCalculator
import org.solai.solai_game_simulator.simulator_core.Player
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameState

class WorseRulePlayer : Player {

    val rulesCalculator = RulesCalculator(
            mapOf(
                    3.1f to PlayerRules.createAvoidHolesRule(300f, 20f),
                    0.6f to PlayerRules.createRetreatRule(0f, 300f),
//                    1.2f to PlayerRules.createConfigBasedAttackRule(),
                    1.2f to PlayerRules.createRandomAttackRule(),
                    1.4f to PlayerRules.createApproachRule(1600f),
                    1f to PlayerRules.createMoveRandomRule(),
                    2.7f to PlayerRules.createAvoidHitboxesRule(100f)
            ).map { it.key * 0.1f to it.value }.toMap()

    )

    override fun onUpdate(controlledCharacterIndex: Int, gameState: SolGameState, charactersConfig: List<CharacterConfig>): SolActions {
        val actions = rulesCalculator.calculateActions(controlledCharacterIndex, gameState, charactersConfig)
        val fuzzyActions = PlayerUtils.applyFuzziness(actions,
                aimVariance = 30f,
                movementFuzzyness = 0.1f,
                abilityFuzzyness = 0f //0.001f
        )
        return fuzzyActions
    }
}