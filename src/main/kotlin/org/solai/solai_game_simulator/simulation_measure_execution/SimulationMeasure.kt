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

class SimulationMeasure(
        val simulationId: String,
        private val simulationFactory: (characters: List<CharacterConfig>) -> Simulation,
        val characterConfigs: List<CharacterConfig>,
        private val metricNames: List<String>
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
        val simulation = simulationFactory(characterConfigs)

        simulation.setup()
        players.forEach { it.onSetup() }
        metrics.forEach { it.metric.setup() }

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
        players.forEachIndexed { index, player -> player.onStart(index, staticState, state) }
        metrics.forEach { it.metric.start(staticState, state) }

        while (!simulation.isFinished()) {
            simulation.update()
            state = simulation.getState()
            val inputs = players.mapIndexed { index, player -> player.onUpdate(index, state) }
            metrics.forEach { it.metric.update(state) }

            simulation.setInputs(inputs)
        }

        simulation.end()
        players.forEachIndexed { index, player ->  player.onEnd(index, state) }
        metrics.forEach { it.metric.end(state) }
    }

}