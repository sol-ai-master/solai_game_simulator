package org.solai.solai_game_simulator.metrics

import org.solai.solai_game_simulator.simulator_core.Metric
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

data class DescribedMetric(
        val name: String,
        val description: String,
        val metric: KClass<out Metric>
)

data class MetricDescription(
        val name: String,
        val description: String
)

object ExistingMetrics {

    val existingMetrics: Collection<DescribedMetric> = listOf(
            DescribedMetric(
                    "gameLength",
                    "The length of the simulated game",
                    GameLengthMetric::class
            ),
            DescribedMetric(
                    "nearDeathFrames",
                    "Amount of frames where the character is closer than 100 units to a hole",
                    NearDeathFramesMetric::class
            ),
            DescribedMetric(
                    "characterWon",
                    "Which characters won the simulated game, given by a 1 if the character won, 0 otherwise",
                    CharacterWonMetric::class
            ),
            DescribedMetric(
                    "stageCoverage",
                    """
                        The amount of the stage that is covered by the characters.
                        The stage is discretized into cells of 32 x 32 units, and this metric
                        returns the amount of cells that have been visited during a game
                        divided by the total amount of cells
                    """.trimIndent(),
                    StageCoverageMetric::class
            ),
            DescribedMetric(
                    "leadChange",
                    """
                        How many times the lead changes during a game,
                        where the lead is determined by a state evaluation function
                        that considers stocks lost, damage taken and the distance from a hole
                    """.trimIndent(),
                    LeadChangeMetric::class
            ),
            DescribedMetric(
                    "accumulatedStateEvaluations",
                    "The state evaluation for each character for each frame summed",
                    StateEvaluationsAccumulatedMetric::class
            ),
            DescribedMetric(
                    "hitInteractions",
                    "The amount of attacks that hit an opponent",
                    HitInteractionsMetric::class
            ),
            DescribedMetric(
                    "leastInteractionType",
                    "The amount of the least hit ability type relative to the total hit ability types." +
                            "Given in the range [0, 1]",
                    LeastInteractionTypeMetric::class
            )
    )

    private val metricsByName: Map<String, KClass<out Metric>> = existingMetrics.map { it.name to it.metric }.toMap()


    fun getMetricInstance(measureName: String): NamedMetric? {
        return metricsByName[measureName]?.let {
            val metric = NamedMetric(measureName, it.createInstance())
            metric.metric.setup()
            metric
        }
    }

    fun getAllMetricNames(): List<String> = existingMetrics.map { it.name }
    fun getAllMetricDescriptions(): List<MetricDescription> = existingMetrics.map { MetricDescription(it.name, it.description) }
}