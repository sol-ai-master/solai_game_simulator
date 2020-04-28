package org.solai.solai_game_simulator.metrics

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

data class NamedMetric(
        val name: String,
        val metric: Metric
)

data class CalculatedMetric(
        val name: String,
        val values: List<Float>
)

object ExistingMetrics {


    private val metricsByName = ConcurrentHashMap(mapOf(
            "gameLength" to GameLengthMetric::class,
            "nearDeathFrames" to NearDeathFramesMetric::class,
            "characterWon" to CharacterWonMetric::class,
            "stageCoverage" to StageCoverageMetric::class,
            "leadChange" to LeadChangeMetric::class,
            "accumulatedStateEvaluations" to StateEvaluationsAccumulatedMetric::class
    ))

    fun getMetricInstance(measureName: String): NamedMetric? {
        return metricsByName[measureName]?.let {
            val metric = NamedMetric(measureName, it.createInstance())
            metric.metric.setup()
            metric
        }
    }

    fun getAllMetricNames() = metricsByName.keys.toList()

}