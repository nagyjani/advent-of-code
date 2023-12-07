package `2023`


import java.io.File
import java.util.*

fun main() {
    Day07().solve()
}


class Day07 {

    val input1 = """
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
    """.trimIndent()

    fun String.rank1(): Long {
        val m = mutableMapOf<Char, Int>()
        forEach { m[it] = m.getOrDefault(it, 0) + 1 }
        if (m.values.max() == 5) {
            return 7
        }
        if (m.values.max() == 4) {
            return 6
        }
        if (m.values.max() == 3) {
            if (m.values.min() == 2) {
                return 5
            }
            return 4
        }
        if (m.values.max() == 2) {
            if (m.keys.size == 3) {
                return 3
            }
            return 2
        }
        return 1
    }

    fun String.rank2(r : Long): Long {
        val cards = "AKQJT98765432"
        return fold(r){acc, it -> 15L * acc + (14 - cards.indexOf(it))}
    }

    fun String.rank2j(r : Long): Long {
        val cards = "AKQT98765432J"
        return fold(r){acc, it -> 15L * acc + (14 - cards.indexOf(it))}
    }

    fun String.rankj(): Long {
        return rank2j(joker().rank1())
    }

    fun String.rank(): Long {
        return rank2(rank1())
    }

    fun String.joker() : String {
        val m = mutableMapOf<Char, Int>()
        forEach { m[it] = m.getOrDefault(it, 0) + 1 }

        if ('J' !in m.keys) {
            return this
        }

        val n = m['J']!!
        val m1 = m.toMutableMap()
        m1.remove('J')

        if (m1.size == 0) {
            return "AAAAA"
        }

        val mx = m1.values.max()
        val cs = m1.filter{it.value == mx}.keys

        val r0 = cs.map {
            this.map{
                c ->
                if (c == 'J') {
                    it
                } else {
                c }}.joinToString("") }

        r0.sortedBy { it.rank() }

        return r0[0]
    }

    val input2 = """
        
    """.trimIndent()

    class Hand(val cards: String, val bid: Long)

    fun solve() {
        val f = File("src/2023/inputs/day07.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0L
        var sum1 = 0L
        var lineix = 0
        val lines = mutableListOf<String>()
        val bids = mutableMapOf<String, Long>()
        val hands = mutableListOf<Hand>()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
        }

        lines.forEach {
            val l = it.split(" ")
            bids[l[0]] = l[1].toLong()
            hands.add(Hand(l[0], l[1].toLong()))}

        val sortedHands = bids.keys.sortedBy { it.rank() }

        for (i in sortedHands.indices) {
            val h = sortedHands[i]
            sum += (i+1) * bids[h]!!
        }

        val sortedHands2 = bids.keys.sortedBy { it.rankj() }

        for (i in sortedHands2.indices) {
            val h = sortedHands2[i]
            sum1 += (i+1) * bids[h]!!
        }

        print("$sum $sum1\n")
    }
}