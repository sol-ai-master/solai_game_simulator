includeBuild("../sol_champ") {
    dependencySubstitution {
        substitute(module("com.github.sol-ai:sol_champ/sol_game:-SNAPSHOT")).with(project(":sol_game"))
        substitute(module("com.github.sol-ai:sol_champ/sol_engine:-SNAPSHOT")).with(project(":sol_engine"))
    }
}


rootProject.name = "solai_game_simulator"
