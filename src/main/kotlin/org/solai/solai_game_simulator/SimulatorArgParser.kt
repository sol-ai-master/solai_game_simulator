package org.solai.solai_game_simulator

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.SystemExitException
import com.xenomachina.argparser.default
import org.solai.solai_game_simulator.simulator_core.SimulatorConfig
import java.lang.Exception

class SimulatorArgParser(parser: ArgParser) {
    val queueAddress by parser.storing(
            help = "address of simulation queue"
    ) { toString() }
            .default("localhost")

    val queuePort by parser.storing(
            help = "The port of the simulation queue, -1 for default port"
    ) { toInt() }
            .default(-1)

    val headlessSimulations by parser.storing(
            help = "Run simulations headless (no graphics)"
    ) { toBoolean() }
            .default(true)

    val maxParallelJobs by parser.storing(
            help = "How many Simulation jobs can run in parallel, default=50"
    ) {toInt()}
            .default(100)

    val maxSimulationUpdates by parser.storing(
            help = "Maximum number of updates for a single simulation, default=54000 (15 minutes)"
    ) {toInt()}
            .default(10000)

    val playOffline by parser.storing(
            help = "Play two characters offline. Value: <char1Id>,<char2Id>,<play as player or two bots 0,1,-1>"
    ) {
        try {
            val parts = this.split(",")
            println("parts: $parts")
            val charIds = parts.subList(0, 2)
            val controlPlayerId = parts[2].toInt()

            PlayOfflineArgs(true, charIds, controlPlayerId)
        } catch (e: Exception) {
            throw SystemExitException("Invalid plyOffline args", -1)
        }
    }.default(PlayOfflineArgs(present = false))

    val experiment by parser.storing(
            help = "Experiment <e,ind>"
    ) {
        try {
            val parts = this.split(",")
            println("parts: $parts")
            val experiment = parts[0]
            val pairIndex = parts[1].toInt()

            ExperimentArgs(true, experiment, pairIndex)
        } catch (e: Exception) {
            throw SystemExitException("Invalid plyOffline args", -1)
        }
    }.default(ExperimentArgs(present = false))

    data class PlayOfflineArgs(
            val present: Boolean,
            val charactersId: List<String> = listOf(),
            val controllingPlayerIndex: Int = -2
    )

    data class ExperimentArgs(
            val present: Boolean,
            val experimentLabel: String = "-1",
            val pairIndex: Int = -1
    )


    fun toSimulatorConfig() = SimulatorConfig(
            queueAddress = queueAddress,
            queuePort = queuePort,
            headlessSimulations = headlessSimulations,
            maxParallelJobs = maxParallelJobs,
            maxSimulationUpdates = maxSimulationUpdates
    )
}