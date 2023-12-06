package `2023`


import java.io.File
import java.util.*
import kotlin.math.sqrt

fun main() {
    Day06().solve()
}


class Day06 {

    val input1 = """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    fun solve() {
        val f = File("src/2023/inputs/day06.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        var times = listOf<Int>()
        var distances = listOf<Int>()

        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            if (line.startsWith("Time")) {
                times = line.split(Regex("[ :]+")).filter { it.all { it1 -> it1.isDigit() } }.map { it.toInt() }
            }
            if (line.startsWith("Dist")) {
                distances = line.split(Regex("[ :]+")).filter { it.all { it1 -> it1.isDigit() } }.map { it.toInt() }
            }
        }

        val t1 = times.map { it.toString() }.joinToString("").toLong()
        val d1 = distances.map { it.toString() }.joinToString("").toLong()

        val r = mutableListOf<Int>()
        for (i in times.indices) {
            val t = times[i]
            val d = distances[i]
            var n = 0
            for (j in 1..t) {
                if ((t-j) * j > d) {
                    n ++
                }
            }
            r.add(n)
        }

        val tf = t1.toDouble()
        val df = d1.toDouble()

        val a1 = (tf - sqrt(tf * tf - 4*df))/2
        val a2 = (tf +sqrt(tf * tf - 4*df))/2

        val a11 = a1.toInt() + 1
        val a22 = a2.toInt()

        print("$sum $sum1 ${r.fold(1){ a, it-> a * it}} ${a22-a11+1}\n")
    }
}