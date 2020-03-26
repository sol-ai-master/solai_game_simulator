package org.solai.solai_game_simulator.character_queue

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.*
import mu.KotlinLogging
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub
import redis.clients.jedis.exceptions.JedisConnectionException


private val SIMULATION_DATA_QUEUE_LABEL = "queue:simulation-data"
private val SIMULATION_RESULT_QUEUE_LABEL = "queue:simulation-result"

private val logger = KotlinLogging.logger {}

private val PUSH_SIMULATION_LOG_PREFIX = "Push game simulation to queue"
private val POLL_SIMULATION_LOG_PREFIX = "Poll simulation from queue"
private val PUSH_SIMULATION_RESULT_LOG_PREFIX = "Push simulation result to queue"
private val POLL_SIMULATION_RESULT_LOG_PREFIX = "Poll simulation result from queue"


class RedisSimulationQueue : SimulationQueue {
    private lateinit var jedis: Jedis
    private val jsonMapper = jacksonObjectMapper()

    override fun connect(address: String): Boolean {
        jedis = Jedis(address)

        return jedis.isConnected
    }

    override fun close() {
        jedis.close()
    }

    override fun pushSimulationData(simulationData: GameSimulationData) {
        logger.debug { "$PUSH_SIMULATION_LOG_PREFIX: $simulationData" }
        val serializedSimulationData = simulationDataToMessage(simulationData)
        logger.debug { "$PUSH_SIMULATION_LOG_PREFIX, raw json: $serializedSimulationData" }
        jedisCall(PUSH_SIMULATION_LOG_PREFIX) { it.lpush(SIMULATION_DATA_QUEUE_LABEL, serializedSimulationData) }
    }

    override fun waitSimulationData(timeout: Int): GameSimulationData? {
        val dataList: List<String>? = jedisCall(POLL_SIMULATION_LOG_PREFIX) {
            it.brpop(timeout, SIMULATION_DATA_QUEUE_LABEL)
        } as List<String>?
        println(dataList)
        if (dataList == null || dataList.size != 2) {
            logger.info { "$POLL_SIMULATION_LOG_PREFIX, timeout expired" }
            return null
        }
        val serializedSimulationData: String = dataList[1]
        logger.debug { "$POLL_SIMULATION_LOG_PREFIX, raw json: $serializedSimulationData" }
        val simulationData = messageToSimulationData(serializedSimulationData)
        logger.debug { "$POLL_SIMULATION_LOG_PREFIX: $simulationData" }
        return simulationData
    }

    override fun pushSimulationResult(simulationResult: GameSimulationResult) {
        logger.debug { "$PUSH_SIMULATION_RESULT_LOG_PREFIX: $simulationResult" }
        val serializedSimulationResult = simulationResultToMessage(simulationResult)
        logger.debug { "$PUSH_SIMULATION_RESULT_LOG_PREFIX, raw: $serializedSimulationResult" }
        jedisCall(PUSH_SIMULATION_LOG_PREFIX) { it.lpush(SIMULATION_RESULT_QUEUE_LABEL, serializedSimulationResult) }
    }

    override fun waitSimulationResult(timeout: Int): GameSimulationResult? {
        val dataList: List<String>? = jedisCall(POLL_SIMULATION_RESULT_LOG_PREFIX) {
            it.brpop(timeout, SIMULATION_RESULT_QUEUE_LABEL)
        } as List<String>?

        if (dataList == null || dataList.size != 2) {
            logger.info { "$POLL_SIMULATION_RESULT_LOG_PREFIX, timeout expired" }
            return null
        }
        val serializedSimulationResult: String = dataList[1]

        logger.debug { "$POLL_SIMULATION_RESULT_LOG_PREFIX, raw json: $serializedSimulationResult" }

        val simulationResult = messageToSimulationResult(serializedSimulationResult)

        logger.info { "$POLL_SIMULATION_RESULT_LOG_PREFIX: $simulationResult" }
        return simulationResult
    }


    private fun jedisCall(callName: String = "", call: (jedis: Jedis) -> Any?): Any? {
        return try {
            call(jedis)
        }
        catch (e: JedisConnectionException) {
            logger.warn("jedis connection could not be established when exectuing: $callName")
            null
        }
    }

    private fun messageToSimulationData(message: String): GameSimulationData? {
        return try {
            jsonMapper.readValue(message)
        }
        catch (e: JsonProcessingException) {
            logger.warn { "$POLL_SIMULATION_LOG_PREFIX could not be parsed as json: $e" }
            null
        }
        catch (e: JsonMappingException) {
            logger.warn { "$POLL_SIMULATION_LOG_PREFIX could not be mapped to kotlin object: $e" }
            null
        }
    }

    private fun simulationDataToMessage(simulationData: GameSimulationData): String {
        val serializedSimulationData = jsonMapper.writeValueAsString(simulationData)
        return serializedSimulationData
    }

    private fun messageToSimulationResult(message: String): GameSimulationResult? {
        return try {
            jsonMapper.readValue(message)
        }
        catch (e: JsonProcessingException) {
            logger.warn { "$POLL_SIMULATION_RESULT_LOG_PREFIX could not be parsed as json: $e" }
            null
        }
        catch (e: JsonMappingException) {
            logger.warn { "$POLL_SIMULATION_RESULT_LOG_PREFIX could not be mapped to kotlin object: $e" }
            null
        }
    }

    private fun simulationResultToMessage(simulationResult: GameSimulationResult): String {
        return jsonMapper.writeValueAsString(simulationResult)
    }
}