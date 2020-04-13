package org.solai.solai_game_simulator

import org.junit.Assert
import org.solai.solai_game_simulator.character_queue.GameSimulationData
import sol_game.CharacterConfigLoader
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.test.Test

class SimulationsHandlerTest {

    @Test
    fun testSimulations() {

        val finishedSimulations: Deque<Simulation> = ConcurrentLinkedDeque()

        val simulationsHandler = SimulationsHandler()
        simulationsHandler.onSimulationFinished { simulation ->
            finishedSimulations.add(simulation)
        }

//        val charConfigs = listOf(
//                CharacterConfigLoader.fromResourceFile("frankCharacterConfig.json"),
//                CharacterConfigLoader.fromResourceFile("schmathiasCharacterConfig.json")
//        )
//        val simData = GameSimulationData(
//                UUID.randomUUID().toString(),
//                charConfigs,
//                listOf("m1", "m2", "m3")
//        )
//        val sim1 = SolSimulation(simData)

        val dummySim = simulationsHandler.submit(DummySimulation("hei"))

        while (finishedSimulations.size < 1) {
            Thread.sleep(100)
        }

        Assert.assertEquals(finishedSimulations.first, dummySim)
    }

}