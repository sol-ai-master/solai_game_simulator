package org.solai.solai_game_simulator

import org.solai.solai_game_simulator.simulation_measure_execution.SimulationMeasureExecutor
import org.solai.solai_game_simulator.simulation_measure_execution.SimulationQueueExecuter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController()
class WebController {

    private val simulationsExecutor = SimulationMeasureExecutor()
    private val simulationQueueExecutor = SimulationQueueExecuter(simulationsExecutor)

    init {
        simulationQueueExecutor.start()
    }

    @GetMapping("/hello")
    fun hello() = "hello"

    @GetMapping("/runningSimulations")
    fun runningSimulations(): List<String> {
        return simulationsExecutor.getExecutionsSimulationIds()
    }

//    @DeleteMapping("/removeSimulation")
//    fun removeSimulation(): Boolean {
//
//    }
}