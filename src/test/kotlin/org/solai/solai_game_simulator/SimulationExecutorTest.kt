package org.solai.solai_game_simulator



import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque


class SimulationExecutorTest {

    lateinit var simulationExecutor: SimulationExecutor

    @BeforeEach
    fun setUp() {
        simulationExecutor = SimulationExecutor()
    }

    @AfterEach
    fun tearDown() {
        simulationExecutor.terminate()
    }

    @Test
    fun testDummySimulation() {

        val finishedSimulations: Deque<Simulation> = ConcurrentLinkedDeque<Simulation>()


        simulationExecutor.onSimulationFinished { simulation ->
            finishedSimulations.addFirst(simulation)
        }

        val dummySims = (0..2)
                .map { DummySimulation("hei$it") }
                .onEach { simulationExecutor.executeSimulation(it) }

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