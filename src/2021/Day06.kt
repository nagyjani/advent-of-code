package `2021`

import java.io.File
import java.math.BigInteger
import java.util.*

fun main() {
    Day06().solve()
}

fun MutableList<BigInteger>.age(n: Int): MutableList<BigInteger> {
    for (i in 0 until n) {
        age()
    }
    return this
}

fun MutableList<BigInteger>.age(): MutableList<BigInteger> {
    val newParents = this[0]
    for (i in 1..8) {
        this[i-1] = this[i]
    }
    this[8] = newParents
    this[6] += newParents
    return this
}

class Day06 {
    fun solve() {
        val f = File("src/2021/inputs/day06.in")
        val s = Scanner(f)
        val ageTree = s.nextLine().trim().split(",").map{it.toInt()}.fold(MutableList<BigInteger>(9){BigInteger.ZERO}){acc, it -> acc[it] += BigInteger.ONE; acc}
        println("${ageTree.age(80).fold(BigInteger.ZERO){acc, it -> acc + it}}")
        println("${ageTree.age(256-80).fold(BigInteger.ZERO){acc, it -> acc + it}}")
    }
}