package `2023`


import common.Linearizer
import java.io.File
import java.lang.RuntimeException
import java.util.*

fun main() {
    Day16().solve()
}


class Day16 {

    val input1 = """
        .|...\....
        |.-.\.....
        .....|-...
        ........|.
        ..........
        .........\
        ..../.\\..
        .-.-/..|..
        .|....-|.\
        ..//.|....
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    // directions | 0:right | 1:down | 2:left | 3:up |
    fun next(ix: Int, dir: Int, l: Linearizer): Int? {
        val offset =
            when (dir) {
                0 -> l.offset(1, 0)
                1 -> l.offset(0, 1)
                2 -> l.offset(-1, 0)
                3 -> l.offset(0, -1)
                else -> throw RuntimeException()
            }
        return offset.apply(ix)
    }

    // directions | 0:right | 1:down | 2:left | 3:up |
    fun nextDir(dir: Int, tile: Char): List<Int> {
        val d =
            when (tile to dir) {
                '.' to 0 -> listOf(dir)
                '.' to 1 -> listOf(dir)
                '.' to 2 -> listOf(dir)
                '.' to 3 -> listOf(dir)
                '-' to 0 -> listOf(dir)
                '-' to 1 -> listOf(0, 2)
                '-' to 2 -> listOf(dir)
                '-' to 3 -> listOf(0, 2)
                '|' to 0 -> listOf(1, 3)
                '|' to 1 -> listOf(dir)
                '|' to 2 -> listOf(1, 3)
                '|' to 3 -> listOf(dir)
                '/' to 0 -> listOf(3)
                '/' to 1 -> listOf(2)
                '/' to 2 -> listOf(1)
                '/' to 3 -> listOf(0)
                '\\' to 0 -> listOf(1)
                '\\' to 1 -> listOf(0)
                '\\' to 2 -> listOf(3)
                '\\' to 3 -> listOf(2)
                else -> throw RuntimeException()
            }
        return d
    }

    fun beam(ix: Int, dir: Int, tiles: String, l: Linearizer, visited: MutableSet<Int>) {
        val c = l.toCoordinates(ix)

        if (visited.contains(ix*4 + dir)) {
            return
        }
        visited.add(ix*4 + dir)

        val nextIxs = nextDir(dir, tiles[ix]).map { next(ix, it, l) to it }.filter { it.first != null }

        nextIxs.forEach { beam(it.first!!, it.second, tiles, l, visited) }
    }

    fun energy(startIx: Int, startDir: Int, tiles: String, l: Linearizer): Int {

        val visited = mutableSetOf<Int>()
        beam(startIx, startDir, tiles, l, visited)
        val visited1 = mutableSetOf<Int>()
        visited.forEach{visited1.add(it/4)}
        return visited1.size
    }

    fun solve() {
        val f = File("src/2023/inputs/day16.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
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

        val l = Linearizer(lines[0].length, lineix)
        val tiles = lines.joinToString("")

        // directions 0: right 1: down 2: left 3: up
        val startDir = 0
        val startPos = 0

        var max = 0
        for (x in 0 until l.dimensions[0]) {
            val e = energy(l.toIndex(x, 0), 1, tiles, l)
            if (e > max) {
                max = e
            }
        }
        for (x in 0 until l.dimensions[0]) {
            val e = energy(l.toIndex(x, l.dimensions[1]-1), 3, tiles, l)
            if (e > max) {
                max = e
            }
        }
        for (y in 0 until l.dimensions[1]) {
            val e = energy(l.toIndex(0, y), 0, tiles, l)
            if (e > max) {
                max = e
            }
        }
        for (y in 0 until l.dimensions[1]) {
            val e = energy(l.toIndex(l.dimensions[0]-1, y), 2, tiles, l)
            if (e > max) {
                max = e
            }
        }

        print("$max ${energy(startPos, startDir, tiles, l)}\n")
    }
}