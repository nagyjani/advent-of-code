package `2023`


import common.Linearizer
import common.Offset
import common.Point
import common.around
import java.io.File
import java.lang.RuntimeException
import java.util.*

fun main() {
    Day10().solve()
}



class Day10 {

    val input1 = """
        ..F7.
        .FJ|.
        SJ.L7
        |F--J
        LJ...
    """.trimIndent()

    val input2 = """
        .F----7F7F7F7F-7....
        .|F--7||||||||FJ....
        .||.FJ||||||||L7....
        FJL7L7LJLJ||LJ.L-7..
        L--J.L7...LJS7F-7L7.
        ....F-J..F7FJ|L7L7L7
        ....L7.F7||L7|.L7L7|
        .....|FJLJ|FJ|F7|.LJ
        ....FJL-7.||.||||...
        ....L---J.LJ.LJLJ...
    """.trimIndent()

    fun Int.next(lines: String, l: Linearizer, b: Int): Int {
        val c = l.toCoordinates(this)

        val ch = lines[this]

        val n =
        when (lines[this]) {
            '|' -> listOf(l.offset(0,-1), l.offset(0,1))
            '-' -> listOf(l.offset(1,0), l.offset(-1,0))
            'L' -> listOf(l.offset(0,-1), l.offset(1,0))
            'J' -> listOf(l.offset(-1,0), l.offset(0,-1))
            '7' -> listOf(l.offset(-1,0), l.offset(0,1))
            'F' -> listOf(l.offset(1,0), l.offset(0,1))
            else -> {
                throw RuntimeException()
            }
        }
        val ns = n.around(this)

        return ns.filter { it != b }.first()
    }
//
//    fun Int.inUpOrLeftIn(lines: String, l: Linearizer, bx: Int, bv: Boolean): Boolean {
//        val c = l.toCoordinates(this)
//        val c0 = l.toCoordinates(bx)
//
//        val ch0 = lines[bx]
//        val ch = lines[this]
//
//        val m = mapOf(
//                "-J" to true,
//                "--" to true,
//                "-7" to true,
//                "LJ" to false,
//                "L-" to false,
//                "L7" to true,
//                "FJ" to true,
//                "F-" to true,
//                "F7" to true,
//                "7|" to false,
//                "7L" to false,
//                "7J" to false,
//                "JF" to true,
//                "J|" to true,
//                "J7" to false,
//                "|L" to true,
//                "|J" to true,
//                "||" to true)
//
//        if (c[0] == c0[0]) {
//            when (listOf(ch, ch0).joinToString { "" }) {
//                "'-' to '7'" -> return bv
//                '' -> return bv
//                'J' -> return bv
//                '7' ->
//                'L' ->
//                '-' ->
//            }
//        } else {
//            when (ch0) {
//                'F' -> return bv
//                'J' -> return bv
//                '-' -> return bv
//            }
//        }
//    }


    fun String.unpack(l: Linearizer) : Pair<String, Linearizer> {
        val lines = this.windowed(l.dimensions[0], l.dimensions[0])
        val lines1 = lines.map {
            val  l = it.map { it1 ->
                when (it1) {
                    '-' -> "--"
                    'L' -> "L-"
                    'F' -> "F-"
                    else -> "${it1}x"
                } }
        val l1 = l.joinToString ( "" )
        l1}
        val lines2 = mutableListOf(lines1[0])
        for (i in 1 until lines.size) {
            val line2 = lines1[i].map {
                when (it) {
                    '|' -> '|'
                    'J' -> '|'
                    'L' -> '|'
                    else -> 'x'
                }
            }.joinToString ( "" )
            lines2.add(line2)
            lines2.add(lines1[i])
        }
        return lines2.joinToString ( "" ) to Linearizer(l.dimensions[0]*2, l.dimensions[1]*2-1)
    }

    fun print(map: String, l: Linearizer) {
        for (y in 0 until l.dimensions[1]) {

        for (x in 0 until l.dimensions[0]) {
                print("${map[l.toIndex(x,y)]}")
            }
            print("\n")
        }
    }

    fun String.fill(l: Linearizer): String {
        var s0 = this
        var s1 = s0.fillStep(l)
        while (s0 != s1) {
            s0 = s1
            s1 = s1.fillStep(l)
        }
        return s1
    }
    fun String.fillStep(l: Linearizer): String {
        return this.mapIndexed{
            ix, it ->
            val m = mapOf('x' to 'o', '.' to 'O')
            if (it in m.keys) {
                val c = l.toCoordinates(ix)
                if (c[0] == 0 || c[1] == 0 || c[0] == l.dimensions[0]-1 || c[1] == l.dimensions[1]-1) {
                    'O'
                } else {
                    val c = l.toCoordinates(ix)
                    val n = listOf(l.offset(0,1), l.offset(0,-1), l.offset(-1,0), l.offset(1,0))
                    if (n.around(ix).any{
                        this[it] in m.values}) {
                        'O'
                    } else {
                        it
                    }
                }
            } else {
                it
            }
        }.joinToString("")
    }


    fun solve() {
        val f = File("src/2023/inputs/day10.in")
        val s = Scanner(f)
        val sp = '|'
//        val s = Scanner(input1)
//        val sp = 'F'
//        val s = Scanner(input2)
//        val sp = 'F'
        var sum = 0
        var sum1 = 0
        var lineix = 0
        val lines = mutableListOf<String>()
        val sb = StringBuilder()

        var xs = 0
        var ys = 0

        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }

            if (line.contains("S")) {
                ys = lineix-1
                xs = line.indexOf('S')
            }

            lines.add(line.replace('S', sp))
            sb.append(line.replace('S', sp))
        }

        val lines1 = sb.toString()

        val x = lines[0].length
        val y = lines.size

        val l = Linearizer(x, y)

        val start = l.toIndex(xs, ys)

        var pipe = mutableListOf(start)
        var next = start.next(lines1, l, -1)
        var last = start
        while (next != start) {
            pipe.add(next)
            val last1 = next
            next = next.next(lines1, l, last)
            last = last1
        }

        val pipe1 = pipe.toSet()

        val lines2 = lines1.mapIndexed{ ix, it -> if (pipe1.contains(ix)) {it} else {'.'} }.joinToString("")

        print(lines1, l)
        print("\n")
        print(lines2, l)
        print("\n")
        print(lines2.fill(l), l)
        print("\n")

        val t = lines2.unpack(l)
        val lines3 = t.first
        val l2 = t.second

        print("${lines3.length}\n")
        print(lines3, l2)
        print("\n")

        val lines4 = lines3.fill(l2)

        print(lines4, l2)
        print("\n")

        print("${lines1.length} ${lines4.filter { it == '.' }.length} $sum1 ${pipe.size} ${pipe.size/2}\n")
    }
}