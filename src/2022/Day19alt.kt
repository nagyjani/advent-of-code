package `2022`

import common.BackTracker
import java.io.File
import java.util.*
import kotlin.math.min

fun main() {
    Day19alt().solve()
}


class Day19alt {

    //    0       1  2    3   4     5     6 7    8    9    10    11   12 13   14   15       16    17    18 19  20 21 22    23  24     25    26    27 28 29  30 31
    val input1 = """
        Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
        Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
    """.trimIndent()

    enum class ResourceType {
        ORE, CLAY, OBS, GEO, NO
    }

    object ResourceIterator {
        val resourceRange = ResourceType.ORE.ordinal .. ResourceType.GEO.ordinal
    }

//    fun solve(bp: o: Int)


    data class Blueprint(
        val id: Int,
        val ore2ore: Int,
        val ore2clay: Int,
        val ore2obs: Int,
        val clay2obs: Int,
        val ore2geo: Int,
        val obs2geo: Int,
    )
    data class Max(var max: Int)

    fun Blueprint.solve(minutesLeft: Int): Int {
        val max = Max(0)
//        solve(minutesLeft, 1, 0, 0, 0, 0, 0, 0, 0, max)
        solve1(minutesLeft, 1, 0, 0, 0, 0, 0, 0, max)
        return max.max
    }

    fun Blueprint.solve1(
        minLeft: Int,
        orr: Int,
        clr: Int,
        obr: Int,
        or: Int,
        cl: Int,
        ob: Int,
        ge: Int,
        max: Max
    ) {
        if (max.max < ge) {
            max.max = ge
        }
        if (minLeft == 0) {
            return
        }
        val maxGe = ge + (minLeft - 1) * minLeft / 2
        if (maxGe <= max.max) {
            return
        }
        if (minLeft == 26) {
            println("$id $max $ge")
        }
        val nor0 = or+orr
        val ncl0 = cl+clr
        val nob0 = ob+obr
        if (ore2geo <= or && obs2geo <= ob) {
            solve1(minLeft-1, orr, clr, obr, nor0-ore2geo, ncl0, nob0-obs2geo, ge+minLeft-1, max)
        }
        if (ore2obs <= or && clay2obs <= cl) {
            solve1(minLeft-1, orr, clr, obr+1, nor0-ore2obs, ncl0-clay2obs, nob0, ge, max)
        }
        if (ore2clay <= or) {
            solve1(minLeft-1, orr, clr+1, obr, nor0-ore2clay, ncl0, nob0, ge, max)
        }
        if (ore2ore <= or) {
            solve1(minLeft-1, orr+1, clr, obr, nor0-ore2ore, ncl0, nob0, ge, max)
        }
        solve1(minLeft-1, orr, clr, obr, nor0, ncl0, nob0, ge, max)
    }

    fun Blueprint.solve(
        minLeft: Int,
        orr: Int,
        clr: Int,
        obr: Int,
        ger: Int,
        or: Int,
        cl: Int,
        ob: Int,
        ge: Int,
        max: Max
    ) {
        if (minLeft == 0) {
            if (max.max < ge) {
                max.max = ge
            }
            return
        }
        val maxGe = ge + (2 * ger + minLeft - 1) * minLeft / 2
        if (maxGe <= max.max) {
            return
        }
        if (minLeft == 20) {
            println("$id $max $ge")
        }
        val nor0 = or+orr
        val ncl0 = cl+clr
        val nob0 = ob+obr
        val nge0 = ge+ger
        if (ore2ore <= or) {
            solve(minLeft-1, orr+1, clr, obr, ger, nor0-ore2ore, ncl0, nob0, nge0, max)
        }
        if (ore2clay <= or) {
            solve(minLeft-1, orr, clr+1, obr, ger, nor0-ore2clay, ncl0, nob0, nge0, max)
        }
        if (ore2obs <= or && clay2obs <= cl) {
            solve(minLeft-1, orr, clr, obr+1, ger, nor0-ore2obs, ncl0-clay2obs, nob0, nge0, max)
        }
        if (ore2geo <= or && obs2geo <= ob) {
            solve(minLeft-1, orr, clr, obr, ger+1, nor0-ore2geo, ncl0, nob0-obs2geo, nge0, max)
        }
        solve(minLeft-1, orr, clr, obr, ger, nor0, ncl0, nob0, nge0, max)
    }

    fun solve() {
        val f = File("src/2022/inputs/day19.in")
        val s = Scanner(f)
//        val s = Scanner(input1)

        var sumQ = 0L
        var prodQ = 1L
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            val words = line.split(" ")
            if (!words.isEmpty()) {
                val id = words[1].split(":")[0].toInt()
                val b =
                    Blueprint(
                        id,
                        words[6].toInt(),
                        words[12].toInt(),
                        words[18].toInt(),
                        words[21].toInt(),
                        words[27].toInt(),
                        words[30].toInt())
//                val q = b.solve(24)
//                sumQ += b.id.toLong() * q.toLong()
//                println("*** ${b.id.toLong() * q.toLong()} ${b.id} $q $sumQ")
                if (id < 4) {
                    val q1 = b.solve(32)
                    prodQ *= q1
                    println("!!! ${b.id} $q1 $prodQ")
                    if (id == 3) break
                }
            }
        }

        println("${sumQ} ${prodQ}")
    }
}

