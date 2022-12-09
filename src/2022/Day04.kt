import java.io.File
import java.util.*

fun main() {
    Day2204().solve()
}


class Day2204 {

    val input1 = """
2-4,6-8
2-3,4-5
5-7,7-9
2-8,3-7
6-6,4-6
2-6,4-8
    """.trimIndent()


    fun solve() {
        val f = File("src/2022/inputs/aoc2204.in")
        val s = Scanner(f)
//                val s = Scanner(input1)
        val ranges = mutableListOf<Pair<Pair<Int, Int>,Pair<Int, Int>>>()
        s.useDelimiter("\\W")
        var contains = 0
        var separate = 0
        var all = 0
        while (s.hasNext()) {
            ++all
            val line = (1..4).map{s.next().toInt()}
            if (line.size != 4 || line[0] > line[1] || line[2] > line[3]) {
                println(line)
                throw RuntimeException()
            }
            if (line[0] <= line[2] && line[1] >= line[3]) {
                ++contains
            } else if (line[0] >= line[2] && line[1] <= line[3]) {
                ++contains
            }
            if (line[1] < line[2] || line[3] < line[0]) {
                ++separate
            }
            println("$contains $separate $line")
        }
        println("$contains $separate ${all-separate}")
    }
}

