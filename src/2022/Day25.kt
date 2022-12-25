package `2022`

import java.io.File
import java.math.BigInteger
import java.util.*

fun main() {
    Day25().solve()
}


class Day25 {

    val input1 = """
        1=-0-2
        12111
        2=0=
        21
        2=01
        111
        20012
        112
        1=-1=
        1-12
        12
        1=
        122
    """.trimIndent()

    val input2 = """
2=-1=0
    """.trimIndent()

    val input3 = """
2011-=2=-1020-1===-1
    """.trimIndent()

    fun solve() {
//        val f = File("src/2022/inputs/day25.in")
//        val s = Scanner(f)
                val s = Scanner(input3)
//        val s = Scanner(input2)
//        val s = Scanner(input1)

        val numbers = mutableListOf<BigInteger>()
        var sum = BigInteger.ZERO

        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            val sn = line.map{
                when (it) {
                    '=' -> '0'
                    '-' -> '1'
                    else -> (it.digitToInt()+2).digitToChar()
                }
            }
            val sn1 = sn.joinToString("")
            val twos = "2".repeat(sn1.length)
            println("$line $sn $sn1")
            numbers.add(BigInteger(sn1, 5) - BigInteger(twos, 5))
            sum += numbers.last()
            println("${numbers.last()} ${numbers.last().toInt()}")
        }

        val twos = "2".repeat(sum.toString(5).length)

        val sum1 = sum + BigInteger(twos, 5)

        val sum2s = sum1.toString(5).map{
            when (it.digitToInt()) {
                0 -> '='
                1 -> '-'
                else -> (it.digitToInt()-2).digitToChar()
            }
        }.joinToString ("")

        println("$sum ${sum1.toString(5)} $sum2s")
    }
}

