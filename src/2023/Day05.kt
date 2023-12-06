package `2023`


import java.io.File
import java.lang.RuntimeException
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day05().solve()
}


class Day05 {

    val input1 = """
        seeds: 79 14 55 13

        seed-to-soil map:
        50 98 2
        52 50 48

        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15

        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4

        water-to-light map:
        88 18 7
        18 25 70

        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13

        temperature-to-humidity map:
        0 69 1
        1 0 69

        humidity-to-location map:
        60 56 37
        56 93 4

    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    fun map(n: Long, rules: List<List<Long>>): Long {
        for (rule in rules) {
            if (n >= rule[1] && n < rule[1] + rule[2]) {
                return rule[0] - rule[1] + n
            }
        }
        return n
    }

    fun map(n: List<Interval1>, rules: List<List<Long>>): List<Interval1> {
        return n.flatMap { map(it, rules) }
    }

    fun map(n: Interval1, rules: List<List<Long>>): List<Interval1> {
        var i0s = listOf(n)
        val irs = mutableListOf<Interval1>()
        for (rule in rules) {
            val i = Interval1(rule[1], rule[2])
            val i1s = i0s.flatMap { it.minus(i) }
            irs.addAll(i0s.flatMap { it.intersect(i) }.map {
                val s1 = it.start
                val e1 = it.end()-1
                val sr = map(s1, listOf(rule))
                val er = map(e1, listOf(rule))
                Interval1(sr, it.length)
            })
            i0s = i1s
        }
        irs.addAll(i0s)
        return irs
    }

    class Interval1(val start: Long, val length: Long) {

        init {
            if (length<1) {
                throw RuntimeException()
            }
        }
        fun minus(o: Interval1): List<Interval1> {
            val start1 = max(start, o.start)
            val start0 = min(start, o.start)
            val end1 = min(end(), o.end())
            val end2 = max(end(), o.end())
            if (start1 >= end1) {
                return listOf(this)
            }
            if (start < o.start) {
                if (end() <= o.end()) {
                    return listOf(Interval1(start, o.start-start))
                }
                return listOf(Interval1(start, o.start-start), Interval1(o.end(), end()-o.end()))
            }
            if (end() <= o.end()) {
                return listOf()
            }
            return listOf(Interval1(o.end(), end()-o.end()))
        }

        fun end(): Long {
            return start + length
        }

        fun intersect(o: Interval1): List<Interval1> {
            val start1 = max(start, o.start)
            val start0 = min(start, o.start)
            val end1 = min(end(), o.end())
            val end2 = max(end(), o.end())
            if (start1 >= end1) {
                return listOf()
            }
            return listOf(Interval1(start1, end1-start1))
        }
    }

    fun solve() {
        val f = File("src/2023/inputs/day05.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        val lines = mutableListOf<String>()
        val maps = List<MutableList<List<Long>>>(7){ mutableListOf() }
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
        }

        val seeds = lines.filter{it.startsWith("seeds: ")}[0].split(Regex("[: ]+")).filter { it.all { it1 -> it1.isDigit() } }.map { it.toLong() }


        val lines1 = lines.subList(2, lines.size)

        var ix = 0
        for (l in lines1) {
            if (l.endsWith("map:")) {
                ix++
                continue
            }
            maps[ix].add(l.split(" ").map { it.toLong() })
        }

        val mappedSeeds = mutableListOf<Long>()

        val locations =
        seeds.map {
            maps.fold(it){ it1, rules -> map(it1, rules) }
        }

        val seeds1 = seeds.windowed(2,2) {listOf(Interval1(it[0], it[1]))}

        val locations1 =
                seeds1.flatMap {
                    maps.fold(it){ it1, rules -> map(it1, rules) }
                }

        val m = locations1.map { it.start }.min()

        print("$sum $sum1 ${locations.min()} $m\n")
    }
}