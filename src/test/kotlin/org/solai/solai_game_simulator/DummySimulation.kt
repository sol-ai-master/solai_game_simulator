package org.solai.solai_game_simulator

class DummySimulation(
        override val simulationId: String
) : Simulation {

    override fun calculateMetrics(): Map<String, Float> {
        return mapOf()
    }

    override fun run() {

        Thread.sleep(1000)
    }
}