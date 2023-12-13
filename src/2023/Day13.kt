package `2023`


import java.io.File
import java.lang.StringBuilder
import java.util.*

fun main() {
    Day13().solve()
}


class Day13 {

    val input1 = """
        #.##..##.
        ..#.##.#.
        ##......#
        ##......#
        ..#.##.#.
        ..##..##.
        #.#.##.#.
        
                #...##..#
                #....#..#
                ..##..###
                #####.##.
                #####.##.
                ..##..###
                #....#..#
    """.trimIndent()

    val input2 = """
        


    """.trimIndent()
    
    fun List<String>.transpose(): List<String> {
        val r = List(this[0].length){StringBuilder()}
        forEachIndexed{ ix1, it1 -> it1.forEachIndexed{ ix2, it2 -> r[ix2].append(it2) } }
        return r.map { it.toString() }
    }


    fun List<String>.vMirror(d: Int): Int {
        return transpose().hMirror(d)
    }

    fun List<String>.hMirror(d: Int): Int {
        var r = 0
        l@ for (i in 0 until size-1) {
            r = 0
            for (j in 0 .. i) {
                if (i+1+j >= size) {
                    continue@l
                }
                val s1 = get(i-j)
                val s2 = get(i+1+j)
                s1.indices.forEach{if (s1[it] != s2[it]) {++r} }
                if (r > d) {
                    continue@l
                }
                if ((j == i || i+1+j == size-1) && r==d) {
                    return i+1
                }
            }
        }
        return -1
    }

    fun List<String>.mirror(d: Int = 0): Int {
        var m = hMirror(d)
        if (m == -1) {
            return vMirror(d)
        }
        return m*100
    }

    fun solve() {
        val f = File("src/2023/inputs/day13.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        val lines = mutableListOf<String>()
        val maps = mutableListOf<MutableList<String>>(mutableListOf<String>())
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty() && maps.last().isNotEmpty()) {
                maps.add(mutableListOf())
                continue
            }
            lines.add(line)
            maps.last().add(line)
        }
        
        if (maps.last().isEmpty()) {
            maps.removeAt(maps.size-1)
        }

        var e = 0
        for (m in maps) {
            val r = m.mirror()
            val r1 = m.mirror(1)
            println("$e $r $r1")
            sum += r
            sum1 += r1
        }

        print("$sum $sum1\n")
    }
}