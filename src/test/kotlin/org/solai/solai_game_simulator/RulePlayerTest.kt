package org.solai.solai_game_simulator

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.solai.solai_game_simulator.players.RulePlayer
import org.solai.solai_game_simulator.simulation.SolSimulation
import sol_game.CharacterConfigLoader

class RulePlayerTest {

    @Test
    fun testRulePlayer() {
        val charactersConfig = listOf(
                CharacterConfigLoader.fromResourceFile("shrankConfig.json"),
                CharacterConfigLoader.fromResourceFile("schmathiasConfig.json")
        )
        val sim = SolSimulation(charactersConfig)
        val players = listOf(
                RulePlayer(0.1f),
                RulePlayer(5f)
        )

        sim.setup(headless = false)
        sim.start()
        sim.update()

        val initialSimState = sim.getState()
        players.forEach { it.onSetup() }
        players.forEachIndexed { index, player -> player.onStart(
                controlledCharacterIndex = index,
                gameState = initialSimState,
                charactersConfig = charactersConfig
        ) }

        while (! sim.isFinished()) {
            val gameState = sim.getState()
            val actions = players.mapIndexed { index, player -> player.onUpdate(
                            controlledCharacterIndex = index,
                            gameState = gameState,
                            charactersConfig = charactersConfig
                    ) }
            sim.setInputs(actions)
            sim.update()
            Thread.sleep(16)
        }
    }
}