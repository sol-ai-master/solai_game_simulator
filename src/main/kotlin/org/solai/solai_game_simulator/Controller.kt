package org.solai.solai_game_simulator

import org.solai.solai_game_simulator.character_queue.GameSimulationData
import org.solai.solai_game_simulator.character_queue.SimulationQueue
import org.springframework.web.bind.annotation.*
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.CharacterConfigLoader
import java.lang.IllegalStateException
import java.util.*


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