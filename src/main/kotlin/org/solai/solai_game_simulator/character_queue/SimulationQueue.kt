package org.solai.solai_game_simulator.character_queue

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.*
import mu.KotlinLogging
import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisConnectionException

data class AbilityData(
        val type: String
)

data class CharacterData(
        val characterId: String,
        val moveAcceleration: Float,
        val characterRadius: Float,
        val abilitiesData: List<AbilityData>
)

data class GameSimulationData(
        val simulationId: String,
        val charactersData: List<CharacterData>,
        val metrics: List<String>
)



data class GameSimulationResult(
        val simulationId: String,
        val simulationData: GameSimulationData,
        val metric: List<String>,
        val measurements: List<Float>
)

private val SIMULATION_DATA_QUEUE_LABEL = "queue:simulation-data"
private val SIMULATION_RESULT_QUEUE_LABEL = "queue:simulation-result"

private val logger = KotlinLogging.logger {}

private val PUSH_SIMULATION_LOG_PREFIX = "Push game simulation to queue"
private val POLL_SIMULATION_LOG_PREFIX = "Poll simulation from queue"
private val PUSH_SIMULATION_RESULT_LOG_PREFIX = "Push simulation result to queue"
private val POLL_SIMULATION_RESULT_LOG_PREFIX = "Poll simulation result from queue"

class CharacterQueue {

    private val address: String = "localhost"
    private lateinit var jedis: Jedis
    private val jsonMapper = jacksonObjectMapper()

    fun connect(): Boolean {
        jedis = Jedis(address)
        return jedis.isConnected
    }

    fun close() {
        jedis.close()
    }

    private fun jedisCall(callName: String = "", call: (jedis: Jedis) -> Any?): Any? {
        try {
            return call(jedis)
        }
        catch (e: JedisConnectionException) {
            logger.warn("jedis connection could not be established when exectuing: $callName")
            return null
        }
    }

    fun pushSimulation(simulationData: GameSimulationData) {
        logger.debug { "$PUSH_SIMULATION_LOG_PREFIX: $simulationData" }
        val serializedSimulationData = jsonMapper.writeValueAsString(simulationData)
        logger.debug { "$PUSH_SIMULATION_LOG_PREFIX, raw json: $serializedSimulationData" }
        jedisCall(PUSH_SIMULATION_LOG_PREFIX) { it.lpush(SIMULATION_DATA_QUEUE_LABEL, serializedSimulationData) }
    }

    fun pollSimulation(): GameSimulationData? {
        val dataList: List<String>? = jedisCall(POLL_SIMULATION_LOG_PREFIX) {
            it.brpop(1, SIMULATION_DATA_QUEUE_LABEL)
        } as List<String>?
        if (dataList == null || dataList.size != 2) {
            logger.info { "$POLL_SIMULATION_LOG_PREFIX, no simulation present" }
            return null
        }
        val serializedSimulationData: String = dataList[1]

        logger.debug { "$POLL_SIMULATION_LOG_PREFIX, raw json: $serializedSimulationData" }
        val simulationResult: GameSimulationData
        try {
           simulationResult = jsonMapper.readValue(serializedSimulationData)
        }
        catch (e: JsonProcessingException) {
            logger.warn { "$POLL_SIMULATION_LOG_PREFIX could not be parsed as json: $e" }
            return null
        }
        catch (e: JsonMappingException) {
            logger.warn { "$POLL_SIMULATION_LOG_PREFIX could not be mapped to kotlin object: $e" }
            return null
        }

        logger.info { "$POLL_SIMULATION_LOG_PREFIX: $simulationResult" }
        return simulationResult
    }

    fun pushSimulationResult(simulationResult: GameSimulationResult) {
        logger.debug { "$PUSH_SIMULATION_RESULT_LOG_PREFIX: $simulationResult" }
        val serializedSimulationResult = jsonMapper.writeValueAsString(simulationResult)
        logger.debug { "$PUSH_SIMULATION_RESULT_LOG_PREFIX, raw: $serializedSimulationResult" }
        jedisCall(PUSH_SIMULATION_LOG_PREFIX) { it.lpush(SIMULATION_RESULT_QUEUE_LABEL, serializedSimulationResult) }
    }

    fun pollSimulationResult(): GameSimulationResult? {
        val dataList: List<String>? = jedisCall(POLL_SIMULATION_RESULT_LOG_PREFIX) {
            it.brpop(1, SIMULATION_RESULT_QUEUE_LABEL)
        } as List<String>?

        if (dataList == null || dataList.size != 2) {
            logger.info { "$POLL_SIMULATION_RESULT_LOG_PREFIX, no simulation present" }
            return null
        }
        val serializedSimulationResult: String = dataList[1]

        logger.debug { "$POLL_SIMULATION_RESULT_LOG_PREFIX, raw json: $serializedSimulationResult" }

        val simulationResult: GameSimulationResult
        try {
            simulationResult = jsonMapper.readValue(serializedSimulationResult)
        }
        catch (e: JsonProcessingException) {
            logger.warn { "$POLL_SIMULATION_RESULT_LOG_PREFIX could not be parsed as json: $e" }
            return null
        }
        catch (e: JsonMappingException) {
            logger.warn { "$POLL_SIMULATION_RESULT_LOG_PREFIX could not be mapped to kotlin object: $e" }
            return null
        }

        logger.info { "$POLL_SIMULATION_RESULT_LOG_PREFIX: $simulationResult" }
        return simulationResult
    }
}