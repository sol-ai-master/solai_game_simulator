package org.solai.solai_game_simulator

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

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
            .default(50)

    fun toSimulatorConfig() = SimulatorConfig(
            queueAddress = queueAddress,
            queuePort = queuePort,
            headlessSimulations = headlessSimulations,
            maxParallelJobs = maxParallelJobs
    )
}