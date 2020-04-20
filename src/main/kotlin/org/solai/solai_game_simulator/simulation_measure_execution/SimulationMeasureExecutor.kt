package org.solai.solai_game_simulator.simulation_measure_execution

import mu.KotlinLogging
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.*
import kotlin.collections.LinkedHashMap


typealias MeasureFinishedListener = (measure: SimulationMeasure) -> Unit


class SimulationMeasureExecutor(
        val maxJobs: Int = 8
) {
    val logger = KotlinLogging.logger {  }

    private val executingMeasures: MutableMap<String, SimulationMeasure> = Collections.synchronizedMap(LinkedHashMap())

    private val threadExecutor = object : ThreadPoolExecutor(
            4,  // corePoolSize
            Integer.MAX_VALUE,  // maximumPoolSize
            30L,  // keepAliveTime
            TimeUnit.SECONDS,  // unit
            SynchronousQueue<Runnable>()  // workQueue, a SynchronousQueue will always create a new thread for a simulation
    ) {
        override fun afterExecute(r: Runnable?, t: Throwable?) {
            super.afterExecute(r, t)
            val simulationMeasure = r as SimulationMeasure
            handleFinishedSimulation(simulationMeasure)

            t?.let { error(t) }
        }
    }

    private var measureFinishedListener: MeasureFinishedListener = {}

    fun isFull(): Boolean = getExecutionsCount() == maxJobs

    fun execute(simulationMeasure: SimulationMeasure) {
        if (isFull()) throw IllegalStateException("full, check if full before executing")

        logger.info { "executing simulation measure: ${simulationMeasure.simulationId}" }
        executingMeasures[simulationMeasure.simulationId] = simulationMeasure
        threadExecutor.execute(simulationMeasure)
    }

    private fun handleFinishedSimulation(simulationMeasure: SimulationMeasure) {
        logger.info { "Simulation measure execution finished for: ${simulationMeasure.simulationId}" }
        executingMeasures.remove(simulationMeasure.simulationId)
        measureFinishedListener.invoke(simulationMeasure)
    }

    fun onSimulationMeasureFinished(listener: MeasureFinishedListener) {
        measureFinishedListener = listener
    }

    fun getExecutionsCount(): Int {
        return executingMeasures.size
    }

    fun getExecutionsSimulationIds(): List<String> {
        return executingMeasures.keys.toList()
    }

    fun getExecutingMeasures() = executingMeasures.values.toList()

    fun getExecutingMeasure(simulationId: String): SimulationMeasure? = executingMeasures[simulationId]

    fun terminate() {
        //        executingSimulations.forEach(sim -> sim.terminate())
        threadExecutor.shutdownNow()
    }
}