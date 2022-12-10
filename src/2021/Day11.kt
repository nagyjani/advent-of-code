package `2021`

import java.io.File
import java.util.*
import common.Adj2D

fun main() {
    Day11().solve()
}

class Day11 {

    val input = """
5483143223
2745854711
5264556173
6141336146
6357385478
4167524645
2176841721
6882881134
4846848554
5283751526
""".trimIndent()

    val a = Adj2D(10, 10)

    fun List<Int>.myToString(): String {
        var s = ""
        for (r in 0..a.toRow(size-1)) {
            for (c in 0..a.toColumn(size-1)) {
                s += "${this[a.toIx(r, c)]}"
            }
            s += "\n"
        }
        return s
    }

    fun MutableList<Int>.step() {
        replaceAll{it+1}
        while (find{it > 9} != null) {
            for (ix in this.indices) {
                if (this[ix] > 9) {
                    this[ix] = 0
                    a.adj8(ix).forEach {
                        if (this[it] != 0) {
                            ++this[it]
                        }
                    }
                }
            }
        }
    }

    fun List<Int>.flashCount(): Int {
        return count { it == 0 }
    }

    fun solve() {
        val f = File("src/2021/inputs/day11.in")
        val s = Scanner(f)
//        val s = Scanner(input)
        val o = mutableListOf<Int>()
        println("${a.adj8(19)}")
        while (s.hasNextLine()) {
            val l = s.nextLine().trim()
            o.addAll(l.map { it.toString().toInt() })
        }
        var flashCount = 0
        for (i in 1..100) {
            o.step()
            flashCount += o.flashCount()
        }
        println("$flashCount")
        var step = 0
        while (!o.all { it == 0 }) {
            o.step()
            ++step
        }
        println("${step+100}")
    }
}