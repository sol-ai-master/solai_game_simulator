package org.solai.solai_game_simulator.web

import org.solai.solai_game_simulator.simulation_measure_execution.SolSimulationFactory
import org.solai.solai_game_simulator.simulation_measure_execution.SimulationMeasureExecutor
import org.solai.solai_game_simulator.simulation_measure_execution.SimulationQueueExecuter
import org.springframework.stereotype.Service


@Service
class SimulationMeasureService {

    final val simulationFactory = SolSimulationFactory()
    final val executor = SimulationMeasureExecutor(maxJobs = 50)
    final val queueExecutor = SimulationQueueExecuter(executor, simulationFactory)

    init {
        queueExecutor.start()
    }

}