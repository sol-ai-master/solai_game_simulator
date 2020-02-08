package org.solai.solai_game_simulator.character_queue

import com.fasterxml.jackson.module.kotlin.*
import redis.clients.jedis.Jedis

data class CharacterSimulationData(
        val characterId: String,
        val moveAcceleration: Float,
        val characterRadius: Float
)

data class GameSimulationData(
    val simulationId: String,
    val charactersData: List<CharacterSimulationData>
)

data class GameSimulationResult(
        val simulationId: String
//        val simulationData: GameSimulationData
)

private val SIMULATION_DATA_QUEUE_LABEL = "queue:simulation-data"
private val SIMULATION_RESULT_QUEUE_LABEL = "queue:simulation-result"

class CharacterQueue {

    private val address: String = "localhost"
    private lateinit var jedis: Jedis
    private val jsonMapper = jacksonObjectMapper()

    fun connect(): Boolean {
        jedis = Jedis(address)
        return jedis.isConnected
    }

    fun pushSimulation(simulationData: GameSimulationData) {
        val serializedSimulationData = jsonMapper.writeValueAsString(simulationData)
        println("Pushing simulation: $serializedSimulationData")
        jedis.lpush(SIMULATION_DATA_QUEUE_LABEL, serializedSimulationData)
    }

    fun pollSimulation(): GameSimulationData? {
        val dataList: List<String>? = jedis.brpop(1, SIMULATION_DATA_QUEUE_LABEL)
        if (dataList == null || dataList.size != 2) {
            return null
        }
        println(dataList)
        val serializedSimulationData: String = dataList[1]

        println("polling simulation result: $serializedSimulationData")
        val simulationResult: GameSimulationData = jsonMapper.readValue(serializedSimulationData)
        return simulationResult
    }
}