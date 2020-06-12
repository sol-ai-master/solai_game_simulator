package org.solai.solai_game_simulator.play_chars

import org.solai.solai_game_simulator.players.RulePlayer
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolGameSimulationOffline
import kotlin.system.measureNanoTime

object PlaySolGame {

    fun playOffline(characterConfigs: List<CharacterConfig>, controlPlayerIndex: Int) {
        val aiPlayers = listOf(RulePlayer(), RulePlayer())

        val solOfflineGame = SolGameSimulationOffline(
                charactersConfigs = characterConfigs,
                graphicsSettings = SolGameSimulationOffline.GraphicsSettings(
                        headless = false,
                        graphicalInput = controlPlayerIndex != -1,
                        allowGui = true,
                        controlPlayerIndex = controlPlayerIndex
                )
        )

        aiPlayers.forEach { it.onSetup() }

        solOfflineGame.setup()
        solOfflineGame.start()
        solOfflineGame.step()  // step once to load all entities in game

        aiPlayers.forEachIndexed { index, it ->
            it.onStart(index, solOfflineGame.retrieveGameState(), characterConfigs)
        }

        val targetUpdateTimeMillis = 1000f / 60f
        while(true) {
            val gameState = solOfflineGame.retrieveGameState()

            if (gameState.gameEnded) {
                break
            }

            val stepTimeNanos = measureNanoTime {
                solOfflineGame.step()

                aiPlayers.forEachIndexed { index, player ->
                    if (index != controlPlayerIndex) {
                        val inputs = player.onUpdate(index, gameState, characterConfigs)
                        solOfflineGame.setInputs(index, inputs)
                    }
                }

            }
            val sleepTime: Long = (targetUpdateTimeMillis - (stepTimeNanos * 0.000001))
                    .toLong().coerceAtLeast(0)
            Thread.sleep(sleepTime)
        }
        solOfflineGame.terminate()
    }

}