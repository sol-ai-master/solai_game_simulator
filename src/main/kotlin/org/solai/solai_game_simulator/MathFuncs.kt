package org.solai.solai_game_simulator

object MathFuncs {
    // should be 1 at domainMin and 0 at domainMax
    fun linearBetween(valueZero: Float, valueOne: Float, value: Float): Float {
        return ((1 / (valueOne - valueZero)) * (value - valueZero)).coerceIn(0f, 1f)
    }
}