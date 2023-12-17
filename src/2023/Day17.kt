package `2023`


import common.Linearizer
import common.Offset
import common.around
import java.io.File
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.min

fun main() {
    Day17().solve()
}


class Day17 {

    val input1 = """
        2413432311323
        3215453535623
        3255245654254
        3446585845452
        4546657867536
        1438598798454
        4457876987766
        3637877979653
        4654967986887
        4564679986453
        1224686865563
        2546548887735
        4322674655533
    """.trimIndent()

    val input2 = """
        111111111111
        999999999991
        999999999991
        999999999991
        999999999991
    """.trimIndent()

    class Tile(val l: Linearizer, val ix: Int, val heat: Int, val mins: ArrayList<Int> = ArrayList(12)) {
        init {
            // -1 unknonwn, -2 no min
            mins.indices.forEach { mins[it] = -1 }
            val ixs = l.toCoordinates(ix)
            if (ixs[0] == 0) {
                (0..2).forEach{setMin(0, it, -2)}
            }
            if (ixs[0] == l.dimensions[0]) {
                (0..2).forEach{setMin(2, it, -2)}
            }
            if (ixs[1] == 0) {
                (0..2).forEach{setMin(1, it, -2)}
            }
            if (ixs[1] == l.dimensions[1]) {
                (0..2).forEach{setMin(3, it, -2)}
            }
        }

        fun hasMin(): Boolean {
            return mins.any{it > 0}
        }

        fun isComplete(): Boolean {
            return !mins.any{ it == -1}
        }

        fun setMin(dir: Int, history: Int, value: Int) {
            mins[dir * 3 + history] = value
        }

        fun getMin(dir: Int, history: Int): Int {
            return mins[dir * 3 + history]
        }

        fun getMin(): Triple<Int, Int, Int> {
            var r = Triple(-1 , -1,  -1)
            mins.forEachIndexed{
                it, ix ->
                    if (it >= 0 && r.first > -1 && r.first>it) {
                        r = Triple(it , ix/3 , ix%3)
                    }
            }
            return r
        }

        fun getMinTo(dir: Int): Pair<Int, Int> {
            var r = Pair(-1 ,  -1)
            mins.forEachIndexed{
                it, ix ->
                if (ix/3 == dir && it >= 0 && r.first > -1 && r.first>it) {
                    r = Pair(it, ix%3)
                }
            }
            return r
        }

        fun dirFrom(fromIx: Int): Int {
            return dir(fromIx, ix)
        }

        fun dirTo(toIx: Int): Int {
            return dir(ix, toIx)
        }
        fun dir(fromIx: Int, toIx: Int): Int {
            val (x0, y0) = l.toCoordinates(fromIx)
            val (x1, y1) = l.toCoordinates(toIx)
            if (x0 < x1) {
                return 0
            }
            if (y0 < y1) {
                return 1
            }
            if (x0 > x1) {
                return 2
            }
            if (y0 > y1) {
                return 3
            }
            throw RuntimeException()
        }

        // each set will have a larger or equal heat0 parameter
        fun setFromIx(fromIx: Int, history: Int, heat0: Int) {
            val dir = dirFrom(fromIx)
            setFromDir(dir, history, heat0)
        }
        fun setFromDir(dir: Int, history: Int, heat0: Int) {
            val min0 = getMin(dir, history)
            if (min0 == -2) {
                return
            }
            if (heat0 == -2) {
                setMin(dir, history, -2)
                setFromDir(dir, history+1, -2)
                return
            }
            val heat1 = heat0 + heat
            if (min0 == -1 || min0 > heat1) {
                setMin(dir, history, heat1)
            }
            if (history < 2) {
                setFromDir(dir, history+1, heat0)
            }
        }
    }

    fun solve(tiles: MutableList<Int>, heats: List<Int>, l: Linearizer): Int {
        val processing = tiles.indices.filter { tiles[it] > -1 }.toMutableSet()
        val finished = mutableSetOf<Int>()
        val path = MutableList(tiles.size){-1}

        solve(tiles, path, heats, processing, finished, l)

        printPath(l.dimensions[0]-1, l.dimensions[1]-1, tiles, path, l)

        val minIx = minIx(l.dimensions[0]-1, l.dimensions[1]-1, tiles, l)
        val (x, y, d, h) = l.toCoordinates(minIx)
        return tiles[minIx]
    }

    fun solve1(tiles: MutableList<Int>, heats: List<Int>, l: Linearizer): Int {
        val processing = tiles.indices.filter { tiles[it] > -1 }.toMutableSet()
        val finished = mutableSetOf<Int>()
        val path = MutableList(tiles.size){-1}

        solve1(tiles, path, heats, processing, finished, l)

        printPath(l.dimensions[0]-1, l.dimensions[1]-1, tiles, path, l)

        val minIx = minIx(l.dimensions[0]-1, l.dimensions[1]-1, tiles, l)
        val (x, y, d, h) = l.toCoordinates(minIx)
        return tiles[minIx]
    }

    fun minIx( x: Int, y: Int, tiles: MutableList<Int>, l: Linearizer): Int {
        var minIx = -1
        for (h in 0..l.dimensions[3]-1) {
            for (d in 0..3) {
                val ix = l.toIndex(x, y, d, h)
                val v = tiles[ix]
                if (v != -1 && (minIx == -1 || tiles[minIx] > v)) {
                    minIx = ix
                }
            }
        }
        return minIx
    }

    fun printPath(x: Int, y: Int, tiles: MutableList<Int>, path: MutableList<Int>, l: Linearizer) {
        var minIx = minIx(x, y, tiles, l)
        while (minIx != -1) {
            val (x1, y1, d1, h1) = l.toCoordinates(minIx)
            print("| ${tiles[minIx]}: ($x1, $y1, $d1, $h1) ")
           minIx = path[minIx]
        }
        println()
    }
    fun print(tiles: MutableList<Int>, heats: List<Int>, l: Linearizer) {
        for (y in 0 until l.dimensions[1]) {
            for (x in 0 until l.dimensions[0]) {
                val minIx = minIx(x, y, tiles, l)
                if (minIx == -1) {
                    print("${heats[l.toIndex(x, y, 0, 0)]}")
                } else {
                    val (_, _, d, h) = l.toCoordinates(minIx)
                    val ch =
                        when (d) {
                            0 -> ">"
                            1 -> "V"
                            2 -> "<"
                            3 -> "A"
                            else -> throw RuntimeException()
                        }
                    print("$ch:$h:${tiles[minIx]}|")
                }
            }
            println()
        }
        println()
    }

    tailrec fun solve(tiles: MutableList<Int>, path: MutableList<Int>, heats: List<Int>, processing: MutableSet<Int>, finished: MutableSet<Int>, l: Linearizer) {

        if (processing.isEmpty()) {
            return
        }

        val ix0 = processing.minBy { tiles[it] }
        val (x0, y0, d0, h0) = l.toCoordinates(ix0)

//        print(tiles, heats, l)

        for (d1 in 0..3) {
            val h1 =
                    if (d0 == d1) {
                        h0 + 1
                    } else {
                        0
                    }
            if (h1 > 2) {
                continue
            }
            if (abs(d0-d1) == 2) {
                continue
            }
            val ix10 =
                    when (d1) {
                        0 -> l.offset(1, 0, 0, 0).apply(ix0)
                        1 -> l.offset(0, 1, 0, 0).apply(ix0)
                        2 -> l.offset(-1, 0, 0, 0).apply(ix0)
                        3 -> l.offset(0, -1, 0, 0).apply(ix0)
                        else -> throw RuntimeException()
                    }
            if (ix10 == null) {
                continue
            }
            val (x1, y1, _, _) = l.toCoordinates(ix10)
            val ix1 = l.toIndex(x1, y1, d1, h1)
            val heat10 = tiles[ix1]
            val heat1 = tiles[ix0] + heats[ix1]
            if (tiles[ix1] == -1 || heat1 < tiles[ix1]) {
                tiles[ix1] = heat1
                path[ix1] = ix0
                processing.add(ix1)
            }
        }
        processing.remove(ix0)
        finished.add(ix0)
        solve(tiles, path, heats, processing, finished, l)
    }

    tailrec fun solve1(tiles: MutableList<Int>, path: MutableList<Int>, heats: List<Int>, processing: MutableSet<Int>, finished: MutableSet<Int>, l: Linearizer) {

        if (processing.isEmpty()) {
            return
        }

        val ix0 = processing.minBy { tiles[it] }
        val (x0, y0, d0, h0) = l.toCoordinates(ix0)

//        print(tiles, heats, l)

        for (d1 in 0..3) {
            for (hl in 4.. 10) {

                if (abs(d0 - d1) == 2 || d0 == d1) {
                    continue
                }
                val ix10 =
                        when (d1) {
                            0 -> l.offset(hl, 0, 0, 0).apply(ix0)
                            1 -> l.offset(0, hl, 0, 0).apply(ix0)
                            2 -> l.offset(-hl, 0, 0, 0).apply(ix0)
                            3 -> l.offset(0, -hl, 0, 0).apply(ix0)
                            else -> throw RuntimeException()
                        }
                if (ix10 == null) {
                    continue
                }
                val (xs, ys) = when (d1) {
                            0 -> 1..hl to 0..0
                            1 -> 0..0 to 1.. hl
                            2 -> -hl..-1 to 0..0
                            3 -> 0..0 to -hl..-1
                            else -> throw RuntimeException()
                        }

                val ixs1 = xs.flatMap { x -> ys.map { y -> x0 + x to y0 + y } }.map { l.toIndex(it.first, it.second, d1, 0 ) }
                val diff = ixs1.sumOf { heats[it] }
                val (x1, y1, _, _) = l.toCoordinates(ix10)
                val ix1 = l.toIndex(x1, y1, d1, 0)
                val heat10 = tiles[ix1]
                val heat1 = tiles[ix0] + diff
//                println("($x0, $y0, $d0, ${heats[ix0]}, ${tiles[ix0]}) -> ($x1, $y1, $d1, ${heats[ix1]}, ${tiles[ix1]}) $diff")
//                println("${ixs1.map{(l.toCoordinates(it)[0] to l.toCoordinates(it)[1]) to heats[it]}}")
                if (tiles[ix1] == -1 || heat1 < tiles[ix1]) {
                    tiles[ix1] = heat1
                    path[ix1] = ix0
                    processing.add(ix1)
                }
            }
        }
        processing.remove(ix0)
        finished.add(ix0)
        solve1(tiles, path, heats, processing, finished, l)
    }

    fun solve() {
        val f = File("src/2023/inputs/day17.in")
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

        val heats0 = lines.joinToString("")

        val l = Linearizer(lines[0].length, lineix, 4, 3)

        val heats1 = MutableList(l.size){0}

        for (x in 0 until l.dimensions[0]) {
            for (y in 0 until l.dimensions[1]) {
                for (d in 0..3) {
                    for (h in 0..2) {
                        heats1[l.toIndex(x,y,d,h)] = heats0[Linearizer(l.dimensions[0], l.dimensions[1]).toIndex(x, y)].digitToInt()
                    }
                }
            }
        }

        val tiles = MutableList(l.size){-1}
        // directions 0: right | 1: down | 2: left | 3: up
        tiles[l.toIndex(1, 0, 0, 0)] = heats1[l.toIndex(1, 0, 0, 0)]
        tiles[l.toIndex(0, 1, 1, 0)] = heats1[l.toIndex(0, 1, 0, 0)]
        val r = solve(tiles, heats1, l)

        val l1 = Linearizer(lines[0].length, lineix, 4, 1)

        val heats2 = MutableList(l1.size){0}
        for (x in 0 until l.dimensions[0]) {
            for (y in 0 until l.dimensions[1]) {
                for (d in 0..3) {
                    heats2[l.toIndex(x,y,d,0)] = heats0[Linearizer(l.dimensions[0], l.dimensions[1]).toIndex(x, y)].digitToInt()
                }
            }
        }

        val tiles1 = MutableList(l1.size){-1}
        // directions 0: right | 1: down | 2: left | 3: up
        tiles1[l.toIndex(0, 0, 0, 0)] = 0
        tiles1[l.toIndex(0, 0, 1, 0)] = 0

        val r1 = solve1(tiles1, heats2, l1)

//        for (i in listOf(
//                        l.toIndex(1,0,0,0), // 4
//                            l.toIndex(2,0,0,1), // 5
//                l.toIndex(2,1,1,0), // 6
//                l.toIndex(3,1,0,0), // 11
//                l.toIndex(4,1,0,1), // 15
//                l.toIndex(5,1,0,2), // 20
//                l.toIndex(5,0,3,0), // 23
//                l.toIndex(6,0,0,0), // 25
//                l.toIndex(7,0,0,1), // 28
//                l.toIndex(8,0,0,2), // 29
//                l.toIndex(8,0,0,2), // 29
//                l.toIndex(8,0,0,2), // 29
//                l.toIndex(8,0,0,2), // 29
//                l.toIndex(8,0,0,2), // 29
//                l.toIndex(8,0,0,2), // 29
//        )) {
//            println("${tiles[i]}")
//        }

        print("$sum $sum1 $r $r1\n")
    }
}