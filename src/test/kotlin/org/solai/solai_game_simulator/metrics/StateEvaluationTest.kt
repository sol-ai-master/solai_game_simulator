package org.solai.solai_game_simulator.metrics

import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.solai.solai_game_simulator.players.RulePlayer
import org.solai.solai_game_simulator.simulation.SolSimulation
import sol_game.CharacterConfigLoader

class StateEvaluationTest {

    val logger = KotlinLogging.logger {  }

    @Test
    fun testStateEvaluation() {
        val charactersConfig = listOf(
                CharacterConfigLoader.fromResourceFile("shrankConfig.json"),
                CharacterConfigLoader.fromResourceFile("schmathiasConfig.json")
        )


        val sim = SolSimulation(charactersConfig)

        sim.setup(headless = false)
        sim.start()

        val players = listOf(RulePlayer(), RulePlayer())
                .onEach { it.onSetup() }

        players.forEachIndexed{ index, it -> it.onStart(index, sim.getState(), charactersConfig) }

        var frames = 60*3
        while (!sim.isFinished() || frames-- == 0) {
            sim.update()
            val state = sim.getState()
            sim.setInputs(players.mapIndexed {index, it -> it.onUpdate(index, state, charactersConfig)})
            val evaluations = StateEvaluation.evaluate(state)
            val bestCharIndex = evaluations.indices.maxBy { evaluations[it] }!!
            println("Best character: $bestCharIndex State evaluations: $evaluations")

            Thread.sleep(100)
        }

        sim.end()
    }
}