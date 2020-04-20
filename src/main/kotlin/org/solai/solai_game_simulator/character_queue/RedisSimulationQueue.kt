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

    fun connect(address: String): Boolean {
        jedis = Jedis(address)
//        jedis.connect()
        return true //jedis.isConnected
    }

    override fun close() {
        jedis.close()
    }

    override fun pushSimulationData(simulationData: GameSimulationData) {
        logger.info { "$PUSH_SIMULATION_LOG_PREFIX: $simulationData" }
        val serializedSimulationData = simulationDataToMessage(simulationData)
        logger.info { "$PUSH_SIMULATION_LOG_PREFIX, raw json: $serializedSimulationData" }
        jedisCall(PUSH_SIMULATION_LOG_PREFIX) { it.lpush(SIMULATION_DATA_QUEUE_LABEL, serializedSimulationData) }
    }

    override fun waitSimulationData(timeout: Int): GameSimulationData? {
        val res: Any? = jedisCall(POLL_SIMULATION_LOG_PREFIX) {
            it.brpop(timeout, SIMULATION_DATA_QUEUE_LABEL)
        }

        if (res != null && res is List<*> && res.size == 2 && res[1] is String) {
            val serializedSimulationData: String = res[1] as String
            logger.debug { "$POLL_SIMULATION_LOG_PREFIX, raw json: $serializedSimulationData" }
            val simulationData = messageToSimulationData(serializedSimulationData)
            logger.debug { "$POLL_SIMULATION_LOG_PREFIX: $simulationData" }
            return simulationData
        }
        else {
            logger.info { "$POLL_SIMULATION_LOG_PREFIX, timeout expired" }
            return null
        }
    }

    override fun pushSimulationResult(simulationResult: GameSimulationResult) {
        logger.debug { "$PUSH_SIMULATION_RESULT_LOG_PREFIX: $simulationResult" }
        val serializedSimulationResult = simulationResultToMessage(simulationResult)
        logger.debug { "$PUSH_SIMULATION_RESULT_LOG_PREFIX, raw: $serializedSimulationResult" }
        jedisCall(PUSH_SIMULATION_LOG_PREFIX) { it.lpush(SIMULATION_RESULT_QUEUE_LABEL, serializedSimulationResult) }
    }

    override fun waitSimulationResult(timeout: Int): GameSimulationResult? {
        val res = jedisCall(POLL_SIMULATION_RESULT_LOG_PREFIX) {
            it.brpop(timeout, SIMULATION_RESULT_QUEUE_LABEL)
        }

        if (res != null && res is List<*> && res.size == 2 && res[1] is String) {
            val serializedSimulationResult: String = res[1] as String
            logger.debug { "$POLL_SIMULATION_RESULT_LOG_PREFIX, raw json: $serializedSimulationResult" }
            val simulationResult = messageToSimulationResult(serializedSimulationResult)
            logger.debug { "$POLL_SIMULATION_RESULT_LOG_PREFIX: $simulationResult" }
            return simulationResult
        }
        else {
            logger.info { "$POLL_SIMULATION_RESULT_LOG_PREFIX, timeout expired" }
            return null
        }
    }


    @Synchronized
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