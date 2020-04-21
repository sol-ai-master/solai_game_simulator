package org.solai.solai_game_simulator.simulation_measure_execution

import mu.KotlinLogging
import org.solai.solai_game_simulator.simulation.Simulation
import org.solai.solai_game_simulator.metrics.CalculatedMetric
import org.solai.solai_game_simulator.players.Player
import org.solai.solai_game_simulator.players.RandomPlayer
import org.solai.solai_game_simulator.metrics.ExistingMetrics
import org.solai.solai_game_simulator.metrics.NamedMetric
import sol_game.core_game.CharacterConfig
import java.util.*
import kotlin.system.measureTimeMillis

class SimulationMeasure(
        val simulationId: String,
        private val simulationFactory: SimulationFactory,
        val characterConfigs: List<CharacterConfig>,
        private val metricNames: List<String>,
        private val updateDelayMillis: Float = 0f
) : Runnable {
    val logger = KotlinLogging.logger {  }


    private val metrics: List<NamedMetric>

    init {
        val unsynchronizedMetrics = metricNames.map { ExistingMetrics.getMetricInstance(it) }
                .onEach { it ?: logger.warn { "$it metric does not exist" } }
                .filterNotNull()
        metrics = Collections.synchronizedList(unsynchronizedMetrics)
    }

    /**
     * Should be called after execution to get a valid calculation
     * Intermediate calculations may be inaccurate or invalid
     */
    fun calculateMetrics(): List<CalculatedMetric> {
        return metrics.map { CalculatedMetric(it.name, it.metric.calculate()) }
    }

    override fun run() {
        val players = listOf(RandomPlayer(), RandomPlayer()).subList(0, characterConfigs.size)
        val simulation = simulationFactory.getSimulation(characterConfigs)

        players.forEach { it.onSetup() }

        executeSimulationWithMetrics(simulation, players, metrics)

        // metrics are updated and ready to be fetched
    }

    private fun executeSimulationWithMetrics(
            simulation: Simulation,
            players: List<Player>,
            metrics: List<NamedMetric>
    ) {
        simulation.start()

        val staticState = simulation.getStaticState()
        var state = simulation.getState()
        val playersCount = state.charactersState.size

        players.forEachIndexed { index, player -> player.onStart(index, staticState, state) }
        metrics.forEach { it.metric.start(playersCount, staticState, state) }


        while (!simulation.isFinished()) {
            val updateTime = measureTimeMillis {
                simulation.update()
                state = simulation.getState()
                val inputs = players.mapIndexed { index, player -> player.onUpdate(index, state) }
                metrics.forEach { it.metric.update(state) }

                simulation.setInputs(inputs)
            }

            val sleepTime = updateDelayMillis.toLong() - updateTime
            if (sleepTime > 0f) {
                Thread.sleep(sleepTime)
            }
        }

        simulation.end()
        players.forEachIndexed { index, player ->  player.onEnd(index, state) }
        metrics.forEach { it.metric.end(state) }
    }

}