package graphing

//class to handle arithmetics of Pair class

object PairArithmetic {
    fun add(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(p1.first + p2.first, p1.second + p2.second)
    }

    fun subtract(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(p1.first - p2.first, p1.second - p2.second)
    }

    fun multiply(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(p1.first * p2.first, p1.second * p2.second)
    }

    fun divide(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(p1.first / p2.first, p1.second / p2.second)
    }
}