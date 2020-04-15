package org.solai.solai_game_simulator

import mu.KotlinLogging
import org.solai.solai_game_simulator.character_queue.SimulationQueue
import org.solai.solai_game_simulator.sol_simulation.SolSimulation
import java.lang.IllegalStateException


private val logger = KotlinLogging.logger {}

class SimulationQueueExecuter(
        val simulationsExecutor: SimulationExecutor
) : Thread() {

    override fun run() {
        super.run()

        val pollSimulationQueue = SimulationQueue.getQueue("localhost") ?: run {
            throw IllegalStateException("Could not connect to simulation queue")
        }
        val pushSimulationQueue = SimulationQueue.getQueue("localhost") ?: run {
            throw IllegalStateException("Could not connect to simulation queue")
        }

        simulationsExecutor.onSimulationFinished { simulation ->
            val result = simulation.calculateResult()
            pushSimulationQueue.pushSimulationResult(result)
        }


        var shouldStop = false
        while (!shouldStop) {
            logger.info { "Waiting for simulation..." }
            val simulationData = pollSimulationQueue.waitSimulationData(60) ?: continue
            logger.info { "Simulation present: $simulationData" }
            simulationsExecutor.executeSimulation(
                    SolSimulation(simulationData)
            )
        }
    }

}