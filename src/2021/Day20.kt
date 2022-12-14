import java.io.File
import java.util.*

fun main() {
    Day20().solve()
}

class Day20 {

    val input = """
        ..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..##
        #..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###
        .######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#.
        .#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#.....
        .#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#..
        ...####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.....
        ..##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

        #..#.
        #....
        ##..#
        ..#..
        ..###
    """.trimIndent()

    class PictureBuilder {
        val rows = mutableListOf<String>()
        fun add(s: String) {
            rows.add(s)
        }
        fun build(): Picture {
            return Picture(rows, '.')
        }
    }

    class Picture(val rows: List<String>, val padding: Char) {
        val sizeX: Int get() = rows[0].length
        val sizeY: Int get() = rows.size
        fun get(x: Int, y: Int): Char {
            if (x<0 || x>=rows[0].length || y<0 || y>=rows.size) {
                return padding
            }
            return rows[y][x]
        }
        fun decode(decoder: String): Picture {
            val newRows = mutableListOf<String>()
            for (y1 in -1..sizeY) {
                val rowBuilder = StringBuilder()
                for (x1 in -1..sizeX) {
                    rowBuilder.append(decode(x1, y1, decoder))
                }
                newRows.add(rowBuilder.toString())
            }
            return Picture(newRows, decode(-2, -2, decoder))
        }
        fun decode(x: Int, y: Int, decoder: String): Char {
            // 8 7 6
            // 5 4 3
            // 2 1 0
            val index =
                (-1..1).flatMap { ity -> (-1..1).map{ itx -> itx to ity} }
                    .map{get(x+it.first, y+it.second)}
                    .map{when (it) { '.' -> 0; '#' -> 1; else -> null!!}}
                    .fold(0) {acc, it -> acc*2+it}
            return decoder[index]
        }
        override fun toString(): String {
            return rows.fold(StringBuilder("[${padding}]\n")) { sb, it -> sb.append(it).append("\n") } .toString()
        }
        fun whites(): Int {
            return rows.sumOf { it.count{it == '#'} }
        }
    }

    fun solve() {
        val f = File("src/2021/inputs/day20.in")
        val s = Scanner(f)
//        val s = Scanner(input)
        val sb = StringBuilder()
        val pb = PictureBuilder()

        while (s.hasNextLine()) {
            val nextLine = s.nextLine()
            if (sb.length < 512) {
                sb.append(nextLine)
            } else if (nextLine.isNotEmpty()) {
                pb.add(nextLine)
            }
        }

        val decoder = sb.toString()
        val picture = pb.build()
        println("$picture")
        val picture1 = picture.decode(decoder)
        println("$picture1")
        val picture2 = picture1.decode(decoder)
        println("$picture2")
        println("${picture2.whites()}")
        println("")
        var picturex = picture
        for (i in 1..50) {
            picturex = picturex.decode(decoder)
        }
        println("${picturex.whites()}")
    }
}