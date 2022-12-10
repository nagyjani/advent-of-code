package `2021`

import java.io.File
import java.util.*

fun main() {
    Day01.solve()
}

object Day01 {
    fun solve() {
        val f = File("src/2021/inputs/day01.in")
        val s = Scanner(f)
        val depths = mutableListOf<Int>()
        while (s.hasNextInt()) {
            depths.add(s.nextInt())
        }
        val numOfIncreases = depths.windowed(2 ){if (it[0] < it[1]) 1 else 0}.sum()
        println("$numOfIncreases")

        val numOfIncreases2 = depths.windowed(3 ){it.sum()}.windowed(2){if (it[0] < it[1]) 1 else 0}.sum()
        println("$numOfIncreases2")
    }
}