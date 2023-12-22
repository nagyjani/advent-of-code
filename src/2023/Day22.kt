package `2023`


import common.Linearizer
import java.awt.List
import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day22().solve()
}


class Day22 {

    val input1 = """
        1,0,1~1,2,1
        0,0,2~2,0,2
        0,2,3~2,2,3
        0,0,4~0,2,4
        2,0,5~2,2,5
        0,1,6~2,1,6
        1,1,8~1,1,9
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    fun drop(bricks: MutableList<Set<Int>>, l: Linearizer) {
        var updated = true
        while (updated) {
            updated = false
            for (b in bricks.indices) {
                if (fall(b, bricks, l)) {
                    updated = true
                }
            }
        }
    }

    fun print(z: Int, bricks: MutableList<Set<Int>>, m: Int, l: Linearizer) {
        val b1 = bricks.flatMap { it.toList() }.toSet()
        for (y in 0..min(m, l.dimensions[1])) {
            for (x in 0..min(m, l.dimensions[0])) {
                if (b1.contains(l.toIndex(x,y,z))) {
                    print("O")
                } else {
                    print('.')
                }
            }
            println()
        }
        println()
    }

    fun fall(b: Int, bricks: MutableList<Set<Int>>, l: Linearizer): Boolean {
        val ixs = bricks[b]
        val b1 = bricks.flatMapIndexed {ix, it -> if (ix == b) {listOf()} else { it.toList()} }.toSet()
        var ixs1 = ixs
        var updated = false
        while (true) {
            ixs1 = ixs1.map { l.offset(0, 0, -1).apply(it) }.map { it!! }.toSet()
            if (ixs1.any {
                val (x, y, z) = l.toCoordinates(it);
                        z < 1 }) {
                break
            }
            if (ixs1.any { b1.contains(it) }) {
                break
            }
            bricks[b] = ixs1
            updated = true
        }
        return updated
    }

    fun holds(b: Int, bricks: MutableList<Set<Int>>, l: Linearizer): Set<Int> {

        var r = mutableSetOf<Int>()

        bricks.forEachIndexed{
            ix, it ->
            if (ix == b) {
                false
            } else {
                val ixs1 = bricks[b].map{l.offset(0, 0, 1).apply(it)!!}
                if (ixs1.any{bricks[ix].contains(it)}) {
                    r.add(ix)
                }
            }
        }
        return r
    }

    fun rests(b: Int, bricks: MutableList<Set<Int>>, l: Linearizer): Set<Int> {
        var r = mutableSetOf<Int>()

        bricks.forEachIndexed{
                                   ix, it ->
            if (ix == b) {
                false
            } else {
                val ixs1 = bricks[b].map{l.offset(0, 0, -1).apply(it)!!}
                if (ixs1.any{bricks[ix].contains(it)}) {
                    r.add(ix)
                }
            }
        }
        return r
    }

    fun Set<Int>.allReachable(removedIx: Int, holding: Map<Int, Set<Int>>): Set<Int> {
        val removed = setOf(removedIx)
        var r = subtract(removed).toMutableSet()
        var r1 = r.reachable(holding).subtract(removed)
        while (r1.isNotEmpty()) {
            r.addAll(r1)
            r1 = r1.reachable(holding).subtract(removed)
        }
        return r
    }

    fun Set<Int>.reachable(holding: Map<Int, Set<Int>>): Set<Int> {
        val r1 = flatMap { holding[it]!! }
        return r1.subtract(this)
    }

    fun solve() {
        val f = File("src/2023/inputs/day22.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0L
        var lineix = 0
        val lines = mutableListOf<String>()
        val css = mutableListOf<Int>()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
            val cs = line.split(Regex("[,~]")).map{it.toInt()}
            css.addAll(cs)
        }

        val minC = css.min()
        val maxC = css.max()

        val  l = Linearizer(325, 325, 325)
        val bricks = css.windowed(6, 6) {
            cs ->
            val ixs = mutableSetOf<Int>()
            for (x in min(cs[0], cs[3]) .. max(cs[0], cs[3])) {
                for (y in min(cs[1], cs[4]) .. max(cs[1], cs[4])) {
                    for (z in min(cs[2], cs[5]) .. max(cs[2], cs[5])) {
                        val ix = l.toIndex(x, y, z)
                        ixs.add(ix)
                    }
                }
            }
            ixs.toSet()
        }.toMutableList()

        drop(bricks, l)

        val r0 = rests(2, bricks, l)
        val h0 = holds(2, bricks, l)

        val n = 100
        print(n, bricks, 10, l)
        print(n+1, bricks, 10, l)

        bricks.indices.forEach{
            val h = holds(it, bricks, l)
            if (h.size == 0 || h.all { it1 -> rests(it1, bricks, l).size > 1 }){
//                println("! $it ${h.size}")
                sum++
            }}
        
        val holding =
            bricks.indices.map{
                it to holds(it, bricks, l)
            }.toMap().toMutableMap()

        val bottoms = bricks.indices.filter { bricks[it].any{ix -> l.toCoordinates(ix)[2] == 1} }.toMutableSet()

        val heldby =
            bricks.indices.map{
                it to rests(it, bricks, l)
            }

        var minReachable = 1000000

        bricks.indices.forEach{
            ix ->
                val reachable = bottoms.allReachable(ix, holding)
                if (reachable.size < minReachable) {
                    minReachable = reachable.size
                }
                sum1 += bricks.size-reachable.size-1
//                println("* $ix ${reachable.size} ${bricks.size-reachable.size-1}")
        }

        print("$sum $sum1\n")
        println("* ${bricks.size-minReachable-1}")
    }
}