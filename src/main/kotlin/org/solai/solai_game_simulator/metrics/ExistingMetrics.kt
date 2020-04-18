package org.solai.solai_game_simulator.metrics

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

data class NamedMetric(
        val name: String,
        val metric: Metric
)

data class CalculatedMetric(
        val name: String,
        val value: Float
)

object ExistingMetrics {


    private val metricsByName = mapOf<String, KClass<out Metric>>(
            "gameLength" to GameLengthMetric::class
    )

    fun getMetricInstance(measureName: String): NamedMetric? {
        return metricsByName[measureName]?.let { NamedMetric(measureName, it.createInstance()) }
    }

}