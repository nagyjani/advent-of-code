import java.io.File
import java.util.*

fun main() {
    Day06().solve()
}


class Day06 {

    val input1 = """
        mjqjpqmgbljsphdztnvjfqwrcgsmlb
    """.trimIndent()

    fun solve(message: String, length: Int): Int {
        var first = 0
        var current = length
        message.windowed(length, step = 1) {
            var d = true
            for (i in 0 until length) {
                for (j in 0 until length) {
                    if (i!=j && it[i] == it[j]) {
                        d = false
                    }
                }
            }
            if (first == 0 && d) {
                first = current
            }
            ++current
        }
        return first
    }

    fun solve() {
        val f = File("src/2022/inputs/day06.in")
        val s = Scanner(f)
//                val s = Scanner(input1)

        val message = s.nextLine().trim()
        val first1 = solve(message, 4)
        val first2 = solve(message, 14)

        println("$first1 $first2")
    }
}

