package `2021`

import java.io.File
import java.math.BigInteger
import java.util.*

fun main() {
    Day14().solve()
}

class Day14 {
    val input = """
        NNCB

CH -> B
HH -> N
CB -> H
NH -> C
HB -> C
HC -> B
HN -> C
NN -> C
BH -> H
NC -> B
NB -> B
BN -> B
BB -> N
BC -> B
CC -> N
CN -> C
""".trimIndent()

    fun<T> MutableMap<T, BigInteger>.incMap(key: T, num: BigInteger): MutableMap<T, BigInteger> {
        this[key] = getOrDefault(key, BigInteger.ZERO).add(num)
        return this
    }

    fun Map<String, BigInteger>.freqs(first: Char, last:Char): Map<Char, BigInteger> {
        val r = mutableMapOf<Char, BigInteger>()
        this.forEach { it.key.forEach { itc -> r.incMap(itc, it.value) } }
        return r.incMap(first, BigInteger.ONE).incMap(last, BigInteger.ONE).mapValues { it.value.divide(BigInteger("2")) }
    }

    fun Map<String, List<String>>.split(nums: Map<String, BigInteger>): MutableMap<String, BigInteger> {
        val newNums = mutableMapOf<String, BigInteger>()
        nums.forEach { this[it.key]!!.forEach{new2 -> newNums.incMap(new2, it.value) }}
        return newNums
    }

    fun solve() {
        val f = File("src/2021/inputs/day14.in")
        val s = Scanner(f)
//        val s = Scanner(input)
        val init = s.nextLine().trim()
        var nums = mutableMapOf<String, BigInteger>()
        init.windowed(2){it.toString()}.forEach { nums.incMap(it, BigInteger.ONE) }

        val t = mutableMapOf<String, List<String>>()
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            if (line.isNotEmpty()) {
                val lineTokens = line.split(" ")
                val left = lineTokens[0]
                val right = lineTokens[2]
                t.set(left, listOf(left.slice(0..0)+right, right+left.slice(1..1)))
            }
        }
        for (i in 0..40) {
            val freqs = nums.freqs(init.first(), init.last())
            val minFreq = freqs.minOf { it.value }
            val maxFreq = freqs.maxOf { it.value }
            println("${minFreq} ${maxFreq} ${maxFreq.subtract(minFreq)}")
            nums = t.split(nums)
        }

//       var counts = mutableMapOf<String, Int>()
//       init.windowed(2){val s = it.toString(); counts[s] = counts.getOrDefault(s, 0)}
//       for (i in 1..4) {
//           var nextCounts = mutableMapOf<String, Int>()
//           counts.keys) {
//               for (n in t.split(k))
//           }
//       }
    }
}