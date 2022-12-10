package `2022`

import java.io.File
import java.util.*

fun main() {
    Day02().solve()
}


class Day02 {

    val input1 = """
A Y
B X
C Z
    """.trimIndent()

    fun points(r: Pair<Int, Int>): Int {
        var p = r.second + 1
        if (r.first == r.second) {
            p += 3
        } else if ((r.first+1)%3 == r.second) {
            p += 6
        }
//        print("$r $p\n")
        return p
    }

    fun points2(r: Pair<Int, Int>): Int {
        // 0: lose 1: draw 2: win
        val other = r.first
        var own = other
        if (r.second == 0) {
            own = (other+2)%3
        } else if (r.second == 2) {
            own = (other+1)%3
        }
        return points(Pair(other, own))
    }

    fun solve() {
        val f = File("src/2022/inputs/day02.in")
        val s = Scanner(f)
//                val s = Scanner(input1)
        val matches = mutableListOf<Pair<Int, Int>>()
        while (s.hasNext()) {
            val other = s.next().trim()[0] - 'A'
            val own = s.next().trim()[0] - 'X'
            matches.add(Pair(other, own))
        }

        val sumPoints = matches.sumOf { points(it) }
        val sumPoints2 = matches.sumOf { points2(it) }

        print("$sumPoints $sumPoints2\n")
    }
}