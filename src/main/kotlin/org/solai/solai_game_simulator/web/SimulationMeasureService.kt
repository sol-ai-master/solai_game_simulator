package org.solai.solai_game_simulator.web

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import org.solai.solai_game_simulator.simulator_core.Simulator
import org.solai.solai_game_simulator.SimulatorArgParser
import org.springframework.boot.ApplicationArguments
import org.springframework.stereotype.Service


@Service
class SimulationMeasureService(
        args: ApplicationArguments
) {

    final val simulator: Simulator

    init {
        val parsedArgs = mainBody {
            ArgParser(args.sourceArgs).parseInto(::SimulatorArgParser)
        }

        val simulatorConfig = parsedArgs.toSimulatorConfig()
        println(simulatorConfig)
        simulator = Simulator(simulatorConfig)

        simulator.start()
    }

}