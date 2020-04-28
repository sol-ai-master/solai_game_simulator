package org.solai.solai_game_simulator.metrics

import org.junit.jupiter.api.Test
import org.solai.solai_game_simulator.simulation_measure_execution.SimulationMeasure
import org.solai.solai_game_simulator.simulation_measure_execution.SolSimulationFactory
import sol_game.CharacterConfigLoader

class LeadChangeMetricTest {

    @Test
    fun testLeadChangeMetric() {
        val charactersConfig = listOf(
                CharacterConfigLoader.fromResourceFile("shrankConfig.json"),
                CharacterConfigLoader.fromResourceFile("schmathiasConfig.json")
        )

        val simulationMeasure = SimulationMeasure(
                "test id",
                SolSimulationFactory(headless=false),
                charactersConfig,
                listOf("leadChange"),
                updateDelayMillis = 16f
        )

        simulationMeasure.run()
        val leadChanges = simulationMeasure.calculateMetrics()
        println("Lead changes: $leadChanges")
    }
}