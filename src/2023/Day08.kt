package `2023`


import java.io.File
import java.lang.RuntimeException
import java.math.BigInteger
import java.util.*

fun main() {
    Day08().solve()
}


class Day08 {

    val input1 = """
        RL

        AAA = (BBB, CCC)
        BBB = (DDD, EEE)
        CCC = (ZZZ, GGG)
        DDD = (DDD, DDD)
        EEE = (EEE, EEE)
        GGG = (GGG, GGG)
        ZZZ = (ZZZ, ZZZ)

    """.trimIndent()

    val input2 = """
        LLR

        AAA = (BBB, BBB)
        BBB = (AAA, ZZZ)
        ZZZ = (ZZZ, ZZZ)

    """.trimIndent()


    val input3 = """
   LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)
    """.trimIndent()


    fun steps(instructions: String, m: Map<String, Pair<String, String>>, start: String, goals: Set<String>): Long {

        var i = 0L
        var p = start
        val reached = mutableMapOf<String,MutableSet<Long>>()
//        while (!goals.contains(p)) {
        while (true) {
            val i1 = (i % instructions.length).toInt()
            when (instructions[i1]) {
                'L' -> p = m[p]!!.first
                'R' -> p = m[p]!!.second
            }
            i++
            if (p.endsWith('Z')) {
                val s = reached.getOrDefault(p, mutableSetOf())
                if (s.any{ (i-it) % instructions.length.toLong() == 0L}) {
                    if (s.size == 1 && s.first() * 2 == i) {
                        return i/2
                    }
                    break
                }
                s.add(i)
                reached[p] = s
            }
        }

        return i
    }


    fun solve() {
        val f = File("src/2023/inputs/day08.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
//        val s = Scanner(input3)

        var sum = 0
        var sum1 = 0
        var lineix = 0
        val lines = mutableListOf<String>()

        val m = mutableMapOf<String, Pair<String, String>>()

        val instructions = s.nextLine().trim()

        val starts = mutableSetOf<String>()

        val goals =  mutableSetOf<String>()

        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
            val l1 = line.split(Regex("[,= \\(\\)]")).filter{it.isNotEmpty()}
            m[l1[0]] = Pair(l1[1], l1[2])
            when (l1[0].last()) {
                'A' -> starts.add(l1[0])
                'Z' -> goals.add(l1[0])
            }
        }

        val lengths = starts.map {
            steps(instructions, m, it, goals)
            }

        val r = lengths.map{BigInteger(it.toString())}.fold(BigInteger.ONE){acc, it ->
            lcm(acc, it)
        }

        print("$r\n")

//        var i1 = 0L
//        var p1 = starts.toSet()
//        while (!p1.all{it.last() == 'Z'}) {
//            val i2 = (i1 % instructions.length).toInt()
//            p1 =
//            p1.map {
//                when (instructions[i2]) {
//                    'L' -> m[it]!!.first
//                    'R' -> m[it]!!.second
//                    else -> {throw RuntimeException()}
//                }
//            }.toSet()
//            i1++
//            if (i1 % 1000000 == 0L) {
//                print("$i1 $p1 ${instructions.length} ${m.size}\n")
//            }
//        }
//
//        print("$i1\n")
//        print("${steps(instructions, m, "AAA", "ZZZ")}\n")
    }

    fun lcm(l1: BigInteger, l2: BigInteger): BigInteger {

        return l1 * l2 / gcd(l1, l2)
    }
    fun gcd(l1: BigInteger, l2: BigInteger): BigInteger {
        var l11 = l1
        var l22 = l2
        while (l11 != l22) {
            if (l11 > l22) {
                if (l11 % l22 == BigInteger.ZERO) {
                    return l22
                }
                l11 = l11 % l22
            } else {
                if (l22 % l11 == BigInteger.ZERO) {
                    return l11
                }
                l22 = l22 % l11
            }
        }
        return l11
    }
}