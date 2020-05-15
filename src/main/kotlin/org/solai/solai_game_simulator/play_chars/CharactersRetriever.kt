package org.solai.solai_game_simulator.play_chars

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.*
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import org.bson.types.ObjectId
import sol_game.core_game.CharacterConfig
import java.io.File
import java.io.PrintWriter


data class EvaluatedIndividual(
        val individual: CharacterConfig,
        val fitness: List<Float>
)

data class EvaluatedNoveltyIndividual(
        val individual: CharacterConfig,
        val fitness: List<Float>,
        val novelty: Float
)


data class EvolutionInstance(
        val _id: ObjectId,
        val evolutionStart: String,
        val generations: List<List<EvaluatedIndividual>>,
        val evolutionConfig: Map<String, Any>,
        val novelArchive: List<EvaluatedNoveltyIndividual>?,
        val totalTimeTaken: String?
)

object CharactersRetriever {

    val USERNAME = "haraldvinje"
    val PASSWORD = System.getenv("SOLAI_DB_PASSWORD")
    val CONNECTION_STR = ConnectionString("mongodb+srv://" + USERNAME + ":" + PASSWORD +
            "@cluster0-dzimv.mongodb.net/test?retryWrites=true&w=majority")

    val jsonMapper = jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    var settings = MongoClientSettings.builder()
            .applyConnectionString(CONNECTION_STR)
            .retryWrites(true)
            .build()

    fun fetchDBCharacterConfig(characterId: String): CharacterConfig? {
        val mongoClient = MongoClients.create(settings)
        val database = mongoClient.getDatabase("solai")
        val collection = database.getCollection("evolution_instances")
        val evolutionInstances = collection.find()
        evolutionInstances.forEach { doc ->
            val evolutionInstanceJson = doc.toJson()
            val evolutionInstance: EvolutionInstance = jsonMapper.readValue(evolutionInstanceJson)

            val charConfigInPopulation = evolutionInstance.generations
                    .asSequence()
                    .flatten()
                    .map { it.individual }
                    .find { it.characterId == characterId }
            charConfigInPopulation?.let { return@fetchDBCharacterConfig it }

        }
        return null
    }

}