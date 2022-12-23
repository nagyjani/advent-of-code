//package `2022`
//
//import common.BackTracker
//import java.io.File
//import java.util.*
//
//fun main() {
//    Day19().solve()
//}
//
//
//class Day19 {
//
//    //    0       1  2    3   4     5     6 7    8    9    10    11   12 13   14   15       16    17    18 19  20 21 22    23  24     25    26    27 28 29  30 31
//    val input1 = """
//        Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
//        Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
//    """.trimIndent()
//
//    enum class ResourceType {
//        ORE, CLAY, OBS, GEO, NO
//    }
//
//    object ResourceIterator {
//        val resourceRange = ResourceType.ORE.ordinal .. ResourceType.GEO.ordinal
//    }
//
//    data class Robots (val v: MutableList<Int> = mutableListOf(1, 0, 0, 0)) {
//        fun copyTo(r: Robots) {
//            for (i in ResourceIterator.resourceRange) {
//                r.v[i] = v[i]
//            }
//        }
//    }
//    data class Resources (
//        var ore: Int = 0,
//        var clay: Int = 0,
//        var obsidian: Int = 0,
//        var geode: Int = 0) {
//        fun copyTo(r: Resources) {
//            r.ore = ore
//            r.clay = clay
//            r.obsidian = obsidian
//            r.geode = geode
//        }
//    }
//
//    data class State (
//        val robots: Robots = Robots(),
//        val resources: Resources = Resources()) {
//        fun copyTo(s: State) {
//
//        }
//        fun collect() {
//            return Resources(r.ore + ore, r.clay + clay,r.obsidian + obsidian,r.geode + geode)
//        }
//    }
//
//    data class Blueprint(
//        val id: Int,
//        val ore2ore: Int,
//        val ore2clay: Int,
//        val ore2obs: Int,
//        val clay2obs: Int,
//        val ore2geo: Int,
//        val obs2geo: Int,
//    )
//
//    class RobotFactory(val b: Blueprint) {
//        fun createOre(s0: State, s1: State) {
//            if (r.ore >= b.ore2ore) {
//                return Resources(r.ore - b.ore2ore, r.clay, r.obsidian, r.geode)
//            }
//        }
//        fun createClay(s0: State, s1: State) {
//            if (r0.ore >= b.ore2clay) {
//                r1.ore = r0.ore - b.ore2clay
//                r1.clay = r0.clay
//                r1.obsidian = r0.obsidian
//                r1.geode = r0.geode
//            }
//        }
//        fun createObs(s0: State, s1: State) {
//            if (r.ore >= b.ore2obs && r.clay >= b.clay2obs) {
//                return Resources(r.ore - b.ore2obs, r.clay - b.clay2obs, r.obsidian, r.geode)
//            }
//        }
//        fun createGeo(s0: State, s1: State) {
//            if (r.ore >= b.ore2geo && r.obsidian >= b.obs2geo) {
//                return Resources(r.ore - b.ore2geo, r.clay, r.obsidian - b.obs2geo, r.geode)
//            }
//        }
//
//        fun idle(r: Resources): Resources {
//            return r.copy()
//        }
//    }
//
//    data class Max(var max: Int)
//    class BtState(
//        val resources: Resources,
//        val robots: Robots,
//        val rf: RobotFactory,
//        val minute: Int = 0,
//        val max: Max = Max(0),
//        var ix: Int = 0,
//        var nextChild: BtState? = null): BackTracker.Node<Int, BtState>(null) {
//
//        fun maxGeode(): Int {
//            val minLeft = 24-minute
//            return resources.geode + (minLeft + 2* robots.geode - 1)*minLeft/2
//        }
//        fun nextChild(): BtState? {
//            if (minute >= 24) {
//                return null
//            }
////            val maxGeode = maxGeode()
////            if (maxGeode <= max.max) {
////                return null
////            }
//            var r1: Resources? = null
//            var robots1: Robots? = null
//            while (r1 == null && ix < 4) {
//                when (ix++) {
//                    0 -> {
//                        r1 = resources.copy()
//                        robots1 = robots.copy()
//                    }
//                    1 -> {
//                        r1 = rf.createOre(resources)
//                        robots1 = Robots(robots.ore + 1, robots.clay, robots.obsidian, robots.geode)
//                    }
//
//                    2 -> {
//                        r1 = rf.createClay(resources)
//                        robots1 = Robots(robots.ore, robots.clay+1, robots.obsidian, robots.geode)
//                    }
//
//                    3 -> {
//                        r1 = rf.createObs(resources)
//                        robots1 = Robots(robots.ore , robots.clay, robots.obsidian+ 1, robots.geode)
//                    }
//
//                    else -> {
//                        r1 = rf.createGeo(resources)
//                        robots1 = Robots(robots.ore, robots.clay, robots.obsidian, robots.geode+ 1)
//                    }
//                }
//            }
//            if (r1 == null) {
//                return null
//            }
//            return BtState(robots.collect(r1), robots1!!, rf, minute+1, max)
//        }
//        init {
//            nextChild = nextChild()
//        }
//        override fun hasNextChild(): Boolean {
//            return nextChild != null
//        }
//
//        override fun getSolution(): Int? {
//            if (minute >= 24) {
//                if (resources.geode > max.max) {
//                    max.max = resources.geode
//                    return max.max
//                }
//            }
//            return null
//        }
//
//        override fun nextChild(recycledNode: BtState?, level: Int): BtState {
//            val nc = nextChild
//            nextChild = nextChild()
//            return nc!!
//        }
//
//        override fun reset(): BtState {return this}
//    }
//
//    fun solve() {
////        val f = File("src/2022/inputs/day19.in")
////        val s = Scanner(f)
//        val s = Scanner(input1)
//
//        while (s.hasNextLine()) {
//            val line = s.nextLine().trim()
//            val words = line.split(" ")
//            if (!words.isEmpty()) {
//                val id = words[1].split(":")[0].toInt()
//                val b =
//                    Blueprint(
//                        id,
//                        words[6].toInt(),
//                        words[12].toInt(),
//                        words[18].toInt(),
//                        words[21].toInt(),
//                        words[27].toInt(),
//                        words[30].toInt())
//                solve(b)
//            }
//        }
//
//        println("${true}")
//    }
//
//    fun solve(b: Blueprint) {
//        val rf = RobotFactory(b)
//        val r = Resources()
//        val rb = Robots()
//
//        val root = BtState(r, rb, rf)
//        val bt = BackTracker(root)
//        println("${b.id} ${bt.toList()}")
//    }
//}
//
