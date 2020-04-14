package org.solai.solai_game_simulator

import java.util.*
import java.util.concurrent.*
import kotlin.collections.LinkedHashMap


typealias SimulationFinishedListener = (simulation: Simulation) -> Unit

class SimulationsHandler {

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
            simulationFinishedListener.invoke(simulation)
        }
    }

    private var simulationFinishedListener: SimulationFinishedListener = {}

    fun performSimulation(simulation: Simulation) {
        threadExecutor.execute(simulation)
        executingSimulations[simulation.simulationId] = simulation
    }

    fun onSimulationFinished(listener: SimulationFinishedListener) {
        simulationFinishedListener = listener
    }

    fun terminate() {
        //        executingSimulations.forEach(sim -> sim.terminate())
        threadExecutor.shutdownNow()
    }
}