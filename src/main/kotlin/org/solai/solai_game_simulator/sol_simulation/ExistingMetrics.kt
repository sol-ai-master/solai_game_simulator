package org.solai.solai_game_simulator.sol_simulation

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

data class NamedMetric(
        val name: String,
        val metric: SolGameMetric
)

object ExistingMetrics {


    private val metricsByName = mapOf<String, KClass<out SolGameMetric>>(
            "gameLength" to GameLengthMetric::class
    )

    fun getMetricInstance(measureName: String): NamedMetric? {
        return metricsByName[measureName]?.let { NamedMetric(measureName, it.createInstance()) }
    }

}