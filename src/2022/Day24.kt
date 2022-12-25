package `2022`

import common.Linearizer
import common.Offset
import common.around
import java.io.File
import java.util.*

fun main() {
    Day24().solve()
}


class Day24 {

    val maxTime = 10000

    val input1 = """
        #.#####
        #.....#
        #>....#
        #.....#
        #...v.#
        #.....#
        #####.#
    """.trimIndent()

    val input2 = """
        #.######
        #>>.<^<#
        #.<..<<#
        #>v.><>#
        #<^v^^>#
        ######.#
    """.trimIndent()

    enum class Dir {
        RIGHT, DOWN, LEFT, UP;
        fun turn(d: String): Dir {
            if (d == "R") {
                return Dir.values()[(ordinal+1)%4]
            }
            return Dir.values()[(ordinal+3)%4]
        }

        fun toChar(): Char {
            return when (this) {
                LEFT -> '<'
                UP -> '^'
                RIGHT -> '>'
                DOWN -> 'v'
            }
        }
    }

    fun Char.toDir(): Dir {
        var r = Dir.RIGHT
        for (d in Dir.values()) {
            if (d.toChar() == this) {
                r = d
            }
        }
        return r
    }

    data class Blizzard(val ix: Int, val dir: Dir)
    class Valley(val walls: Set<Int>, val blizzards: Map<Int, Set<Blizzard>>, val l: Linearizer) {

        val blizzardMoves =
            mapOf(
                Dir.LEFT to l.offset(-1, 0),
                Dir.RIGHT to l.offset(1, 0),
                Dir.UP to l.offset(0, -1),
                Dir.DOWN to l.offset(0, 1)
            )

        val personMoves =
            listOf(
                l.offset(-1, 0),
                l.offset(1, 0),
                l.offset(0, -1),
                l.offset(0, 1),
                l.offset(0, 0)
            )
        fun next(): Valley {
            val nextBlizzards = mutableMapOf<Int, MutableSet<Blizzard>>()
            for (b0 in blizzards) {
                for (b1 in b0.value) {
                    val b2 = move(b1)
                    if (!nextBlizzards.containsKey(b2.ix)) {
                        nextBlizzards[b2.ix] = mutableSetOf()
                    }
                    nextBlizzards[b2.ix]!!.add(b2)
                }
            }
            return Valley(walls, nextBlizzards, l)
        }

        fun move(b: Blizzard): Blizzard {
            val ix1 = blizzardMoves[b.dir]!!.apply(b.ix)!!
            if (walls.contains(ix1)) {
                val xy = l.toCoordinates(ix1)
                if (xy[0] == 0) {
                    return Blizzard(l.toIndex(l.dimensions[0]-2, xy[1]), b.dir)
                }
                if (xy[0] == l.dimensions[0]-1) {
                    return Blizzard(l.toIndex(1, xy[1]), b.dir)
                }
                if (xy[1] == 0) {
                    return Blizzard(l.toIndex(xy[0], l.dimensions[1]-2), b.dir)
                }
                if (xy[1] == l.dimensions[1]-1) {
                    return Blizzard(l.toIndex(xy[0], 1), b.dir)
                }
                println("${xy[0]} ${xy[1]}")
                throw RuntimeException()
            }
            return Blizzard(ix1, b.dir)
        }

        fun toString(p: Int?): String {
            val sb = StringBuilder()
            for (j in 0 until l.dimensions[1]) {
                for (i in 0 until l.dimensions[0]) {
                    val ix = l.toIndex(i, j)
                    if (p == ix) {
                        sb.append('X')
                    } else if (walls.contains(ix)) {
                        sb.append('#')
                    } else if (blizzards.containsKey(ix)) {
                        val n = blizzards[ix]?.size
                        when (n) {
                            null -> throw RuntimeException()
                            0 -> throw RuntimeException()
                            1 -> sb.append(blizzards[ix]?.first()!!.dir.toChar())
                            in 2..9 -> sb.append(n.toString())
                            else -> sb.append('+')
                        }
                    } else {
                        sb.append('.')
                    }
                }
                sb.appendLine()
            }
            return sb.toString()
        }

        fun next(p: Int): List<Int> {
            return personMoves.around(p).filter { open(it) }
        }

        fun open(p: Int): Boolean {
            return !walls.contains(p) && !blizzards.containsKey(p)
        }

        override fun toString(): String {
            return toString(null)
        }
    }
    class Valleys(valley: Valley) {
        val valleys: MutableList<Valley> = mutableListOf(valley)
        val l = valley.l
        val frequency = (l.dimensions[0]-2) * (l.dimensions[1]-2) // FIXME: LCM

        operator fun get(i0: Int): Valley {
            val i = i0%frequency
            while (i > valleys.size-1) {
                valleys.add(valleys.last().next())
            }
            return valleys[i]
        }

        fun code(valley: Int, ix: Int): Int {
            val c = valley * l.size + ix
            if (valley(c) != valley || ix(c) != ix) {
                throw RuntimeException()
            }
            return c
        }
        fun valley(c: Int): Int {
            return c/l.size
        }

        fun ix(c: Int): Int {
            return c % l.size
        }
    }

    class Minimums {
        val codeToMin = mutableMapOf<Int, Int>()
        val minToCode = mutableMapOf<Int, MutableSet<Int>>()
        val finished = mutableMapOf<Int, Int>()
        fun add(c: Int, v: Int) {
            var oldMin: Int? = null
            var newMin: Int? = null
            if (finished.containsKey(c)) {
                if (v < finished[c]!!) {
                    throw RuntimeException()
                }
                return
            }
            if (codeToMin.containsKey(c)) {
                if (v < codeToMin[c]!!) {
                    oldMin = codeToMin[c]
                    codeToMin[c] = v
                    newMin = v
                }
            } else {
                codeToMin[c] = v
                newMin = v
            }
            if (oldMin != null) {
                minToCode[oldMin]!!.remove(c)
            }
            if (newMin != null) {
                if (!minToCode.containsKey(newMin)) {
                    minToCode[newMin] = mutableSetOf()
                }
                minToCode[newMin]!!.add(c)
            }
        }

        fun getFinished(c: Int): Int {
            return finished[c]!!
        }

        fun minOrNull(): Int? {
            val m = minToCode.keys.minOrNull()
            if (m != null) {
                return minToCode[m]!!.first()
            }
            return null
        }

        fun finish(c: Int) {
            val m = codeToMin[c]!!
            codeToMin.remove(c)
            minToCode[m]!!.remove(c)
            if (minToCode[m]!!.isEmpty()) {
                minToCode.remove(m)
            }
            finished[c] = m
        }
    }

    fun findMin(t0: Int, startIx: Int, endIx: Int, valleys: Valleys): Int {
        val mins = Minimums()
//        val v0 = t0 % valleys.frequency
        val v0 = t0
        mins.add(valleys.code(v0, startIx), 0)
        var next = mins.minOrNull()
        while (next != null) {
            mins.finish(next)
            val minSteps = mins.getFinished(next)
            val valley0 = valleys.valley(next)
            val ix0 = valleys.ix(next)
//            println("finish $valley0 ${l.toCoordinates(ix0)[0]} ${l.toCoordinates(ix0)[1]} $minSteps")
//            println("${valleys[valley0].toString(ix0)}")
//            println("${valleys[valley0+1].next(ix0)}")
//            println("${valleys[valley0+1].toString()}")
            if (ix0 == endIx) {
                return  minSteps
            }
            for (ix in valleys[valley0+1].next(ix0)) {
//                println("add $valley0 ${l.toCoordinates(ix0)[0]} ${l.toCoordinates(ix0)[1]} $minSteps")
//                println("${valleys[valley0+1].toString(ix)}")
                mins.add(valleys.code(valley0+1, ix), minSteps+1)
            }
            next = mins.minOrNull()
        }
        throw RuntimeException()
    }

    fun solve() {
        val f = File("src/2022/inputs/day24.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)

        val walls = mutableSetOf<Int>()
        val blizzards = mutableMapOf<Int, Set<Blizzard>>()
        val lines = mutableListOf<String>()
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            if (!line.isEmpty()) {
                lines.add(line)
            }
        }
        val x = lines.first().length
        val y = lines.size
        val l = Linearizer(x, y)
        for (j in 0 until y) {
            for (i in 0 until x) {
                val c = lines[j][i]
                val ix = l.toIndex(i, j)
                when (c) {
                    '.' ->
                        Unit
                    '#' ->
                        walls.add(ix)
                    else ->
                        blizzards[ix] = setOf(Blizzard(ix, c.toDir()))
                }
            }
        }

        val valley = Valley(walls, blizzards, l)
        val valleys = Valleys(valley)

        val startPos = l.toIndex(1, 0)
        val endPos = l.toIndex(l.dimensions[0]-2, l.dimensions[1]-1)

        println("${valleys[0].toString(startPos)}")
        println("${valleys[1].toString(endPos)}")
        for (i in 0..0) {
            println("${i}")
            println("${valleys[i].toString()}")
        }

//        val mins = Minimums()
//        mins.add(valleys.code(0, startPos), 0)
//        var next = mins.minOrNull()
//        while (next != null) {
//            mins.finish(next)
//            val minSteps = mins.getFinished(next)
//            val valley0 = valleys.valley(next)
//            val ix0 = valleys.ix(next)
////            println("finish $valley0 ${l.toCoordinates(ix0)[0]} ${l.toCoordinates(ix0)[1]} $minSteps")
////            println("${valleys[valley0].toString(ix0)}")
////            println("${valleys[valley0+1].next(ix0)}")
////            println("${valleys[valley0+1].toString()}")
//            if (ix0 == endPos) {
//                println("${minSteps}")
//                break
//            }
//            for (ix in valleys[valley0+1].next(ix0)) {
////                println("add $valley0 ${l.toCoordinates(ix0)[0]} ${l.toCoordinates(ix0)[1]} $minSteps")
////                println("${valleys[valley0+1].toString(ix)}")
//                mins.add(valleys.code(valley0+1, ix), minSteps+1)
//            }
//            next = mins.minOrNull()
//        }

        println("${findMin(0, startPos, endPos, valleys)}")

        val t1 = findMin(0, startPos, endPos, valleys)
        val t2 = findMin(t1, endPos, startPos, valleys)
        val t3 = findMin(t2+t1, startPos, endPos, valleys)

        println("$t1 $t2 $t3 ${t1 + t2 + t3}")
    }
}
