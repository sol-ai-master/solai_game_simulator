package org.solai.solai_game_simulator



import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.solai.solai_game_simulator.simulator_core.Simulation
import org.solai.solai_game_simulator.simulation_measure_execution.SimulationFactory
import org.solai.solai_game_simulator.simulation_measure_execution.SimulationMeasure
import org.solai.solai_game_simulator.simulation_measure_execution.SimulationMeasureExecutor
import sol_game.core_game.CharacterConfig
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque


class SimulationMeasureExecutorTest {

    lateinit var simulationMeasureExecutor: SimulationMeasureExecutor

    @BeforeEach
    fun setUp() {
        simulationMeasureExecutor = SimulationMeasureExecutor()
    }

    @AfterEach
    fun tearDown() {
        simulationMeasureExecutor.terminate()
    }

    @Test
    fun testDummySimulation() {

        val finishedSimulations: Deque<SimulationMeasure> = ConcurrentLinkedDeque<SimulationMeasure>()


        simulationMeasureExecutor.onSimulationMeasureFinished { simulation ->
            finishedSimulations.addFirst(simulation)
        }

        val simulationFactory = object : SimulationFactory {
            override fun getSimulation(charactersConfig: List<CharacterConfig>): Simulation {
                return FixedIterationsSimulation(100)
            }
        }

        val dummySimMeasures: List<SimulationMeasure> = (0..2)
                .map {
                    SimulationMeasure(
                            "$it",
                            simulationFactory,
                            listOf(),
                            listOf()
                    )
                }

        dummySimMeasures.forEach { simulationMeasureExecutor.execute(it) }

        while (finishedSimulations.size < 1) {
            Thread.sleep(100)
        }

        assertEquals(3, finishedSimulations.size)
        dummySimMeasures.forEach { assertTrue(finishedSimulations.contains(it)) }
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