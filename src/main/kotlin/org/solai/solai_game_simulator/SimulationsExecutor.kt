package org.solai.solai_game_simulator

import mu.KotlinLogging
import java.util.*
import java.util.concurrent.*
import kotlin.collections.LinkedHashMap


typealias SimulationFinishedListener = (simulation: Simulation) -> Unit


class SimulationExecutor {
    val logger = KotlinLogging.logger {  }

    private val executingSimulations: MutableMap<String, Simulation> = Collections.synchronizedMap(LinkedHashMap())

    private val threadExecutor = object : ThreadPoolExecutor(
            4,  // corePoolSize
            Integer.MAX_VALUE,  // maximumPoolSize
            30L,  // keepAliveTime
            TimeUnit.SECONDS,  // unit
            SynchronousQueue<Runnable>()  // workQueue, a SynchronousQueue will always create a new thread for a simulation
    ) {
        override fun afterExecute(r: Runnable?, t: Throwable?) {
            super.afterExecute(r, t)
            val simulation = r as Simulation
            handleFinishedSimulation(simulation)
        }
    }

    private var simulationFinishedListener: SimulationFinishedListener = {}

    fun executeSimulation(simulation: Simulation) {
        logger.info { "executing simulation: ${simulation.simulationId}" }
        threadExecutor.execute(simulation)
        executingSimulations[simulation.simulationId] = simulation
    }

    private fun handleFinishedSimulation(simulation: Simulation) {
        logger.info { "Simulation execution finished for: ${simulation.simulationId}" }
        executingSimulations.remove(simulation.simulationId)
        simulationFinishedListener.invoke(simulation)
    }

    fun onSimulationFinished(listener: SimulationFinishedListener) {
        simulationFinishedListener = listener
    }

    fun getSimulationsCount(): Int {
        return executingSimulations.size
    }

    fun getExecutingSimulationsIds(): List<String> {
        return executingSimulations.keys.toList()
    }

    fun terminate() {
        //        executingSimulations.forEach(sim -> sim.terminate())
        threadExecutor.shutdownNow()
    }
}