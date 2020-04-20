package org.solai.solai_game_simulator.simulation_measure_execution

import mu.KotlinLogging
import org.solai.solai_game_simulator.character_queue.GameSimulationData
import org.solai.solai_game_simulator.character_queue.GameSimulationResult
import org.solai.solai_game_simulator.character_queue.SimulationQueue
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentHashMap




class SimulationQueueExecuter(
        val simulationsMeasureExecutor: SimulationMeasureExecutor,
        val simulationFactory: SimulationFactory
) : Thread() {

    val logger = KotlinLogging.logger {}

    private val simulationDataById = ConcurrentHashMap<String, GameSimulationData>()

    var simulationUpdateDelayMillis: Float = 0f
            @Synchronized get
            @Synchronized set


    fun simulationMeasureToResult(simulationMeasure: SimulationMeasure): GameSimulationResult? {
        val metricResults = simulationMeasure.calculateMetrics()
        val simulationData = simulationDataById[simulationMeasure.simulationId] ?: return null

        return GameSimulationResult(
                simulationMeasure.simulationId,
                simulationData,
                metricResults.map { it.name to it.values }.toMap()
        )
    }

    override fun run() {
        super.run()

        val pollSimulationQueue = SimulationQueue.getQueue("localhost") ?: run {
            throw IllegalStateException("Could not connect to simulation queue")
        }
        val pushSimulationQueue = SimulationQueue.getQueue("localhost") ?: run {
            throw IllegalStateException("Could not connect to simulation queue")
        }

        simulationsMeasureExecutor.onSimulationMeasureFinished { simulationMeasure ->
            simulationMeasureToResult(simulationMeasure)
                    ?.let {
                        pushSimulationQueue.pushSimulationResult(it)
                        simulationDataById.remove(it.simulationId)
                    }
                    ?: run { logger.error { "SimulationData did not exist for finished simulation measure:${simulationMeasure.simulationId}" } }
        }


        var shouldStop = false
        while (!shouldStop) {
            if (simulationsMeasureExecutor.isFull()) {
                sleep(10)
                continue
            }

            logger.info { "Waiting for simulation..." }
            val simulationData = pollSimulationQueue.waitSimulationData(60) ?: continue
            logger.info { "Simulation present: $simulationData" }

            // store this to be used in the simulation result
            simulationDataById[simulationData.simulationId] = simulationData

            val simulationMeasure = SimulationMeasure(
                    simulationData.simulationId,
                    simulationFactory = simulationFactory,
                    characterConfigs = simulationData.charactersConfigs,
                    metricNames = simulationData.metrics,
                    updateDelayMillis = simulationUpdateDelayMillis
            )

            simulationsMeasureExecutor.execute(simulationMeasure)
        }
    }

}