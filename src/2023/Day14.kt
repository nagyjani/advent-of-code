package `2023`


import java.io.File
import java.lang.StringBuilder
import java.util.*

fun main() {
    Day14().solve()
}


class Day14 {

    val input1 = """
        O....#....
        O.OO#....#
        .....##...
        OO.#O....O
        .O.....O#.
        O.#..O.#.#
        ..O..#O..O
        .......O..
        #....###..
        #OO..#....
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    fun List<String>.transpose(): List<String> {
        val r = List(this[0].length){ StringBuilder() }
        forEachIndexed{ ix1, it1 -> it1.forEachIndexed{ ix2, it2 -> r[ix2].append(it2) } }
        return r.map { it.toString() }
    }

    fun List<String>.reverse(): List<String> {
        return map { it.reversed() }
    }

    fun List<String>.tilt(): List<String> {
        return map { l -> l.split('#').map { chunk -> chunk.toCharArray().sortedBy { when (it) {'O' -> 0; else -> 1} }.joinToString("") }.joinToString("#")}
    }

    fun List<String>.fullCycle(): Pair<List<String>, Int> {

        val n = tilt()
        val w = n.transpose().tilt()
        val s = w.transpose().reverse().tilt()
        val e = s.transpose().reverse().tilt()
        val n1 = e.reverse().transpose().reverse()
        val load = n1.load()

        return n1 to load
    }

    fun List<String>.load(): Int {
        return map {
            it.mapIndexed{ix, it ->
                if (it == 'O') get(0).length - ix else 0
            }.sum()
        }.sum()
    }



    fun List<String>.print() {
        val e = joinToString("\n")
        print("$e\n")
    }

    fun solve() {
        val f = File("src/2023/inputs/day14.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
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

        val map0 = lines.toList().transpose()

        val t = map0.fullCycle()

        val m = mutableMapOf<String, Pair<Int, Int>>()

        var map = map0
        var periodStart = 0
        var period = 0
        for (i in 1 .. 10000) {
            val t = map.fullCycle()
            map = t.first
            val k = map.joinToString("")
            if (i == 98) {
                println("${t.second}")
            }
            if (m.contains(k)) {
                print("!!! $i")
                period = i - m[k]!!.first
                periodStart = i - period
                break
            } else {
                m[k] = i to t.second
            }
        }

        val e = (1000000000 - periodStart) % period + periodStart


        t.first.transpose().print()

        print("$e $sum $sum1 ${map0.tilt().load()}")
    }
}