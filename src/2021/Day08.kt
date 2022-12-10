package `2021`

import java.io.File
import java.util.*

fun main() {
    Day08().solve()
}

val input = """ be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
                    edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
                    fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
                    fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
                    aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
                    fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
                    dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
                    bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
                    egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
                    gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce"""

fun Set<Char>.comp(): Set<Char> {
    return "abcdefg".toSet().subtract(this)
}

fun List<Set<Char>>.toChar(f: (Set<Char>) -> Set<Char>): Char {
    return this.map{f(it)}.filter{it.size == 1}.first().first()
}

fun  Map<Int, List<Set<Char>>>.single(i: Int): Set<Char> {
    return this[i]!![0]
}

fun Map<Int, List<Set<Char>>>.multi(i: Int): List<Set<Char>> {
    return this[i]!!
}

class Coder {
    val codeMap = mutableMapOf<Char, Char>()
    val decodeMap = mutableMapOf<Char, Char>()
    fun add(decoded: Char, coded: Char): Coder {
        codeMap[decoded] = coded
        decodeMap[coded] = decoded
        return this
    }
    fun code(s: String): Set<Char> {
        return code(s.toSet())
    }
    fun code(s: Set<Char>): Set<Char> {
        return s.map{codeMap[it]!!}.toSet()
    }
    fun decode(s: String): Set<Char> {
        return decode(s.toSet())
    }
    fun decode(s: Set<Char>): Set<Char> {
        return s.map{decodeMap[it]!!}.toSet()
    }
}

fun Set<Char>.toKey(): String {
    return this.toList().sorted().joinToString("")
}

//7 - 8
//6 - 0 (abcefg) (d) 6 (abdefg) (c) 9 (abcdfg) (e)
//5 - 2 (acdeg) (bf) 3 (acdfg) (be) 5 (abdfg) (ce)
//4 - 4 (bcdf) (aeg)
//3 - 7 (acf) (bdeg)
//2 - 1 (cf)
//
//L3 - L2 = a
//oL6 - (L4+a) = g
//cL4 - a - g = e
//cL3 - e - g - coL6 = b
//cL3 - b - e - g = d
//ocL6 - d - e = c
//L2 - c = f

class Day08 {
    fun solve() {
        val f = File("src/2021/inputs/day08.in")
        val s = Scanner(f)
//        val s = Scanner(input)
        val dm = mapOf(0 to "abcefg", 1 to "cf", 2 to "acdeg", 3 to "acdfg", 4 to "bcdf",
            5 to "abdfg", 6 to "abdefg", 7 to "acf", 8 to "abcdefg", 9 to "abcdfg").mapValues { it.value.toSet().toKey() }
        val rdm = dm.entries.associateBy({it.value }) {it.key}
        var r = mutableListOf<Int>()
        var r1 = mutableListOf<Int>()
        while (s.hasNextLine()) {
            val lineHalfs = s.nextLine().trim().split("|").map { it.trim() }
            val allDigits = lineHalfs[0].split(" ").map{it.toSet()}
            val interestingDigits = lineHalfs[1].split(" ").map{it.toSet()}
            val lm = allDigits.groupBy{it.size}
            val coder = Coder()
            coder.add('a', lm.single(3).subtract(lm.single(2)).first())
            coder.add('g', lm.multi(6).toChar{ it.subtract(lm.single(4)).subtract(coder.code("a")) })
            coder.add('e', lm.single(4).comp().subtract(coder.code("ag")).first())
            coder.add('b', lm.multi(6).toChar{lm.single(3).comp().subtract(coder.code("eg")).subtract(it.comp())})
            coder.add('d', lm.single(3).comp().subtract(coder.code("beg")).first())
            coder.add('c', lm.multi(6).toChar{it.comp().subtract(coder.code("de"))})
            coder.add('f', lm.single(2).subtract(coder.code("c")).first())
            r1.addAll(interestingDigits.map{rdm[coder.decode(it).toKey()]!!})
            r.add(interestingDigits.map{rdm[coder.decode(it).toKey()]!!.toString()}.joinToString("").toInt())
        }
        println("${r1.filter{it == 1 || it == 4 || it == 7 || it ==8}.count()} ${r.sum()}")
    }
}

//be cfbegad cbdgef(9) fgaecd cgeb fdcge agebfd fecdb fabcd edb |
//fdgacbe cefdb cefbgd gcbe
