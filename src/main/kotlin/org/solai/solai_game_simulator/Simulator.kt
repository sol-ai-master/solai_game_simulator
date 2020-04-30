package org.solai.solai_game_simulator

import org.solai.solai_game_simulator.simulation_measure_execution.SimulationMeasureExecutor
import org.solai.solai_game_simulator.simulation_measure_execution.SimulationQueueExecuter
import org.solai.solai_game_simulator.simulation_measure_execution.SolSimulationFactory

data class SimulatorConfig(
        val queueAddress: String = "localhost",
        val queuePort: Int = -1,  // default port
        val headlessSimulations: Boolean = true,
        val maxParallelJobs: Int = 50
)

class Simulator(
    val config: SimulatorConfig
) {
    val simulationFactory = SolSimulationFactory(headless = config.headlessSimulations)
    val executor = SimulationMeasureExecutor(maxJobs = config.maxParallelJobs)
    val queueExecutor = SimulationQueueExecuter(executor, simulationFactory, config.queueAddress, config.queuePort)

    fun start() {
        queueExecutor.start()
    }
}