import java.io.File
import java.util.*

fun main() {
    Day03().solve()
}


class Day03 {

    val input1 = """
vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent()

    fun prio(c: Char): Int {
        val p1 = c-'a'+1
        val p2 = c-'A'+27
        if (p1 > 0 && p1 < 27) {
            return p1
        }
        return p2
    }

    fun solve() {
        val f = File("src/2022/inputs/aoc03.in")
        val s = Scanner(f)
//                val s = Scanner(input1)
        val backpacks = mutableListOf<String>()
        while (s.hasNextLine()) {
            backpacks.add(s.nextLine().trim())
        }
        val sum1 = backpacks.map {
            val h1 = it.subSequence(0, it.length/2)
            val h2 = it.subSequence(it.length/2, it.length)
            val intersect = h1.toSet().intersect(h2.toSet())
            val prio = intersect.sumOf { prio(it) }
//            val prio = h1.toSet().intersect(h2.toSet()).sumOf { prio(it) }
//            println("'$it' ${it.length} $h1 $h2 $intersect $prio")
            prio
        }.sum()
        val sum2 = backpacks.windowed(3, step = 3){
            val badge = it[0].toSet().intersect(it[1].toSet()).intersect(it[2].toSet())
            println("$badge ${prio(badge.first())}")
            prio(badge.first())
        }.sum()
        println("$backpacks\n$sum1 $sum2")
    }
}

