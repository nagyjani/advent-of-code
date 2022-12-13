package `2021`

import common.*
import java.io.File
import java.util.*

fun main() {
    Day15().solve()
}

class Day15 {
    val input = """
1163751742
1381373672
2136511328
3694931569
7463417111
1319128137
1359912421
3125421639
1293138521
2311944581
""".trimIndent()

    fun StringBuilder.toCave(): List<Int> {
        return toString().map{it.toString().toInt()}
    }

    fun to1to9(n: Int): Int {
        return if (n<10) n else n%9
    }

    fun StringBuilder.toCave5(x: Int, y: Int): List<Int> {
        val cave1 = toCave()
        val l1 = Linearizer(5, 5)
        val l2 = Linearizer(x, y)
        val l12 = Linearizer(x, 5, y, 5)
        val cave5 = MutableList(l12.size){0}
        for (i in l1.indexes) {
            for (j in l2.indexes) {
                val c1 = l1.toCoordinates(i)
                val c2 = l2.toCoordinates(j)
                val ix5 = l12.toIndex(c2[0], c1[0], c2[1], c1[1])
                cave5[ix5] = to1to9(cave1[j] + c1[0] + c1[1])
            }
        }
        return cave5
    }

    fun solve() {
        val f = File("src/2021/inputs/day15.in")
        val s = Scanner(f)
//        val s = Scanner(input)
        val caveBuilder = StringBuilder()
        caveBuilder.append(s.nextLine())
        val x0 = caveBuilder.length
        while (s.hasNextLine()) {
            caveBuilder.append(s.nextLine())
        }
        val y0 = caveBuilder.length/x0
        val cave = caveBuilder.toCave5(x0, y0)
        println("$x0 $y0 ${cave.size}")
        val l = Linearizer(x0 * 5, y0 * 5)

        val startIx = l.toIndex(0, 0)
        val finalIx = l.toIndex(x0*5-1, y0*5-1)
        val caveRisk = MutableList(cave.size){-1}
        caveRisk[startIx] = 0
        val border = mutableSetOf(startIx)
        val neighbours =
            listOf(
                l.offset(0, -1),
                l.offset(0, 1),
                l.offset(-1, 0),
                l.offset(1, 0))

        while (border.isNotEmpty()) {
            val minBorderIx = border.minByOrNull { caveRisk[it] }!!
            val minBorderRisk = caveRisk[minBorderIx]
            if (minBorderIx == finalIx) {
                println(minBorderRisk)
                break
            }
            border.remove(minBorderIx)
            for (i in neighbours.around(minBorderIx)) {
                val newRisk = minBorderRisk + cave[i]
                if (caveRisk[i] == -1) {
                    caveRisk[i] = newRisk
                    border.add(i)
                } else if (caveRisk[i] > newRisk) {
                    caveRisk[i] = newRisk
                }
            }
        }
    }
}