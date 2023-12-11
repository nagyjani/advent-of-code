package `2023`


import common.Linearizer
import java.io.File
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day11().solve()
}


class Day11 {

    val input1 = """
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()


    fun print(map: String, l: Linearizer) {
        for (y in 0 until l.dimensions[1]) {

            for (x in 0 until l.dimensions[0]) {
                print("${map[l.toIndex(x,y)]}")
            }
            print("\n")
        }
    }

    fun String.expand(l: Linearizer) : Pair<String, Linearizer> {

        val sumlinesX = (1..l.dimensions[0]).map { '.' }.toMutableList()
        val sumlinesY = (1..l.dimensions[1]).map { '.' }.toMutableList()

        for (x in 0 until l.dimensions[0]) {
            for (y in 0 until l.dimensions[1]) {
                if (get(l.toIndex(x,y)) == '#') {
                    sumlinesX[x] = '#'
                    sumlinesY[y] = '#'
                }
            }
        }

        val sumlinesX1 = sumlinesX.flatMap { if (it == '.') { listOf('.', '+') } else { listOf(it) } }
        val sumlinesY1 = sumlinesY.flatMap { if (it == '.') { listOf('.', '+') } else { listOf(it) } }

        val xd1 = sumlinesX.size + sumlinesX.filter { it == '.' }.size
        val yd1 = sumlinesY.size + sumlinesY.filter { it == '.' }.size

        val l1 = Linearizer(xd1, yd1)

        val r = (1..xd1 * yd1).map { '.' }.toMutableList()

        var ix = 0

        for (x in 0 until xd1) {
            var iy = 0
            for (y in 0 until yd1) {
                if (sumlinesX1[x] == '+' || sumlinesY1[y] == '+') {
                    r[l1.toIndex(x,y)] = '+'
                } else {
                    r[l1.toIndex(x,y)] = get(l.toIndex(ix,iy))
                    ++iy
                }
            }
            if (sumlinesX1[x] != '+') {
                ++ix
            }
        }

        return r.joinToString ( "" ) to l1
    }


    fun solve() {
        val f = File("src/2023/inputs/day11.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0L
        var lineix = 0
        val lines = mutableListOf<String>()
        val sb = StringBuilder()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
            sb.append(line)
        }

        val l = Linearizer(lines[0].length, lineix)
        val lines1 = sb.toString()

        print(lines1, l)
        print("\n")

        val t = lines1.expand(l)
        val lines2 = t.first
        val l2 = t.second

        print(lines2, l2)
        print("\n")

        val galaxies = mutableListOf<Int>()
        lines2.forEachIndexed{ ix, it -> if (it == '#') {galaxies.add(ix)} }

        val m = 1000000
        for (g1 in galaxies) {
            for (g2 in galaxies) {
                if (g1 < g2) {
                    val c1 = l2.toCoordinates(g1)
                    val c2 = l2.toCoordinates(g2)

                    val cXmin = min(c1[0], c2[0])
                    val cXmax = max(c1[0], c2[0])
                    val cYmin = min(c1[1], c2[1])
                    val cYmax = max(c1[1], c2[1])


                    for (x in cXmin+1 .. cXmax) {
                        val y = cYmin
                        if (lines2[l2.toIndex(x,y)] == '+') {
                            sum1 += m-1
                        } else {
                            ++sum1
                        }
                    }

                    for (y in cYmin+1 .. cYmax) {
                        val x = cXmax
                        if (lines2[l2.toIndex(x,y)] == '+') {
                            sum1 += m-1
                        } else {
                            ++sum1
                        }
                    }

                    sum += abs(c1[0] - c2[0]) + abs(c1[1] - c2[1])
                }
            }
        }

        print("$sum $sum1\n")
    }
}