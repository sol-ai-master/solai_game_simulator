package org.solai.solai_game_simulator



import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque


class SimulationsHandlerTest {

    lateinit var simulationsHandler: SimulationsHandler

    @BeforeEach
    fun setUp() {
        simulationsHandler = SimulationsHandler()
    }

    @AfterEach
    fun tearDown() {
        simulationsHandler.terminate()
    }

    @Test
    fun testDummySimulation() {

        val finishedSimulations: Deque<Simulation> = ConcurrentLinkedDeque<Simulation>()


        simulationsHandler.onSimulationFinished { simulation ->
            finishedSimulations.addFirst(simulation)
        }

        val dummySims = (0..2)
                .map { DummySimulation("hei$it") }
                .onEach { simulationsHandler.performSimulation(it) }

        while (finishedSimulations.size < 1) {
            Thread.sleep(100)
        }

        assertEquals(3, finishedSimulations.size)
        dummySims.forEach { assertTrue(finishedSimulations.contains(it)) }

    }

    @Test
    fun testSolSimulation() {
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
    }
}