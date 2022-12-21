package `2022`

import java.io.File
import java.util.*

fun main() {
    Day22().solve()
}


class Day22 {

    val input1 = """
    """.trimIndent()


    fun solve() {
//        val f = File("/home/janos/Downloads/day20.in")
//        val s = Scanner(f)
        val s = Scanner(input1)

        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            val words = line.split(" ")
            if (!words.isEmpty()) {
                println("${words}")
            }
        }

        println("${true}")
    }
}

