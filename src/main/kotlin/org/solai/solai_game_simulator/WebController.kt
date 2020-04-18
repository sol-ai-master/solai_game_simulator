package org.solai.solai_game_simulator

import org.solai.solai_game_simulator.character_queue.GameSimulationResult
import org.solai.solai_game_simulator.metrics.ExistingMetrics
import org.solai.solai_game_simulator.simulation_measure_execution.SimulationMeasureExecutor
import org.solai.solai_game_simulator.simulation_measure_execution.SimulationQueueExecuter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController()
class WebController {

    private val simulationsMeasureExecutor = SimulationMeasureExecutor()
    private val simulationQueueExecutor = SimulationQueueExecuter(simulationsMeasureExecutor)

    init {
        simulationQueueExecutor.start()
    }

    @GetMapping("/hello")
    fun hello() = "hello"

    @GetMapping("/runningSimulations")
    fun runningSimulations(): List<String> {
        return simulationsMeasureExecutor.getExecutionsSimulationIds()
    }

    @GetMapping("/existingMetrics")
    fun existingMetrics(): List<String> = ExistingMetrics.getAllMetricNames()

    @GetMapping("/intermediateResult/{simulationId}")
    fun intermediateResult(@PathVariable simulationId: String): GameSimulationResult? {
        val simMeasure = simulationsMeasureExecutor.getExecutingMeasure(simulationId)
        return simMeasure?.let { simulationQueueExecutor.simulationMeasureToResult(it) }
                ?: run { null }
    }

//    @DeleteMapping("/removeSimulation")
//    fun removeSimulation(): Boolean {
//
//    }
}