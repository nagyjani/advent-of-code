package `2023`


import common.BackTracker
import java.io.File
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day12().solve()
}


class Day12 {

    val input1 = """
        ???.### 1,1,3
        .??..??...?##. 1,1,3
        ?#?#?#?#?#?#?#? 1,3,1,6
        ????.#...#... 4,1,1
        ????.######..#####. 1,6,5
        ?###???????? 3,2,1
    """.trimIndent()

    val input2 = """
        ??????????? 3,1,1
    """.trimIndent()


    fun List<Int>.minLength() = sum() + size - 1

    fun String.solve2(ls: List<Int>): BigInteger {
        if (isEmpty()) {
            if (ls.isEmpty()) {
                return BigInteger.ONE
            }
            return BigInteger.ZERO
        }
        val m1 = indexOf('.')
        if (m1 == -1) {
            return solve3(ls)
        }
        if (m1 == length-1) {
            return substring(0, length-1).solve2(ls)
        }
        if (m1 == 0) {
            return substring(1).solve2(ls)
        }
        val s1 = substring(0, m1)
        val s2 = substring(m1 + 1)
        var r = BigInteger.ZERO
        for (i in 0 .. ls.size) {
            val ls1 = ls.subList(0, i)
            if (ls1.minLength() > s1.length) {
                break
            }
            val a = s1.solve3(ls1)
            if (a == BigInteger.ZERO) {
                continue
            }
            val ls2 = ls.subList(i, ls.size)
            if (ls2.minLength() > s2.length) {
                continue
            }
            val b = s2.solve2(ls2)
            r += a * b
        }
        return r
    }


    // only '?' and '#'
    fun String.solve3(ls: List<Int>): BigInteger {
        val m1 = indexOf('#')
        if (m1 == -1) {
            return solve4(ls)
        }
        var r = BigInteger.ZERO
        for (i in ls.indices) {
            for (j in 0 until ls[i]) {
                val startIx = m1 - j
                if (startIx < 0) {
                    continue
                }
                if (startIx > 0 && get(startIx-1) == '#') {
                    continue
                }
                val endIx = startIx + ls[i]
                if (endIx > length) {
                    continue
                }
                if (endIx < length && get(endIx) == '#') {
                    continue
                }
                if (startIx < 2 && endIx > length-2) {
                    if (ls.size == 1) {
                        r++
                    }
                    continue
                }
                if (startIx < 2) {
                    if (i == 0) {
                        val s0 = substring(endIx + 1)
                        val ls0 = ls.subList(1, ls.size)
                        r += s0.solve3(ls0)
                    }
                    continue
                }
                if (endIx > length-2) {
                    if (i == ls.size-1) {
                        val s0 = substring(0, startIx-1)
                        val ls0 = ls.subList(0, ls.size-1)
                        r += s0.solve4(ls0)
                    }
                    continue
                }
                val s1 = substring(0, startIx-1)
                val a = s1.solve4(ls.subList(0, i))
                if (a == BigInteger.ZERO) {
                    continue
                }
                val s2 = substring(endIx+1)
                val b = s2.solve3(ls.subList(i+1, ls.size))
                r += a * b
            }
        }
        return r
    }

    fun Long.factorial() = (2..this).fold(BigInteger.ONE){acc, it -> acc * BigInteger.valueOf(it)}

    fun binomial(n: Long, k: Long) = n.factorial() / (n-k).factorial() / k.factorial()

    // only '?'
    fun String.solve4(ls: List<Int>): BigInteger {
        if (ls.isEmpty()) {
            return BigInteger.ONE
        }
        val n = length - ls.minLength()
        if (n<0) {
            return BigInteger.ZERO
        }
        val k = ls.size + 1
        val b = binomial(n+k-1L, k-1L)
        return b
    }

    class SpringMap(val s: String, val l: List<Int>, val ixs: List<Int> = listOf()) {

        override fun toString(): String {
            return s + " " + l.joinToString ("," ) + " " + ixs.joinToString ("," ) + "\n" +
                    ".".repeat(ixs.getOrNull(0)?:0) + ixs.indices.map{ if (it == ixs.size-1) "#".repeat(l[it]) else "#".repeat(l[it]) + ".".repeat(ixs[it+1] - ixs[it] - l[it])}.joinToString("")
        }
    }

    fun solve() {
        val f = File("src/2023/inputs/day12.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = BigInteger.ZERO
        var sum1 = BigInteger.ZERO
        var lineix = 0
        val lines = mutableListOf<String>()
        val springMaps = mutableListOf<SpringMap>()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
            val halves = line.split(" ")
            springMaps.add(SpringMap(halves[0], halves[1].split(",").map { it.toInt() }))
        }

        val springMaps1 = springMaps.map {
            val s1 = (it.s + '?').repeat(5)
            val l1 = mutableListOf<Int>()
            (1..5).forEach{it1 -> l1.addAll(it.l)}
            SpringMap(s1.substring(0..s1.length-2), l1)
        }

        var e = 0
        for (i in springMaps) {
            val r = i.s.solve2(i.l)
            println("${e++} $r")
            sum += r
        }

        e = 0
        for (i in springMaps1) {
            val r = i.s.solve2(i.l)
            println("${e++} $r")
            sum1 += r
        }

        print("$sum $sum1\n")
    }
}