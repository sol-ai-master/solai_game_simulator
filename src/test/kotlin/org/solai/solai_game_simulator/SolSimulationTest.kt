package org.solai.solai_game_simulator

import org.junit.jupiter.api.Test
import org.solai.solai_game_simulator.character_queue.GameSimulationData
import sol_game.CharacterConfigLoader
import java.util.*
import org.junit.jupiter.api.Assertions.*
import org.solai.solai_game_simulator.sol_simulation.SolSimulation

class SolSimulationTest {

    @Test
    fun testSolSimulation() {

        val charConfigs = listOf(
                CharacterConfigLoader.fromResourceFile("frankCharacterConfig.json"),
                CharacterConfigLoader.fromResourceFile("schmathiasCharacterConfig.json")
        )
        val simData = GameSimulationData(
                UUID.randomUUID().toString(),
                charConfigs,
                listOf("gameLength")
        )
        val sim1 = SolSimulation(simData)

        val simThread = Thread(sim1)

        simThread.start()
//        fail<Any>(":(")
        simThread.join()

        println("metrics: ${sim1.calculateMetrics()}")
        assertFalse(simThread.isAlive)
    }
}