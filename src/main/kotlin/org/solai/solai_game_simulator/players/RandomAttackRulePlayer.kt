package org.solai.solai_game_simulator.players

import org.solai.solai_game_simulator.players.helpers.PlayerRules
import org.solai.solai_game_simulator.players.helpers.RulesCalculator
import org.solai.solai_game_simulator.simulator_core.Player
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameState

class RandomAttackRulePlayer : Player {

    val rulesCalculator = RulesCalculator(mapOf(
            1f to PlayerRules.createAvoidHolesRule(200f, 20f),
            0.2f to PlayerRules.createRetreatRule(0f, 300f),
            0.7f to PlayerRules.createRandomAttackRule(),
            0.4f to PlayerRules.createApproachRule(1600f),
            0.6f to PlayerRules.createMoveRandomRule()
    ))

    override fun onUpdate(controlledCharacterIndex: Int, gameState: SolGameState, charactersConfig: List<CharacterConfig>): SolActions {
        return rulesCalculator.calculateActions(controlledCharacterIndex, gameState, charactersConfig)
    }
}