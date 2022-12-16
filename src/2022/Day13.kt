package `2022`

import java.io.File
import java.lang.Math.min
import java.util.*
import kotlin.math.max

fun main() {
    Day13().solve()
}


class Day13 {

    val input1 = """
        [1,1,3,1,1]
        [1,1,5,1,1]

        [[1],[2,3,4]]
        [[1],4]

        [9]
        [[8,7,6]]

        [[4,4],4,4]
        [[4,4],4,4,4]

        [7,7,7,7]
        [7,7,7]

        []
        [3]

        [[[]]]
        [[]]

        [1,[2,[3,[4,[5,6,7]]]],8,9]
        [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent()

    data class Term(val list: MutableList<Term> = mutableListOf(), var int: Int? = null) {

        constructor(s: String): this() {
            add(s, 0)
        }
        fun add(s: String, startIx: Int): Int {
            var ix = startIx
            while (s[ix] == ',') {
                ++ix
            }
            if (s[ix] == '[') {
                if (s[ix+1] == ']') {
                    return ix+2
                }
                while (s[ix] != ']') {
                    list.add(Term())
                    ix = list.last().add(s, ix + 1)
                }
                return ix+1
            } else {
                int = s.substring(ix).takeWhile { it.isDigit() }.toInt()
                while (s[ix].isDigit()) {
                    ++ix
                }
                return ix
            }
        }

        fun isInt(): Boolean = int != null

        fun isList(): Boolean = !list.isEmpty()

        fun toList() {
            if (int == null) {
                return
            }
            list.add(Term(int = int))
            int = null
        }
    }


    fun compare(t1: Term, t2: Term): Int {
        if (t1.isInt() && t2.isInt()) {
            if (t1.int!! < t2.int!!) {
                return 1
            }
            if (t1.int!! > t2.int!!) {
                return -1
            }
            return 0
        }
        t1.toList()
        t2.toList()
        for (i in 0 until min(t1.list.size, t2.list.size)) {
            val c = compare(t1.list[i], t2.list[i])
            if (c == 0) {
                continue
            }
            return c
        }
        if (t1.list.size < t2.list.size) {
            return 1
        }
        if (t1.list.size > t2.list.size) {
            return -1
        }
        return 0
    }

    fun solve() {
        val f = File("src/2022/inputs/day13.in")
        val s = Scanner(f)
//                val s = Scanner(input1)

        var ix = 0
        var sumIx = 0

        val t01 = Term("[[2]]")
        val t02 = Term("[[6]]")
        val packets = mutableListOf(t01, t02)

        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            ++ix
            val t1 = Term(line)
            val t2 = Term(s.nextLine().trim())
            if (compare(t1, t2) > 0) {
                sumIx += ix
            }
            packets.add(t1)
            packets.add(t2)
        }

        val p1 = packets.sortedWith{ a, b -> compare(a, b) }.reversed()

        val ix1 = p1.indexOf(t01)
        val ix2 = p1.indexOf(t02)

        println("${sumIx} $ix1 $ix2 ${(ix1+1) * (ix2+1)}")
    }
}

