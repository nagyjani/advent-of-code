package `2023`


import common.Linearizer
import java.io.File
import java.lang.RuntimeException
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day18().solve()
}


class Day18 {

    val input1 = """
        R 6 (#70c710)
        D 5 (#0dc571)
        L 2 (#5713f0)
        D 2 (#d2c081)
        R 2 (#59c680)
        D 2 (#411b91)
        L 5 (#8ceee2)
        U 2 (#caa173)
        L 1 (#1b58a2)
        U 2 (#caa171)
        R 2 (#7807d2)
        U 3 (#a77fa3)
        L 2 (#015232)
        U 2 (#7a21e3)
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    fun Pair<Int, Int>.apply(xy: Pair<Int, Int>): Pair<Int, Int> {
        val (d, n) = this
        var (x1, y1) = xy
        when (d) {
            0 -> x1 += n
            1 -> y1 += n
            2 -> x1 -= n
            3 -> y1 -= n
        }
        return x1 to y1
    }

    fun Pair<Int, Int>.apply(xy0: Pair<Int, Int>, tiles: MutableList<Char>, l: Linearizer): Pair<Int, Int> {
        val (x0, y0) = xy0

        val (x1,y1) =  apply(xy0)

        val (d, n) = this

        val ix0 = l.toIndex(x0, y0)
        val ix1 = l.toIndex(x1, y1)

        tiles[ix0] = '*'
        tiles[ix1] = '*'

        when (d) {
            0 ->
                for (x in x0+1 until x1) tiles[l.toIndex(x, y0)] = '-'
            1 ->
                for (y in y0+1 until y1) tiles[l.toIndex(x0, y)] = '|'
            2 ->
                for (x in x1+1 until x0) tiles[l.toIndex(x, y0)] = '-'
            3 ->
                for (y in y1+1 until y0) tiles[l.toIndex(x0, y)] = '|'
            else -> throw RuntimeException()
        }

//        print(tiles, l)

        return x1 to y1
    }

    fun print(map: List<Char>, l: Linearizer) {
        for (y in 0 until l.dimensions[1]) {
            for (x in 0 until l.dimensions[0]) {
                print("${map[l.toIndex(x,y)]}")
            }
            print("\n")
        }
        println()
    }

    fun solve() {
        val f = File("src/2023/inputs/day18.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        val lines = mutableListOf<String>()
        val cmds = mutableListOf<Pair<Int, Int>>()
        val cmds1 = mutableListOf<Pair<Int, Int>>()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
            val (d, n, h) = Regex("(.+) (.+) \\(#(.+)\\)").find(line)!!.destructured
            cmds.add(d.let { when (it) {"R" -> 0; "D" -> 1; "L" -> 2; "U" -> 3; else -> throw RuntimeException()}} to n.toInt())

            val d1 = h.last().digitToInt()
            val h1 = h.substring(0, h.length-1)
            val n1 = h1.toInt(16)
            cmds1.add(d1 to n1)
        }
//
//        val points0 = mutableListOf<Pair<Int, Int>>()
//        cmds.fold(0 to 0){p, cmd -> val p1 = cmd.apply(p); points0.add(p1); p1}
//
//        val minX = points0.minOf { it.first }
//        val maxX = points0.maxOf { it.first }
//        val minY = points0.minOf { it.second }
//        val maxY = points0.maxOf { it.second }
//
//        val startPoint = -1 * minX + 1 to -1 * minY + 1
//        val l = Linearizer(maxX - minX + 3, maxY - minY + 3)
//
//        val tiles = MutableList(l.size){'.'}
////        print(tiles, l)
//
//        val points1 = mutableListOf<Pair<Int, Int>>()
//        cmds.fold(startPoint){p, cmd -> val p1 = cmd.apply(p, tiles, l); points1.add(p1); p1}
////        print(tiles, l)
//
//        for (i in cmds.indices) {
//            val (x, y) = points1[i]
//            val ix = l.toIndex(x, y)
//            val d = cmds[i].first
//            val d1 = cmds[(i+1)%cmds.size].first
//            val corner =
//                when (d to d1) {
//                    0 to 0 -> '-'
//                    0 to 1 -> 'i'
//                    0 to 3 -> '!'
//                    1 to 0 -> '!'
//                    1 to 1 -> '|'
//                    1 to 2 -> '!'
//                    2 to 1 -> 'i'
//                    2 to 2 -> '-'
//                    2 to 3 -> '!'
//                    3 to 0 -> 'i'
//                    3 to 1 -> '|'
//                    3 to 2 -> 'i'
//                    else -> 'K'
//                }
//            tiles[ix] = corner
//        }
//
////        print(tiles, l)
//
//        for (y in 0 until l.dimensions[1]) {
//            // 0 - out
//            // 1 - in
//            var w = 0
//            var lastNonDash = 'K'
//            for (x in 0 until l.dimensions[0]) {
//                val ix = l.toIndex(x, y)
//                if (tiles[ix] == '.') {
//                    if (w == 1) {
//                        tiles[ix] = ' '
//                    }
//                } else if (tiles[ix] == '|') {
//                    w = 1 - w
//                } else if (tiles[ix] == '-') {
//                    continue
//                } else if (tiles[ix] == 'i' && lastNonDash == '!' || tiles[ix] == '!' && lastNonDash == 'i') {
//                    w = 1-w
//                }
//                lastNonDash = tiles[ix]
//            }
//        }
//
//        print(tiles, l)
//
//        sum = tiles.count { it != '.' }
//
//        print("$sum $sum1\n")
//
//        val cmds2 = cmds.toMutableList()

        var area = cmds1.area(1)
//        var area2 = cmds1.area(-1)

        print("$area\n")
    }

    fun List<Pair<Int, Int>>.area(dir: Int): Long {
        var a = 0L
        val cmds1 = toMutableList()
        while (cmds1.size != 2) {
            while (cmds1.removeLineDots()) {}
            a += cmds1.removeRectangles(dir)
        }
        a += cmds1[0].second + 1
        return a
    }

    fun MutableList<Pair<Int, Int>>.removeLineDots(): Boolean {
        for (i in 0 until size) {
            val i1 = (i+1)%size
            if (get(i).first == get(i1).first) {
                set(i, get(i).first to get(i).second + get(i1).second)
                removeAt(i1)
                return true
            }
        }
        return false
    }

//    fun MutableList<Pair<Int, Int>>.removeBackAndForth(dir: Int): Long {
//
//        var ii = -1
//        var sign = 0L
//        for (i in 0 until size) {
//            val (d, n) = get(i)
//            val i1 = (i + 1) % size
//            val (d1, n1) = get(i1)
//            if ((d + 2) % 4 == d1) {
//                ii = i
//                sign = 1L * dir
//                break;
//            }
//            if ((d1 + 2) % 4 == d) {
//                ii = i
//                sign = 1L * dir
//                break;
//            }
//
//                if (n > n1) {
//                    set(i, d to n-n1)
//                    removeAt(i1)
//                    sign * (n1 + dir.toLong()) * n2
//                } else if (n2 > n) {
//                    set(i2, d2 to n2-n)
//                    removeAt(i)
//                    sign * (n1 + sign) * n
//                } else {
//                    removeAt(max(i, i2))
//                    removeAt(min(i, i2))
//                    sign * (n1 + sign) * n
//                }
//            }
//        }
//
//    }

    fun MutableList<Pair<Int, Int>>.removeRectangles(dir: Int): Long {
        var a = 0L
        var imin = -1
        var n1min = 0
        var sign = 0L
        for (i in 0 until size) {
            val (d, n) = get(i)
            val i1 = (i+1)%size
            val (d1, n1) = get(i1)
            val i2 = (i+2)%size
            val (d2, n2) = get(i2)
            if ((d+1)%4 == d1 && (d1+1)%4 == d2) {
                if (imin == -1 || n1<n1min) {
                    imin = i
                    n1min = n1
                    sign = 1L
                }
            }
            if ((d2+1)%4 == d1 && (d1+1)%4 == d) {
                if (imin == -1 || n1<n1min) {
                    imin = i
                    n1min = n1
                    sign = -1L
                }
            }
        }

        val sign1 = sign * dir

        if (imin != -1) {
            val i = imin
            val (d, n) = get(i)
            val i1 = (i+1)%size
            val (d1, n1) = get(i1)
            val i2 = (i+2)%size
            val (d2, n2) = get(i2)
            val toRemoveIxs = mutableListOf<Int>()
            var r = if (n > n2) {
                    set(i, d to n-n2)
                    val i3 = (i+3)%size
                    val (d3, n3) = get(i3)
                    if ((d2 + sign.toInt() + 4)%4 == d3) {
                        set(i2, d2 to 0)
                    } else {
                        toRemoveIxs.add(i2)
                    }
                    sign1 * (n1 + sign1) * n2
                } else if (n2 > n) {
                    set(i2, d2 to n2-n)
                    val i01 = (i+size-1)%size
                    val (d01, n01) = get(i01)
                    if ((d - sign.toInt() + 4)%4 == d01) {
                        set(i, d to 0)
                    } else {
                        toRemoveIxs.add(i)
                    }
                    sign1 * (n1 + sign1) * n
                } else {
                    val i3 = (i+3)%size
                    val (d3, n3) = get(i3)
//                    if ((d2 + sign.toInt() + 4)%4 == d3) {
//                        set(i2, d2 to 0)
//                    } else {
                        toRemoveIxs.add(i2)
//                    }
                    val i01 = (i+size-1)%size
                    val (d01, n01) = get(i01)
//                    if ((d - sign.toInt() + 4)%4 == d01) {
//                        set(i, d to 0)
//                    } else {
                        toRemoveIxs.add(i)
//                    }
                    sign1 * (n1 + sign1) * n
                }
            toRemoveIxs.sorted().reversed().forEach { removeAt(it) }
            return r
        }
        return 0L
    }
}