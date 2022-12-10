package `2021`

import java.io.File
import java.util.*

fun main() {
    Day03().solve()
}

data class Count(val i: Int, var all: Int = 0, var ones: Int = 0) {
    fun apply(s: String): Count {
        ++all
        if (s[i] == '1') {
            ++ones
        }
        return this
    }
    fun o2filter(ss: List<String>): List<String> {
        ss.forEach{apply(it)}
        return ss.filter{o2(it)}
    }
    fun co2filter(ss: List<String>): List<String> {
        ss.forEach{apply(it)}
        return ss.filter{co2(it)}
    }
    fun o2(s: String): Boolean {
        if (ones*2 >= all) {
            return s[i] == '1'
        }
        return s[i] == '0'
    }
    fun co2(s: String): Boolean {
        if (ones*2 >= all) {
            return s[i] == '0'
        }
        return s[i] == '1'
    }
}

fun List<String>.o2Rate(): Int {
    var l = List(this.size){this[it]}
    for (i in 0 until this.first().length) {
        l = Count(i).o2filter(l)
        if (l.size == 1) {
            return l[0].toInt(2)
        }
    }
    return l[0].toInt(2)
}

fun List<String>.co2Rate(): Int {
    var l = List(this.size){this[it]}
    for (i in 0 until this.first().length) {
        l = Count(i).co2filter(l)
        if (l.size == 1) {
            return l[0].toInt(2)
        }
    }
    return l[0].toInt(2)
}

data class BinStrCount(val length: Int) {
    var count = 0
    val oneCounts = MutableList(length){0}
    fun add(binStr: String): BinStrCount {
        binStr.forEachIndexed { ix, c -> if (c == '1') ++oneCounts[ix] }
        ++count
        return this
    }
    fun gammaRate(): Int {
        val binStr = oneCounts.mapIndexed{ix, c -> if (count/2 < c) "1" else "0" }.joinToString("")
        return binStr.toInt(2)
    }
    fun epsilonRate(): Int {
        val binStr = oneCounts.mapIndexed{ix, c -> if (count/2 < c) "0" else "1" }.joinToString("")
        return binStr.toInt(2)
    }
}

class Day03 {
    fun solve() {
        val f = File("src/2021/inputs/day03.in")
        val s = Scanner(f)
        val binStrs = mutableListOf<String>()
        while (s.hasNextLine()) {
            binStrs.add(s.nextLine().trim())
        }
        val b = binStrs.fold(BinStrCount(binStrs.first().length)){b, it -> b.add(it)}
        println("${b.epsilonRate() * b.gammaRate()} (${b.gammaRate()} * ${b.epsilonRate()})")
        println("${binStrs.o2Rate() * binStrs.co2Rate()} (${binStrs.o2Rate()} * ${binStrs.co2Rate()})")
    }
}