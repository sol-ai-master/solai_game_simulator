package org.solai.solai_game_simulator.simulation_measure_execution

import mu.KotlinLogging
import org.solai.solai_game_simulator.character_queue.GameSimulationData
import org.solai.solai_game_simulator.character_queue.GameSimulationResult
import org.solai.solai_game_simulator.character_queue.SimulationQueue
import org.solai.solai_game_simulator.simulation.SolSimulation
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentHashMap




class SimulationQueueExecuter(
        val simulationsMeasureExecutor: SimulationMeasureExecutor
) : Thread() {

    val logger = KotlinLogging.logger {}

    private val simulationDataById = ConcurrentHashMap<String, GameSimulationData>()

    override fun run() {
        super.run()

        val pollSimulationQueue = SimulationQueue.getQueue("localhost") ?: run {
            throw IllegalStateException("Could not connect to simulation queue")
        }
        val pushSimulationQueue = SimulationQueue.getQueue("localhost") ?: run {
            throw IllegalStateException("Could not connect to simulation queue")
        }

        simulationsMeasureExecutor.onSimulationMeasureFinished { simulationMeasure ->
            val metricResults = simulationMeasure.calculateMetrics()
            val simulationData = simulationDataById.remove(simulationMeasure.simulationId)

            if (simulationData == null) {
                logger.error { "SimulationData did not exist for finished simulation measure: ${simulationMeasure.simulationId}" }
                return@onSimulationMeasureFinished
            }

            pushSimulationQueue.pushSimulationResult(GameSimulationResult(
                    simulationMeasure.simulationId,
                    simulationData,
                    metricResults.map { it.name to it.value }.toMap()
            ))
        }


        var shouldStop = false
        while (!shouldStop) {
            logger.info { "Waiting for simulation..." }
            val simulationData = pollSimulationQueue.waitSimulationData(60) ?: continue
            logger.info { "Simulation present: $simulationData" }

            // store this to be used in the simulation result
            simulationDataById[simulationData.simulationId] = simulationData

            val simulationMeasure = SimulationMeasure(
                    simulationData.simulationId,
                    simulationFactory = { charactersConfig -> SolSimulation(charactersConfig) },
                    characterConfigs = simulationData.charactersConfigs,
                    metricNames = simulationData.metrics
            )

            simulationsMeasureExecutor.execute(simulationMeasure)
        }
    }

}