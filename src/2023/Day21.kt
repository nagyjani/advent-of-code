package `2023`


import common.Linearizer
import common.around
import java.io.File
import java.math.BigInteger
import java.util.*
import kotlin.text.StringBuilder

fun main() {
    Day21().solve()
}


class Day21 {

    val input1 = """
        ...........
        .....###.#.
        .###.##..#.
        ..#.#...#..
        ....#.#....
        .##..S####.
        .##..#...#.
        .......##..
        .##.#.####.
        .##..##.##.
        ...........
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    fun step(n: Int, garden: String, l: Linearizer): Set<Int> {
        val startIx = garden.indexOf('S')
        return step(n, garden, l, setOf(startIx))
    }
    fun step(n: Int, garden: String, l: Linearizer, startPoints: Set<Int> = setOf()): Set<Int> {
        var steps = startPoints
        for (i in 1..n) {
            steps = step(garden, l, steps)
//            print(garden, l, steps)
        }
        return steps
    }

    fun print(garden: String, l: Linearizer, steps0: Set<Int> = setOf()) {
        for (y in 0 until l.dimensions[1]) {
            for (x in 0 until l.dimensions[0]) {
                val ix = l.toIndex(x,y)
                if (steps0.contains(ix)) {
                    print("O")
                } else {
                    print("${garden[ix]}")
                }
            }
            print("\n")
        }
        println()
    }

    fun step(garden: String, l: Linearizer): Set<Int> {
        return step(garden, l, garden.indexOf('S'))
    }

    fun step(garden: String, l: Linearizer, startIx: Int): Set<Int> {
        return step(garden, l, setOf(startIx))
    }

    fun step(garden: String, l: Linearizer, steps0: Set<Int>): Set<Int> {

        val ns = listOf(l.offset(1, 0), l.offset(0, 1), l.offset(-1, 0), l.offset(0, -1))

        val steps1 = mutableSetOf<Int>()

        for (s in steps0) {
            for (sn in ns.around(s)) {
                if (garden[sn] != '#') {
                    steps1.add(sn)
                }
            }
        }

        return steps1
    }

    fun zoom(n: Int, garden: String, l: Linearizer): Pair<String, Linearizer> {
        val n1 = 2 * n + 1
        val l1 = Linearizer(l.dimensions[0] * n1, l.dimensions[1] * n1)
        val g1 = MutableList(l1.size) {'.'}
        for (i in 0 until n1) {
            for (j in 0 until  n1) {
                for (x in 0 until l.dimensions[0]) {
                    for (y in 0 until l.dimensions[1]) {
                        val c = if (garden[l.toIndex(x, y)] == 'S') { '.' } else { garden[l.toIndex(x, y)] }
                        g1[l1.toIndex(i * l.dimensions[0] + x, j * l.dimensions[1] + y)] = c
                    }
                }
            }
        }
        g1[l1.toIndex(l1.dimensions[0]/2, l1.dimensions[1]/2)] = 'S'
        return g1.joinToString ("") to l1
    }

    fun calc(n: Int, garden: String, l: Linearizer): BigInteger {

        val n65 = 64
        val startIx = l.toIndex(65, 65)

        val c1 = step(130, garden, l, setOf(l.toIndex(65, 0)))
        val c2 = step(130, garden, l, setOf(l.toIndex(130, 65)))
        val c3 = step(130, garden, l, setOf(l.toIndex(65, 130)))
        val c4 = step(130, garden, l, setOf(l.toIndex(0, 65)))
        val c = BigInteger.valueOf(c1.size.toLong() + c2.size.toLong() + c3.size.toLong() +c4.size.toLong())

//        print(garden, l, c2)
//        println("${c2.size}")

        val a1 = step(n65, garden, l, setOf(l.toIndex(0, 0)))
        val a2 = step(n65, garden, l, setOf(l.toIndex(0, 130)))
        val a3 = step(n65, garden, l, setOf(l.toIndex(130, 0)))
        val a4 = step(n65, garden, l, setOf(l.toIndex(130, 130)))
        val a = BigInteger.valueOf(a1.size.toLong() + a2.size.toLong() + a3.size.toLong() + a4.size.toLong())

//        print(garden, l, a4)
//        println("${a4.size}")
//
        val b1 = step(131 + n65, garden, l, setOf(l.toIndex(0, 0))).size.toLong()
        val b2 = step(131 + n65, garden, l, setOf(l.toIndex(0, 130))).size.toLong()
        val b3 = step(131 + n65, garden, l, setOf(l.toIndex(130, 0))).size.toLong()
        val b4 = step(131 + n65, garden, l, setOf(l.toIndex(130, 130))).size.toLong()
        val b = BigInteger.valueOf(b1 + b2 + b3 + b4)

//                val bb = step(131 + n65, garden, l, setOf(l.toIndex(0, 0)))
//        print(garden, l, bb)

        val f1 = BigInteger.valueOf(step(131, garden, l, setOf(startIx)).size.toLong())
        val f2 = BigInteger.valueOf(step(132, garden, l, setOf(startIx)).size.toLong())

        val f3 = step(1131, garden, l, setOf(startIx)).size.toLong()
        val f4 = step(1132, garden, l, setOf(startIx)).size.toLong()

        val nm1 = BigInteger.valueOf(n-1L)
        val n0 = BigInteger.valueOf(n.toLong())

        val r = nm1 * b + n0 * a + c + n0 * n0 * f2 + nm1 * nm1 * f1

        return r
    }

    fun count(points: Set<Int>, l: Linearizer, x0: Int, x1: Int, y0: Int, y1: Int): Int {
        var r = 0
        for (p in points) {
            val (x, y) = l.toCoordinates(p)
            if (x0 <= x && x < x1 && y0 <= y && y < y1) {
                r++
            }
        }
        return r
    }

    fun print(garden: String, l: Linearizer, steps0: Set<Int> = setOf(), x0: Int, x1: Int, y0: Int, y1: Int) {
        for (y in y0 until y1) {
            for (x in x0 until x1) {
                val ix = l.toIndex(x,y)
                if (steps0.contains(ix)) {
                    print("O")
                } else {
                    print("${garden[ix]}")
                }
            }
            print("\n")
        }
        println()
    }

    fun solve() {
        val f = File("src/2023/inputs/day21.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        val lines = mutableListOf<String>()
        val gardenBuilder = StringBuilder()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
            gardenBuilder.append(line)
        }
        val garden = gardenBuilder.toString()
        val x = lines[0].length
        val y = lineix
        val l = Linearizer(x ,y)
        val startIx = garden.indexOf('S')
        val (startX, startY) = l.toCoordinates(startIx)

//        print(garden, l)

        // 131: 7656
        // 132: 7688
        // 133: 7656

        val accessible = step(65, garden, l)
//        print(garden, l, accessible)

//        val accessible1 = step(65, garden, l, setOf(l.toIndex(66, 0)))
//        print(garden, l, accessible1)


//        val (g1, l1) = zoom(2, garden, l)

//        val a3 = step(131 + 131 + 65, g1, l1)

//        print(g1, l1, a3)

//        val r1 = calc(2, garden, l)

//        val left = count(a3, l1, 0, 131, 131, 262)
//        print(g1, l1, a3, 0, 131, 131, 262)

//        println("${a3.size} $r1")
        val r1 = calc(202300, garden, l)

//        println("${b1*b1*f1 + b2*b2*f2}") // 627960760332488
//        println("${b1*b1* BigInteger.valueOf(f2) + b2*b2*BigInteger.valueOf(f1)}") // 627960747385256
//boldog kara
        println("$r1 $startX $startY")
        print("${accessible.size} ${l.dimensions[0]} ${l.dimensions[1]}\n")
    }
}