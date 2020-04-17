package org.solai.solai_game_simulator

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController()
class Controller {

    private val simulationsExecutor = SimulationExecutor()
    private val simulationQueueExecutor = SimulationQueueExecuter(simulationsExecutor)

    init {
        simulationQueueExecutor.start()
    }

    @GetMapping("/hello")
    fun hello() = "hello"

    @GetMapping("/runningSimulations")
    fun runningSimulations(): List<String> {
        return simulationsExecutor.getExecutingSimulationsIds()
    }

//    @DeleteMapping("/removeSimulation")
//    fun removeSimulation(): Boolean {
//
//    }
}