package org.solai.solai_game_simulator.character_queue

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.readValue
import redis.clients.jedis.Jedis


interface SimulationQueue {

    companion object {
        fun getQueue(address: String): SimulationQueue? {
            val simulationQueue = RedisSimulationQueue()
            val connected = simulationQueue.connect(address)
            return if (connected) simulationQueue else null
        }
    }

    fun close()

    fun pushSimulationData(simulationData: GameSimulationData)
    fun waitSimulationData(timeout: Int): GameSimulationData?


    fun pushSimulationResult(simulationResult: GameSimulationResult)
    fun waitSimulationResult(timeout: Int): GameSimulationResult?
}