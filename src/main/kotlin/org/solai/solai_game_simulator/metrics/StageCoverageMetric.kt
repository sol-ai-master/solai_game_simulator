package org.solai.solai_game_simulator.metrics

import org.joml.Vector2f
import org.joml.Vector2i
import org.solai.solai_game_simulator.simulator_core.Metric
import sol_game.game_state.SolGameState

class StageCoverageMetric : Metric {

    class DiscreteBoardVisits(
            val worldSize: Vector2f
    ) {
        val cellSize = Vector2f(32f, 32f)
        val discreteBoardSize = Vector2i((worldSize.x / cellSize.x).toInt(), (worldSize.y / cellSize.y).toInt())
        private var discreteBoard = (0 until discreteBoardSize.x).map {
            (0 until discreteBoardSize.y).map { 0 }.toMutableList()
        }

        fun addVisit(position: Vector2f) {
            val cellX = (position.x / cellSize.x).toInt().coerceIn(0, discreteBoardSize.x-1)
            val cellY = (position.y / cellSize.y).toInt().coerceIn(0, discreteBoardSize.y-1)
            discreteBoard[cellX][cellY]++
        }

        fun getAllCellValues(): List<Int> = discreteBoard.flatten()
        override fun toString(): String {
            return "DiscreteBoardVisits(boardSize=$worldSize, cellSize=$cellSize, discreteBoardSize=$discreteBoardSize, discreteBoard=$discreteBoard)"
        }

        fun getCellVisitFrequency(): Map<Int, Int> {
            val frequency = getAllCellValues()
                    .groupingBy { it }
                    .eachCount()
                    .filter { entry -> entry.key != 0 }
            println("frequency: $frequency")
            return frequency
        }

    }

    var boardVisitsPerCharacter = listOf<DiscreteBoardVisits>()
        private set

    var worldSize = Vector2f()

    override fun start(playersCount: Int, gameState: SolGameState) {
        worldSize = Vector2f(gameState.staticGameState.worldSize)
        boardVisitsPerCharacter = (0 until playersCount).map { DiscreteBoardVisits(worldSize) }
    }

    override fun update(gameState: SolGameState) {
        gameState.charactersState
                .map { it.physicalObject.position }
                .forEachIndexed { index, pos -> boardVisitsPerCharacter[index].addVisit(pos) }
    }

    private fun deviationFromOne(values: List<Int>): Float {
        return values.map { 1f / it.toFloat() }.sum() / values.size
    }

    // linear deviation from one for each cell
    override fun calculate(): List<Float> {
        return boardVisitsPerCharacter.map { boardVisits ->
            deviationFromOne(boardVisits.getAllCellValues().filter { it != 0 })
        }
    }
}