package org.solai.solai_game_simulator.simulation_measure_execution

import mu.KotlinLogging
import org.solai.solai_game_simulator.simulator_core.Simulation
import org.solai.solai_game_simulator.metrics.CalculatedMetric
import org.solai.solai_game_simulator.simulator_core.Player
import org.solai.solai_game_simulator.metrics.ExistingMetrics
import org.solai.solai_game_simulator.metrics.NamedMetric
import org.solai.solai_game_simulator.players.RulePlayer
import sol_game.core_game.CharacterConfig
import sol_game.game_state.SolGameState
import java.util.*
import kotlin.system.measureTimeMillis

class SimulationMeasure(
        val simulationId: String,
        private val simulationFactory: SimulationFactory,
        val characterConfigs: List<CharacterConfig>,
        private val metricNames: List<String>,
        val maxSimulationUpdates: Int = 54000,  // 15 minutes at 60 updates per second
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
        val players = listOf(RulePlayer(), RulePlayer()).subList(0, characterConfigs.size)
        val simulation = simulationFactory.getSimulation(characterConfigs)

        players.forEach { it.onSetup() }

        executeSimulationWithMetrics(simulation, characterConfigs, players, metrics, maxSimulationUpdates)

        // metrics are updated and ready to be fetched
    }

    private fun executeSimulationWithMetrics(
            simulation: Simulation,
            charactersConfig: List<CharacterConfig>,
            players: List<Player>,
            metrics: List<NamedMetric>,
            maxUpdates: Int
    ) {
        simulation.start()

        var actualState = simulation.getState()
        val playersCount = actualState.charactersState.size

        players.forEachIndexed { index, player -> player.onStart(index, actualState, charactersConfig) }
        metrics.forEach { it.metric.start(playersCount, actualState) }

        val stateDelay = 12  // frames
        val prevStates = LinkedList<SolGameState>()
        (0 until stateDelay).forEach { _ -> prevStates.add(actualState) }

        var updateCount = 0
        while (updateCount++ < maxUpdates && !simulation.isFinished()) {
            val updateTime = measureTimeMillis {

                actualState = simulation.getState()
                val playerPerceivedState = prevStates.pollLast()
                prevStates.addFirst(actualState)

                val inputs = players.mapIndexed { index, player ->
                    player.onUpdate(index, playerPerceivedState, charactersConfig)
                }
                metrics.forEach { it.metric.update(actualState) }

                simulation.setInputs(inputs)
                simulation.update()
            }

            val sleepTime = updateDelayMillis.toLong() - updateTime
            if (sleepTime > 0f) {
                Thread.sleep(sleepTime)
            }
        }

        simulation.end()
        players.forEachIndexed { index, player ->  player.onEnd(index, actualState, charactersConfig) }
        metrics.forEach { it.metric.end(actualState) }
    }

}