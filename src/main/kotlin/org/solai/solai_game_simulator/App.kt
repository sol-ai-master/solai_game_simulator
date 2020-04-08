/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.solai.solai_game_simulator

import org.solai.solai_game_simulator.character_queue.GameSimulationData
import org.solai.solai_game_simulator.character_queue.RedisSimulationQueue
import org.solai.solai_game_simulator.character_queue.SimulationQueue
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class App

fun main(args: Array<String>) {
    SpringApplication.run(App::class.java, *args)
//    testQueue()
}


fun testQueue() {

    val charQ: SimulationQueue = RedisSimulationQueue()
    charQ.connect("localhost")


    Thread() {
        var i = 0
        while (true) {
            val charQ2:  SimulationQueue = RedisSimulationQueue()
            charQ2.connect("localhost")
            charQ2.pushSimulationData(GameSimulationData(
                    simulationId = "sim1 $i",
                    charactersData = listOf(),
                    metrics = listOf("hei")
            ))
            i++
            Thread.sleep(1000)
        }

    }.start()

    while (true) {
        val simData = charQ.waitSimulationData(3)
        println(simData)
        Thread.sleep(3000)
    }

}