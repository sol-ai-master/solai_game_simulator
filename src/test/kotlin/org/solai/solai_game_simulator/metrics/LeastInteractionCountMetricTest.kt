package org.solai.solai_game_simulator.metrics

import org.joml.Vector2f
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.solai.solai_game_simulator.players.helpers.PlayerUtils
import org.solai.solai_game_simulator.simulation.SolSimulation
import sol_game.CharacterConfigLoader
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameStateFuncs

class LeastInteractionCountMetricTest {

    fun moveCharactersUpUntilHole(sim: SolSimulation, callOnUpdate: ((SolSimulation) -> Unit)? = null) {
        while (true) {
            val state = sim.getState()
            val inputs = state.charactersState.map {
                if (SolGameStateFuncs.closestHole(it.physicalObject, state.staticGameState).length() > 300f) {
                    SolActions(mvUp = true)
                } else SolActions()
            }
            if (inputs == List(inputs.size) { SolActions() }) {
                break
            } else {
                sim.setInputs(inputs)
                sim.update()
                callOnUpdate?.invoke(sim)
            }
        }
    }

    fun moveCharactersClose(sim: SolSimulation, callOnUpdate: ((SolSimulation) -> Unit)? = null) {
        moveCharactersUpUntilHole(sim, callOnUpdate)

        while (true) {
            val state = sim.getState()
            val charsVec = SolGameStateFuncs.distanceOuter(state.charactersState[0].physicalObject,
                    state.charactersState[1].physicalObject)

            if (charsVec.length() < 16f) {
                break
            }

            val normCharsVec = if (charsVec.lengthSquared() == 0f) charsVec else charsVec.normalize(Vector2f())
            sim.setInputs(listOf(
                    PlayerUtils.directionToMoveInputAsAction(normCharsVec),
                    PlayerUtils.directionToMoveInputAsAction(normCharsVec.negate(Vector2f()))
            ))

            sim.update()
            callOnUpdate?.invoke(sim)
        }
    }

    fun hitAbility(
            charIndex: Int,
            abilityIndex: Int,
            sim: SolSimulation,
            callOnUpdate: ((SolSimulation) -> Unit)? = null
    ) {
        moveCharactersClose(sim, callOnUpdate)
        val state = sim.getState()
        // perform ability

        sim.setInputs(state.charactersState.indices.map { index ->
            if (charIndex == index) {
                val otherChar = state.charactersState[(index + 1) % state.charactersState.size]
                SolActions(
                        ability1 = abilityIndex == 0,
                        ability2 = abilityIndex == 1,
                        ability3 = abilityIndex == 2,
                        aimX = otherChar.physicalObject.position.x,
                        aimY = otherChar.physicalObject.position.y
                )
            }
            else SolActions()
        })

        sim.update()
        callOnUpdate?.invoke(sim)
        sim.setInputs(List(state.charactersState.size) { SolActions() })
        (0 until 200).forEach { _ ->
            sim.update()
            callOnUpdate?.invoke(sim)
        }
    }

    @Test
    fun testVisually() {
        val charactersConfig = listOf(
                CharacterConfigLoader.fromResourceFile("shrankConfig.json"),
                CharacterConfigLoader.fromResourceFile("schmathiasConfig.json")
        )
        val sim = SolSimulation(charactersConfig)
        sim.setup(false)
        sim.start()

        val leastInteractionCountMetric = LeastInteractionCountMetric()
        leastInteractionCountMetric.setup()
        leastInteractionCountMetric.start(2, sim.getState())

        hitAbility(0, 0, sim) { leastInteractionCountMetric.update(it.getState()) }
        hitAbility(0, 0, sim) { leastInteractionCountMetric.update(it.getState()) }
        hitAbility(0, 0, sim) { leastInteractionCountMetric.update(it.getState()) }

        hitAbility(0, 1, sim) { leastInteractionCountMetric.update(it.getState()) }

        hitAbility(0, 2, sim) { leastInteractionCountMetric.update(it.getState()) }
        hitAbility(0, 2, sim) { leastInteractionCountMetric.update(it.getState()) }

        hitAbility(1, 0, sim) { leastInteractionCountMetric.update(it.getState()) }
        hitAbility(1, 1, sim) { leastInteractionCountMetric.update(it.getState()) }

        println(leastInteractionCountMetric.charactersAbilitiesHitCount)

        val metricCalc = leastInteractionCountMetric.calculate()
        Assertions.assertEquals(1f, metricCalc[0], "least used ability of char0 should be used 1 time")
        Assertions.assertEquals(0f, metricCalc[1], "least used ability of char1 should be used 0 times")
    }

}