package org.solai.solai_game_simulator.play_chars

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import sol_game.CharacterConfigLoader
import sol_game.core_game.CharacterConfig
import sol_game.schmathiasConfig
import java.io.File
import java.io.PrintWriter
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException



object ExperimentsCharacterRetriever {

    val shrankCharacterId = "93e00e03-fa8d-4f08-9c84-c6fbbef6a95f"
    val schmathiasCharacterId = "9775f3af-9db7-4d2d-83fe-f12c9120b2dd"
    val brailCharacterId = "0c78f292-f5f6-4abb-bc36-4d1360a3e4f2"
    val magnetCharacterId = "0c6b65fc-828d-4176-96e4-6f095a2913ef"

    val lastPopFeasiblesId = listOf(
            "a857c4be-ba2b-43e0-bd2b-023082d28cbe",
            "ff4bc012-772a-4501-8d97-1d4b0ed65268",
            "11dc6e2f-46ef-4b0a-b6b2-50cffebcd280",
            "b5bc3b2d-bba2-4b7d-98a5-6b02f5b96539",
            "29f9e600-ad4c-4b5c-808b-c0185775f36b",
            "af2b0410-bcd7-4dce-a74b-af8fc3e413e3"
    )

    val randomInfeasiblesId = mapOf(
            0.8f to "a1b53136-cc87-4f61-b84b-eaf39950e98c",
            0.6f to "7c5433b6-79ac-43f9-a003-e273ad7bd795",
            0.4f to "9bb723e1-2683-4c61-892b-28c551707eb1",
            0.2f to "b5d8290a-a363-47e8-b288-2d3f90dffff3"
    )

    val randomFeasiblesId = listOf(
            "c0cbfbcf-b78c-4c84-a1e1-90550146af30",
            "74666c0b-6558-4300-bbc2-c74258319476",
            "dd19937b-f7c6-49df-b891-aa5442bdcc96",
            "9c483471-46d3-4083-a620-922ff00d8f6d"
    )

    fun saveCharacterConfig(charConfig: CharacterConfig) {
        val objectMapper = jacksonObjectMapper()
        val charSerialized = objectMapper.writeValueAsString(charConfig)
        val writer = PrintWriter(File("experimentChars/${charConfig.characterId}.json"))
        writer.println(charSerialized)
        writer.close()
    }

    fun saveAllExperiemntChars() {
        val charsId = listOf(
                shrankCharacterId,
                schmathiasCharacterId,
                brailCharacterId,
                magnetCharacterId
        ) + lastPopFeasiblesId + randomInfeasiblesId.values.toList() + randomFeasiblesId

        val charsConfig = charsId.map { CharactersRetriever.fetchDBCharacterConfig(it)!! }
        charsConfig.forEach { saveCharacterConfig(it) }
    }

//    fun loadExperimentsCharacters(): List<CharacterConfig> {
//        val dirName = "/experimentsCharactersConfig"
//        val experimentsFolderResource = this.javaClass.getResource(dirName)
//
//        val experimentCharactersFolder = File(experimentsFolderResource.toURI())
//        val charConfigsFilename = experimentCharactersFolder.list()
//                ?: throw IllegalStateException("could not get list of experiment characters config in directory")
//
//        val charConfigs = charConfigsFilename.map {
//            val charFilename = "${dirName.substring(1)}/$it"
//            println(charFilename)
//            CharacterConfigLoader.fromResourceFile(charFilename)
//        }
//        return charConfigs
//    }


    val experimentsCharacterPairs = mapOf(
            "test" to listOf(
                    shrankCharacterId to schmathiasCharacterId,
                    schmathiasCharacterId to shrankCharacterId,
                    brailCharacterId to magnetCharacterId,
                    magnetCharacterId to brailCharacterId
            ),
            "e1" to listOf(
                    randomFeasiblesId[0] to shrankCharacterId,
                    randomInfeasiblesId[0.4f] to shrankCharacterId,

                    randomFeasiblesId[0] to schmathiasCharacterId,
                    randomInfeasiblesId[0.4f] to schmathiasCharacterId,

                    randomFeasiblesId[1] to shrankCharacterId,
                    randomInfeasiblesId[0.2f] to shrankCharacterId,

                    randomFeasiblesId[1] to schmathiasCharacterId,
                    randomInfeasiblesId[0.2f] to schmathiasCharacterId,

                    randomFeasiblesId[2] to shrankCharacterId,
                    randomInfeasiblesId[0.8f] to shrankCharacterId,

                    randomFeasiblesId[2] to schmathiasCharacterId,
                    randomInfeasiblesId[0.8f] to schmathiasCharacterId,

                    randomFeasiblesId[3] to shrankCharacterId,
                    randomInfeasiblesId[0.6f] to shrankCharacterId,

                    randomFeasiblesId[3] to schmathiasCharacterId,
                    randomInfeasiblesId[0.6f] to schmathiasCharacterId
            ),
            "e2" to listOf(
//                    lastPopFeasiblesId[0] to shrankCharacterId,
//                    brailCharacterId to shrankCharacterId,
//                    lastPopFeasiblesId[1] to shrankCharacterId,
//                    magnetCharacterId to shrankCharacterId,

                    lastPopFeasiblesId[0] to schmathiasCharacterId,
                    brailCharacterId to schmathiasCharacterId,
                    lastPopFeasiblesId[1] to schmathiasCharacterId,
                    magnetCharacterId to schmathiasCharacterId,


//                    lastPopFeasiblesId[2] to shrankCharacterId,
//                    brailCharacterId to shrankCharacterId,
//                    lastPopFeasiblesId[3] to shrankCharacterId,
//                    magnetCharacterId to shrankCharacterId,

                    lastPopFeasiblesId[2] to schmathiasCharacterId,
                    brailCharacterId to schmathiasCharacterId,
                    lastPopFeasiblesId[3] to schmathiasCharacterId,
                    magnetCharacterId to schmathiasCharacterId,


//                    lastPopFeasiblesId[4] to shrankCharacterId,
//                    brailCharacterId to shrankCharacterId,
//                    lastPopFeasiblesId[5] to shrankCharacterId,
//                    magnetCharacterId to shrankCharacterId,

                    lastPopFeasiblesId[4] to schmathiasCharacterId,
                    brailCharacterId to schmathiasCharacterId,
                    lastPopFeasiblesId[5] to schmathiasCharacterId,
                    magnetCharacterId to schmathiasCharacterId
            )
    )

    fun playExperimentChars(experimentLabel: String, characterPairIndex: Int, playerIndex: Int = -2) {
        val characterIdPair = experimentsCharacterPairs[experimentLabel]
                ?.get(characterPairIndex)
                ?: throw IllegalArgumentException("Experiment label invalid: $experimentLabel")
        val char1Config = CharacterConfigLoader.fromResourceFile("experimentsCharactersConfig/${characterIdPair.first}.json")
        val char2Config = CharacterConfigLoader.fromResourceFile("experimentsCharactersConfig/${characterIdPair.second}.json")

        val charConfigs = listOf(char1Config, char2Config)
        println("playerIndex $playerIndex")
        if (playerIndex == -2) {
            PlaySolGame.playOffline(charConfigs, 0)
        }
        else if (playerIndex == -1) {
            PlaySolGame.playServer(charConfigs)
        }
        else if (playerIndex == 0 || playerIndex == 1) {
            PlaySolGame.playClient(playerIndex)
        }
    }

}