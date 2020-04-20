package org.solai.solai_game_simulator.simulation_measure_execution

import mu.KotlinLogging
import org.solai.solai_game_simulator.simulation.Simulation
import org.solai.solai_game_simulator.metrics.CalculatedMetric
import org.solai.solai_game_simulator.players.Player
import org.solai.solai_game_simulator.players.RandomPlayer
import org.solai.solai_game_simulator.metrics.ExistingMetrics
import org.solai.solai_game_simulator.metrics.NamedMetric
import sol_game.core_game.CharacterConfig
import sol_game.game_state.SolCharacterState
import sol_game.game_state.SolGameState
import java.util.*

class SimulationMeasure(
        val simulationId: String,
        private val simulationFactory: SimulationFactory,
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
        var prevState = state
        var prevPrevState = prevState
        val playersCount = state.charactersState.size

        players.forEachIndexed { index, player -> player.onStart(index, staticState, state) }
        metrics.forEach { it.metric.start(playersCount, staticState, state) }

        var i = 0
        while (!simulation.isFinished()) {
            simulation.update()
            prevPrevState = prevState
            prevState = state
            state = simulation.getState()
            val inputs = players.mapIndexed { index, player -> player.onUpdate(index, state) }
            metrics.forEach { it.metric.update(state) }

            simulation.setInputs(inputs)
            i++
        }

        val getPrintState = {solState: SolGameState -> mapOf(
                "gameStarted" to solState.gameStarted,
                "gameEnded" to solState.gameEnded,
                "indexWOn" to solState.playerIndexWon,
                "charStates" to solState.charactersState.map { charState -> mapOf(
                        "object" to charState.physicalObject,
                        "vel" to charState.velocity,
                        "stocks" to charState.stocks,
                        "damage" to charState.damage
                ) }
        )}

        println("end prev prev state: ${getPrintState(prevPrevState)}")
        println("end prev state: ${getPrintState(prevState)}")
        println("end state: ${getPrintState(state)}")

        println("simulation ended in $i updates")
        simulation.end()
        players.forEachIndexed { index, player ->  player.onEnd(index, state) }
        metrics.forEach { it.metric.end(state) }
    }

}