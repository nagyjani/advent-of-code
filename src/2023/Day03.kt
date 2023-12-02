package `2023`


import java.io.File
import java.util.*

fun main() {
    Day03().solve()
}


class Day03 {

    val input1 = """
        
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    fun solve() {
//        val f = File("src/2023/inputs/day03.in")
//        val s = Scanner(f)
                val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }

        }

        print("$sum $sum1\n")
    }
}