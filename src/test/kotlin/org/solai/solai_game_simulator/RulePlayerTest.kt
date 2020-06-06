package org.solai.solai_game_simulator

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.solai.solai_game_simulator.players.RulePlayer
import org.solai.solai_game_simulator.simulation.SolSimulation
import sol_game.CharacterConfigLoader
import sol_game.game_state.SolGameState
import java.util.*
import kotlin.collections.ArrayDeque

class RulePlayerTest {

    @Test
    fun testRulePlayer() {
        val charactersConfig = listOf(
                CharacterConfigLoader.fromResourceFile("shrankConfig.json"),
                CharacterConfigLoader.fromResourceFile("schmathiasConfig.json")
        )
        val sim = SolSimulation(charactersConfig)
        val players = listOf(
                RulePlayer(),
                RulePlayer()
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

        val stateDelay = 12

        val prevStates = LinkedList<SolGameState>()
        (0 until stateDelay).forEach { _ -> prevStates.add(initialSimState) }

        while (! sim.isFinished()) {
            val gameState = prevStates.pollLast()
            prevStates.addFirst(sim.getState())

            val actions = players.mapIndexed { index, player -> player.onUpdate(
                            controlledCharacterIndex = index,
                            gameState = gameState,
                            charactersConfig = charactersConfig
                    ) }
            sim.setInputs(actions)
            sim.update()
            Thread.sleep(8)
        }
    }
}