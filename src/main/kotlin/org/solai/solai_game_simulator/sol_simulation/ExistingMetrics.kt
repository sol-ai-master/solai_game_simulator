package org.solai.solai_game_simulator.sol_simulation

object ExistingMetrics {

    private val existingMetrics = listOf<SolGameMetric>(
            GameLengthMetric()
    )

    private val measuresByName = existingMetrics
            .map { it.metricName to it }
            .toMap()

    fun getMeasure(measureName: String): SolGameMetric? {
        return measuresByName[measureName]
    }

}