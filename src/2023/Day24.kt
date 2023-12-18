package `2023`


import java.io.File
import java.util.*

fun main() {
    Day24().solve()
}


class Day24 {

    val input1 = """
        
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    fun solve() {
//        val f = File("src/2023/inputs/day24.in")
//        val s = Scanner(f)
        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        val lines = mutableListOf<String>()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
        }

        print("$sum $sum1\n")
    }
}