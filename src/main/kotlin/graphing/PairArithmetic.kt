package graphing


/**
 * Object für arithmetische Operation mit Pairs
 *
 * @constructor Create empty Pair arithmetic
 */
object PairArithmetic {
    /**
     * Add
     *
     * @param p1
     * @param p2
     * @return
     */
    fun add(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(p1.first + p2.first, p1.second + p2.second)
    }

    /**
     * Subtract
     *
     * @param p1
     * @param p2
     * @return
     */
    fun subtract(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(p1.first - p2.first, p1.second - p2.second)
    }

    /**
     * Multiply
     *
     * @param p1
     * @param p2
     * @return
     */
    fun multiply(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(p1.first * p2.first, p1.second * p2.second)
    }

    /**
     * Divide
     *
     * @param p1
     * @param p2
     * @return
     */
    fun divide(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(p1.first / p2.first, p1.second / p2.second)
    }
}