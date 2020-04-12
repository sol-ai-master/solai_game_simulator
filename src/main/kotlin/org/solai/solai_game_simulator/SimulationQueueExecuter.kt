package org.solai.solai_game_simulator

import mu.KotlinLogging
import org.solai.solai_game_simulator.character_queue.RedisSimulationQueue
import org.solai.solai_game_simulator.character_queue.SimulationQueue
import java.lang.IllegalStateException


private val logger = KotlinLogging.logger {}

class SimulationQueueExecuter(
        val gameServerPool: GameServerPool
) : Thread() {

    override fun run() {
        super.run()

        val simulationQueue = SimulationQueue.getQueue("localhost") ?: run {
            throw IllegalStateException("Could not connect to simulation queue")
        }
        val simulator = Simulator()

        simulator.onSimulationResult { result ->
            simulationQueue.pushSimulationResult(result)
        }

        var shouldStop = false
        while (!shouldStop) {
            logger.info { "Waiting for simulation..." }
            val simulationData = simulationQueue.waitSimulationData(60) ?: continue
            logger.info { "Simulation present: $simulationData" }
            simulator.simulate(
                    gameServerPool,
                    simulationData
            )
        }
    }

}