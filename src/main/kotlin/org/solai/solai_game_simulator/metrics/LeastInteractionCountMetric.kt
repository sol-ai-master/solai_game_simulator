package org.solai.solai_game_simulator.metrics

import org.solai.solai_game_simulator.simulator_core.Metric
import sol_game.game_state.HitboxHitState
import sol_game.game_state.SolGameState
import java.lang.IllegalStateException

class LeastInteractionCountMetric : Metric {

    var charactersAbilitiesHitCount: List<MutableMap<String, Int>>? = null

    override fun start(playersCount: Int, gameState: SolGameState) {
        charactersAbilitiesHitCount = gameState.charactersState.map { char ->
            char.abilities.map { ab -> ab.name to 0 }.toMap().toMutableMap()
        }
    }

    val prevHits = mutableSetOf<List<HitboxHitState>>()

    override fun update(gameState: SolGameState) {
        gameState.charactersState.forEachIndexed { charIndex, char ->
            val charAbilitiesHitCount = charactersAbilitiesHitCount!![charIndex]
            char.currentHitboxes
                    .filter { hitbox ->
                        hitbox.hitsGivenNow.isNotEmpty()
                    }
                    .forEach { hitbox ->
                        if (prevHits.contains(hitbox.hitsGivenNow)) {
                            println("Repeated hitbox")
                        }
                        prevHits.add(hitbox.hitsGivenNow)
                        charAbilitiesHitCount.compute(hitbox.entityName) { _, value ->
                            value
                                    ?.let { it + 1 }
                                    ?: throw IllegalStateException("State contained an ability name that is not listed in characters abilities")
                        }
                    }
        }
    }

    override fun calculate(): List<Float> {
        return charactersAbilitiesHitCount
                ?.map {abilitiesCount ->
                    abilitiesCount.values.min()!!.toFloat()
                }
                ?: listOf(0f)
    }

}