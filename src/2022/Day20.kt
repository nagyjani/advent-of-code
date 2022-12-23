package `2022`

import java.io.File
import java.math.BigInteger
import java.util.*

fun main() {
    Day20().solve()
}


class Day20 {

    val input1 = """
        1
        2
        -3
        3
        -2
        0
        4
    """.trimIndent()

    fun MutableList<Pair<Int, BigInteger>>.move(ix: Int) {
        val ix0 = indexOfFirst { it.first == ix }
        val number = get(ix0)
        val moves = number.second
        var ix1 = moves.add(ix0.toBigInteger())
        val s = size.toBigInteger()
        val s1 = s.minus(BigInteger.ONE)
        if (ix1 >= s) {
            val a0 = (ix1 - s) / (s1) - BigInteger.ONE
            for (i in 0..4) {
                val ix2 = ix1 - a0.plus(i.toBigInteger()) * s1
                if (ix2 < s && ix2 >= BigInteger.ZERO) {
                    ix1 = ix2
                    break
                }
            }
        }
        if (ix1 < BigInteger.ZERO) {
            val a0 = ix1.negate() / (s1) - BigInteger.ONE
            for (i in 0..4) {
                val ix2 = ix1 + a0.plus(i.toBigInteger()) * s1
                if (ix2 < s && ix2 >= BigInteger.ZERO) {
                    ix1 = ix2
                    break
                }
            }
        }
        if (moves < BigInteger.ZERO && ix1 == BigInteger.ZERO) {
            ix1 = s1
        }
        val ix11 = ix1.toInt()
        if (ix0 != ix11) {
            removeAt(ix0)
            add(ix11, number)
        }
    }

    fun solve() {
        val f = File("src/2022/inputs/day20.in")
        val s = Scanner(f)
//        val s = Scanner(input1)

        val numbers: MutableList<Pair<Int, BigInteger>> = mutableListOf()
        var ix = 0
//        val key = BigInteger("811589153")
        val key = BigInteger("811589153")
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            if (!line.isEmpty()) {
                numbers.add(ix to BigInteger(line).times(key))
                ++ix
            }
        }

//        val mixer = numbers.toMutableList()

//        println(numbers.map{it.second})
        for (j in 0 until 10) {
            for (i in 0 until numbers.size) {
                numbers.move(i)

            }
                    println(numbers.map{it.second})
        }

        println("${numbers.size} ${numbers.toSet().size}")
        println(numbers)

        val ix0 = numbers.indexOfFirst { it.second.equals(BigInteger.ZERO) }
        val ix1 = (ix0+1000)%numbers.size
        val ix2 = (ix0+2000)%numbers.size
        val ix3 = (ix0+3000)%numbers.size

        println("$ix0 ${numbers[ix1].second} ${numbers[ix2].second} ${numbers[ix3].second}")

        println("$ix0 ${numbers[ix1].second} + ${numbers[ix2].second} + ${numbers[ix3].second} = ${numbers[ix1].second + numbers[ix2].second + numbers[ix3].second}")
    }
}

