package `2021`

import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*

fun main() {
    Day09().solve()
}

class Day09 {
    val input = """
2199943210
3987894921
9856789892
8767896789
9899965678
""".trimIndent()

    class CaveBuilder(s: Scanner) {
        val h = mutableListOf<Int>()
        var sizeX = 0
        var sizeY = 0
        init {
            while (s.hasNextLine()) {
                val line = s.nextLine().trim()
                this.add(line)
            }
        }
        fun add(s: String) {
            sizeX = s.length
            ++sizeY
            h.addAll(s.toCharArray().map{it.toString().toInt()})
        }
        fun build(): Cave {
            return Cave(h, sizeX, sizeY)
        }
    }

    class Cave(val h: List<Int>, val sizeX: Int, val sizeY: Int) {
        val basins = MutableList(h.size){-1}
        init {
            for (i in basins.indices) {
                if (basins[i] == -1 && h[i] != 9) {
                    slide(i)
                }
            }
        }
        fun neighbours4(i: Int): List<Int> {
            val r = mutableListOf<Int>()
            val x = toX(i)
            val y = toY(i)
            if (x>0) {
                r.add(i-1)
            }
            if (x<sizeX-1) {
                r.add(i+1)
            }
            if (y>0) {
                r.add(i-sizeX)
            }
            if (y<sizeY-1) {
                r.add(i+sizeX)
            }
            return r
        }
        fun neighbours8(i: Int): List<Int> {
            val r = mutableListOf<Int>()
            val x = toX(i)
            val y = toY(i)
            for (ix in max(0, x-1)..min(sizeX-1, x+1)) {
                for (jy in max(0, y-1)..min(sizeY-1, y+1)) {
                    if (ix!=x || jy!=y) {
                        r.add(jy * sizeX + ix)
                    }
                }
            }
            return r
        }
        fun toX(i: Int): Int {
            return i%sizeX
        }
        fun toY(i: Int): Int {
            return i/sizeX
        }
        fun riskMap(): List<Int> {
            return h.mapIndexed{ix, it -> if (neighbours4(ix).map{jt -> h[jt]}.all{hit -> hit>it}) {it+1} else {0} }
        }
        fun riskSum(): Int {
            return riskMap().sum()
        }
        fun slide(start: Int) {
            val s = mutableListOf<Int>()
            var nextIx = start
            var basinIx = basins[nextIx]
            while (basinIx == -1) {
                s.add(nextIx)
                nextIx = gradNext(nextIx)
                if (nextIx == -1) {
                    break
                }
                basinIx = basins[nextIx]
            }
            if (basinIx == -1) {
                basinIx = s.last()
            }
            s.forEach{basins[it] = basinIx}
        }
        fun gradNext(i: Int): Int {
            val minNextIx = neighbours4(i).map{Pair(it, h[it])}.minWithOrNull(compareBy({it.second}))!!.first
            if (h[minNextIx]<h[i]) {
                return minNextIx
            }
            return -1
        }

        fun toString(list: List<Int>): String {
            return list.windowed(size = sizeX, step = sizeX){it.joinToString("|")}.joinToString("\n")
        }
    }

    fun solve() {
        val f = File("src/2021/inputs/day09.in")
        val s = Scanner(f)
//        val s = Scanner(input)
        val cave = CaveBuilder(s).build()
        val basinSizes = cave.basins.fold(mutableMapOf<Int, Int>()){acc, it -> acc[it]=(acc[it]?:0)+1; acc}.filter{it.key != -1}.values.sorted().reversed()
        println("${cave.riskSum()} ${basinSizes[0] * basinSizes[1] * basinSizes[2]}")
    }
}