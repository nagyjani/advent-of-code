package `2022`

import common.Linearizer
import common.Offset
import common.*
import java.io.File
import java.util.*

fun main() {
    Day23().solve()
}


class Day23 {

    val input1 = """
        ....#..
        ..###.#
        #...#.#
        .#...##
        #.###..
        ##.#.##
        .#..#..
    """.trimIndent()

    val input2 = """
        .....
        ..##.
        ..#..
        .....
        ..##.
        .....
    """.trimIndent()


    data class Rule(val check: List<Offset>, val thenDo: Offset)
    class Offsets(val l: Linearizer) {
        fun get(dir: String): Offset {
            return when (dir) {
                "N" -> l.offset(0, -1)
                "NE" -> l.offset(1, -1)
                "NW" -> l.offset(-1, -1)
                "W" -> l.offset(-1, 0)
                "E" -> l.offset(1, 0)
                "S" -> l.offset(0, 1)
                "SE" -> l.offset(1, 1)
                "SW" -> l.offset(-1, 1)
                else -> throw RuntimeException()
            }
        }

        fun get(dir: List<String>): List<Offset> {
            return dir.map{get(it)}
        }

        fun rule(check: List<String>, apply: String): Rule {
            return Rule(get(check), get(apply))
        }
    }

    fun ranges(elves: Set<Int>, l: Linearizer): Pair<IntRange, IntRange> {
        val min = l.toCoordinates(elves.first())
        val max = l.toCoordinates(elves.first())
        for (e in elves) {
            val xy = l.toCoordinates(e)
            for (i in 0..1) {
                if (xy[i] < min[i]) {
                    min[i] = xy[i]
                }
                if (xy[i] > max[i]) {
                    max[i] = xy[i]
                }
            }
        }
        return min[0]..max[0] to min[1]..max[1]
    }
    fun render(elves: Set<Int>, l: Linearizer): String {
        val sb = StringBuilder()
        val ranges = ranges(elves, l)
        sb.appendLine(ranges.toString())
        for (y in ranges.second) {
            for (x in ranges.first) {
                if (elves.contains(l.toIndex(x,y))) {
                    sb.append("#")
                } else {
                    sb.append(".")
                }
            }
            sb.appendLine()
        }
        return sb.toString()
    }

    fun emptyTiles(elves: Set<Int>, l: Linearizer): Int {
        val ranges = ranges(elves, l)
        var emptyTiles = 0
        for (y in ranges.second) {
            for (x in ranges.first) {
                if (!elves.contains(l.toIndex(x,y))) {
                    ++emptyTiles
                }
            }
        }
        return emptyTiles
    }

    fun spread(elves: Set<Int>, startRule: Int, rules: List<Rule>, allAdjacent: List<Offset>, l: Linearizer): Pair<Set<Int>, Boolean> {
        val oldToNewPos = mutableMapOf<Int, Int>()
        val newToOldPos = mutableMapOf<Int, Int>()
        val conflicting = mutableSetOf<Int>()
        for (e in elves) {
            var e1 = e
            if (allAdjacent.around(e).any{elves.contains(it)}) {
                for (i in 0..3) {
                    val rix = (i + startRule) % 4
                    val rule = rules[rix]
                    if (rule.check.around(e).all { !elves.contains(it) }) {
                        e1 = rule.thenDo.apply(e)!!
                        break
                    }
                }
            }
            oldToNewPos[e] = e1
            if (newToOldPos.contains(e1)) {
                conflicting.add(e)
                conflicting.add(newToOldPos[e1]!!)
            }
            newToOldPos[e1] = e
        }
        var updated = false
        val elves1 =
            elves.map{
                if (conflicting.contains(it)) {
                    it
                } else {
                    val newPos = oldToNewPos[it]!!
                    if (newPos != it) {
                        updated = true
                    }
                    newPos
                }
            }.toSet()
        return elves1 to updated
    }

    fun solve() {
        val startOffset = 1000
        val dimension = 2000

        val f = File("/home/janos/Downloads/day23.in")
        val s = Scanner(f)
//        val s = Scanner(input1)

        val l = Linearizer(dimension, dimension)
        val o = Offsets(l)

        val allAdjacent =
            o.get(listOf("NW", "N", "NE", "W", "E", "SW", "S", "SE"))

        val rules =
            listOf(
                o.rule(listOf("N", "NE", "NW"), "N"),
                o.rule(listOf("S", "SE", "SW"), "S"),
                o.rule(listOf("W", "NW", "SW"), "W"),
                o.rule(listOf("E", "NE", "SE"), "E")
            )

        val elves = mutableSetOf<Int>()
        var y0 = 0
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            for (x0 in 0 until line.length) {
                if (line[x0] == '#') {
                    elves.add(l.toIndex(x0 + startOffset, y0 + startOffset))
                }
            }
            ++y0
        }

        var ruleIx = 0

        var round = 1
        var  r = spread(elves, ruleIx, rules, allAdjacent, l)
//        while (r.second && round<11) {
        while (r.second) {
            ++ruleIx
            println("After round ${round++}")
            println(render(r.first, l))
            println(emptyTiles(r.first, l))
            r = spread(r.first, ruleIx, rules, allAdjacent, l)
        }

        println(render(r.first, l))
    }
}

