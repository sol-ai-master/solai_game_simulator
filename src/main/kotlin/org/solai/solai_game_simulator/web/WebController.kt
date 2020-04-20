package org.solai.solai_game_simulator.web

import org.solai.solai_game_simulator.character_queue.GameSimulationResult
import org.solai.solai_game_simulator.metrics.ExistingMetrics
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController()
class WebController {

    @Autowired
    lateinit var simulationMeasuer: SimulationMeasureService

    @GetMapping("/hello")
    fun hello() = "hello"

    @GetMapping("/runningSimulations")
    fun runningSimulations(): List<String> {
        return simulationMeasuer.executor.getExecutionsSimulationIds()
    }

    @GetMapping("/existingMetrics")
    fun existingMetrics(): List<String> = ExistingMetrics.getAllMetricNames()

    @GetMapping("/intermediateResult/{simulationId}")
    fun intermediateResult(@PathVariable simulationId: String): GameSimulationResult? {
        val simMeasure = simulationMeasuer.executor.getExecutingMeasure(simulationId)
        return simMeasure?.let { simulationMeasuer.queueExecutor.simulationMeasureToResult(it) }
                ?: run { null }
    }

    @PostMapping("/headlessSimulations")
    fun headlessSimulations(@RequestBody headless: Boolean): Boolean {
        return simulationMeasuer.simulationFactory.let {
            it.headless = headless
            it.headless
        }
    }

//    @DeleteMapping("/removeSimulation")
//    fun removeSimulation(): Boolean {
//
//    }
}