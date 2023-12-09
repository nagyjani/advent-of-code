package `2023`


import java.io.File
import java.util.*

fun main() {
    Day09().solve()
}


class Day09 {

    val input1 = """
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    fun List<Long>.d(): List<Long> {
        return windowed(2,1) {it[1] - it[0]}
    }

    fun List<Long>.next(): Long {

        val ds = mutableListOf(this)

        while (ds.last().any{ it != 0L}) {
            ds.add(ds.last().d())
        }

        var r = 0L
        for (i in ds.reversed()) {
            r += i.last()
        }

        return r
    }

    fun List<Long>.prev(): Long {

        val ds = mutableListOf(this)

        while (ds.last().any{ it != 0L}) {
            ds.add(ds.last().d())
        }

        var r = 0L
        for (i in ds.reversed()) {
            r = i.first() - r
        }

        return r
    }

    fun solve() {
        val f = File("src/2023/inputs/day09.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0L
        var sum1 = 0L
        var lineix = 0
        val lines = mutableListOf<String>()
        val series = mutableListOf<List<Long>>()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
            series.add(line.split(" ").map{it.toLong()})
        }

        sum = series.map{it.next()}.sum()
        sum1 = series.map{it.prev()}.sum()

        print("$sum $sum1\n")
    }
}