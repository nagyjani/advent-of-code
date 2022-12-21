//package `2022`
//
//import common.BackTracker
//import java.io.File
//import java.util.*
//
//fun main() {
//    Day16().solve()
//}
//
//
//class Day16 {
//
//    val input1 = """
//                Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
//                Valve BB has flow rate=13; tunnels lead to valves CC, AA
//                Valve CC has flow rate=2; tunnels lead to valves DD, BB
//                Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
//                Valve EE has flow rate=3; tunnels lead to valves FF, DD
//                Valve FF has flow rate=0; tunnels lead to valves EE, GG
//                Valve GG has flow rate=0; tunnels lead to valves FF, HH
//                Valve HH has flow rate=22; tunnel leads to valve GG
//                Valve II has flow rate=0; tunnels lead to valves AA, JJ
//                Valve JJ has flow rate=21; tunnel leads to valve II
//    """.trimIndent()
//
//    data class Valve(val name: String, val flow: Long, val nexts: MutableMap<String, Int>)
//
//
//
//    fun MutableMap<String, Valve>.popBroken(startName: String): Valve? {
//        return firstNotNullOfOrNull { if (it.value.name != startName && it.value.flow == 0L) {it.value} else {null} }?. also { this.remove(it.name) }
//    }
//
//    class Max(var max: Long)
//
//    class BtState(
//        p: BtState?,
//        val valves: Map<String, Valve>,
//        val minDistances: Map<String, Int>,
//        val current: String = "AA",
//        val unvisited: Set<String> = valves.keys.toMutableSet().apply { remove("AA") },
//        val initMinutes: Int = 30,
//        val minutesLeft: Int = initMinutes,
//        val flow: Long = 0,
//        val max: Max = Max(0L),
//        val walkers: Int = 1,
//        var walkerIx: Int = 0,
//        var nextIx: Int = 0): BackTracker.Node<Long, BtState>(p) {
//
//        val valve = valves[current]!!
//        val nexts = unvisited.filter { minDistances[current + it]!! < minutesLeft }.toList()
//
//        init {
//            println(toString())
//        }
//        override fun hasNextChild(): Boolean {
//            return nextIx < nexts.size || walkerIx < walkers
//        }
//
//        override fun getSolution(): Long? {
//            if (flow > max.max) {
//                max.max = flow
//                return flow
//            }
//            return null
//        }
//
//        override fun reset(): BtState {
//            return this.also { it.nextIx = 0 }
//        }
//
//        override fun nextChild(recycledNode: BtState?, level: Int): BtState {
//            if (nextIx >= nexts.size) {
//                return BtState(
//                    this,
//                    valves,
//                    minDistances,
//                    "AA",
//                    unvisited,
//                    initMinutes,
//                    initMinutes,
//                    flow)
//            }
//            val nextValve = valves[nexts[nextIx++]]!!
//            val distance = minDistances[current + nextValve.name]!!
//            val nextMinutesLeft = minutesLeft - distance - 1
//            return BtState(
//                this,
//                valves,
//                minDistances,
//                nextValve.name,
//                unvisited.toMutableSet().apply { remove(nextValve.name) },
//                initMinutes,
//                nextMinutesLeft,
//                flow + nextValve.flow * nextMinutesLeft)
//        }
//
//        override fun toString(): String {
//            return parent.toString() + " (" + current  + ", flow: " + flow + ", left: " + minutesLeft + " [" + walkerIx + "/" + walkers + "])"
//        }
//    }
//
//
//    class Solver(
//        val n: Int,
//        val minDistances: List<Long>,
//        val flows: List<Long>,
//        val start: Long,
//        val minutes: Long,
//        val names: List<String>,
//        val elephantHelp: Boolean = false
//    ) {
//
//        val maxes = mutableMapOf<Long, Long>()
//        val noVisits = 0b0L
//        val visitedBitMask: Long // = 0b1111111111111111L
//        init {
//            var m = 0b1L
//            for (i in 1 until n) {
//                m = (m shl 1) or 0b1L
//            }
//            visitedBitMask = m
//        }
//        val minutesLeftBitMask = 0b11111L
//        val posBitMask = 0b1111L
//        val flowBitMask = 0b11111111L
//
//        init {
//            if (minutes > minutesLeftBitMask || n-1 > posBitMask || flows.sum() > flowBitMask) {
//                throw RuntimeException()
//            }
//        }
//
//
//
//        fun distance(a: Long, b: Long): Long {
//            return minDistances[a.toInt() * n + b.toInt()]
//        }
//
//        fun flow(a: Long): Long {
//            return flows[a.toInt()]
//        }
//
//        fun max(): Long {
//            // assuming the starting position does not have a working valve
//            return getMax(minutes, start, noVisits.visit(start), false, 0, listOf(start))
//        }
//
//        class UnvisitedPosIterator(visited: Long, visitedBitMask: Long): Iterator<Long> {
//            var unvisited = visited xor visitedBitMask
//            var nextIx = 0L
//            override fun hasNext(): Boolean {
//                return unvisited != 0b0L
//            }
//            override fun next(): Long {
//                while (unvisited and 0b1L == 0b0L) {
//                    ++nextIx
//                    unvisited = unvisited shr 1
//                }
//                val r = nextIx
//                ++nextIx
//                unvisited = unvisited shr 1
//                return r
//            }
//
//            fun getNext() {
//                while ((unvisited and 0b1L) == 0b0L) {
//                    ++nextIx
//                    unvisited = unvisited shr 1
//                }
//            }
//        }
//
//        fun Long.unvisited(): Iterator<Long> {
//            return UnvisitedPosIterator(this, visitedBitMask)
//        }
//
//        fun Long.visit(pos: Long): Long {
//            return this or (0b1L shl pos.toInt())
//        }
//
//        fun Long.name(): String {
//            return names[this.toInt()]
//        }
//
//        fun getMax(minutesLeft: Long, pos: Long, visited: Long, elephant: Boolean, flow: Long, path: List<Long>): Long {
//
//            if (elephant && !elephantHelp) {
//                return 0L
//            }
////            println("path: ${path.map{it.name()}.joinToString(",")}")
////            println("unvisited: ${visited.unvisited().asSequence().map { it.name() }.joinToString(",")}")
//
//            val state = toState(minutesLeft, pos, visited, elephant, flow)
//            if (minutesLeft != state.getMinutesLeft() || pos != state.getPos() || visited != state.getVisited() || elephant != state.isElephant() || flow != state.getFlow()) {
//                throw RuntimeException()
//            }
//            if (maxes.containsKey(state)) {
//                return maxes[state]!!
//            }
//            var max = flow * minutesLeft
//            if (!elephant) {
//                val m0 = flow * minutesLeft
//                // assuming the starting position does not have a working valve
//                val m1 = getMax(minutes, start, visited, true, 0, path.toMutableList().apply{add(start)})
//                if (m0 + m1 > max) {
//                    max = m0 + m1
//                }
//            }
//            for (u in visited.unvisited()) {
//                val d = distance(pos, u)
//                if (d >= minutesLeft) {
//                    continue
//                }
//                val flow1 = flow + flow(u)
//                val m0 = (d+1) * flow
//                val m1 = getMax(minutesLeft-d-1, u, visited.visit(u), elephant, flow1, path.toMutableList().apply{add(u)})
//                if (m0 + m1 > max) {
//                    max = m0 + m1
//                }
//            }
////            println("path: ${path.map{it.name()}.joinToString(",")}")
////            println("minLeft: $minutesLeft, pos: $pos, elephant: $elephant, flow: $flow")
////            println("unvisited: ${visited.unvisited().asSequence().map { it.name() }.joinToString(",")}")
////            println("state: $state, max: $max")
//            maxes[state] = max
//            return max
//        }
//        fun toState(minutesLeft: Long, pos: Long, visited: Long, elephant: Boolean, flow: Long): Long {
//            // visited: 0..15
//            // minutes left: 16..20
//            // pos: 21..24
//            // elephant: 25
//            // flow: 26..33
//            if (minutesLeft > minutesLeftBitMask || pos > posBitMask || visited > visitedBitMask || flow > flowBitMask) {
//                throw RuntimeException()
//            }
//            return (flow shl 26) or (elephant.compareTo(false).toLong() shl 25) or (pos shl 21) or (minutesLeft shl 16) or visited.visit(pos)
//        }
//        fun Long.getPos(): Long {
//            return posBitMask and (this shr 21)
//        }
//        fun Long.getMinutesLeft(): Long {
//            return minutesLeftBitMask and (this shr 16)
//        }
//        fun Long.getVisited(): Long {
//            return visitedBitMask and this
//        }
//        fun Long.isElephant(): Boolean {
//            return (0b1L and (this shr 25)) == 0b1L
//        }
//        fun Long.getFlow(): Long {
//            return flowBitMask and (this shr 26)
//        }
//    }
//
//
//    fun solve() {
//        val f = File("src/2022/inputs/day16.in")
//        val s = Scanner(f)
////        val s = Scanner(input1)
//
//        val valves = mutableMapOf<String, Valve>()
//
//        while (s.hasNextLine()) {
//            val line = s.nextLine().trim()
//            val words = line.split(" ")
//            if (!words.isEmpty()) {
//                val valve =
//                    Valve(
//                        words[1],
//                        words[4].split(Regex("[=;]"))[1].toLong(),
//                        words.subList(9, words.size).joinToString("").split(",").map{it to 1}.toMap().toMutableMap())
//                valves[valve.name] = valve
//                println("${words}")
//                println("${valve}")
//            }
//        }
//
//        val startName = "AA"
//        var brokenValve = valves.popBroken(startName)
//        while (brokenValve != null) {
//            for (v1n in brokenValve.nexts) {
//                val v1 = valves[v1n.key]!!
//                for (v2n in brokenValve.nexts) {
//                    if (v1n.key < v2n.key) {
//                        println("$v1n $v2n")
//                        val v2 = valves[v2n.key]!!
//                        val d = v1n.value + v2n.value
//                        if (!v1.nexts.containsKey(v2n.key) || v1.nexts[v2n.key]!! > d) {
//                            v1.nexts[v2n.key] = d
//                            v1.nexts.remove(brokenValve.name)
//                            v2.nexts[v1n.key] = d
//                            v2.nexts.remove(brokenValve.name)
//                        }
//                    }
//                }
//            }
//            brokenValve = valves.popBroken(startName)
//        }
//
//        val names = valves.keys.toList()
//
//        val paths = mutableMapOf<String, Int>()
//        for (i in names) {
//            val vi = valves[i]!!
//            for (j in vi.nexts.keys) {
//                paths[i + j] = vi.nexts[j]!!
//            }
//        }
//
//        val infinite = 1000
//        val n = names.size
//        val minDistances = MutableList(n*n) {0}
//        for (i in 0 until n) {
//            val unvisited = (0 until n).toMutableSet()
//            for (j in 0 until n) {
//                if (i == j) {
//                    minDistances[i*n + j] = 0
//                } else {
//                    minDistances[i*n + j] = infinite
//                }
//            }
//            var nextVisit: Int? = i
//            while (nextVisit != null) {
//                val nv = valves[names[nextVisit]]!!
//                val d0 = minDistances[i*n + nextVisit]
//                for (e in nv.nexts) {
//                    val ix = i*n + names.indexOf(e.key)
//                    if (minDistances[ix] > d0 + e.value) {
//                        minDistances[ix] = d0 + e.value
//                    }
//                }
//                unvisited.remove(nextVisit)
//                nextVisit = unvisited.minByOrNull { minDistances[i*n + it] }
//            }
//        }
//
//        println(minDistances)
//        println(names)
//
//        val flows = List(n){valves[names[it]]!!.flow}
//
////        val solver = Solver(n, minDistances.map { it.toLong() }, flows, names.indexOf("AA").toLong(), 30, names)
////
////        println(solver.max())
////
//        val solver1 = Solver(n, minDistances.map { it.toLong() }, flows, names.indexOf("AA").toLong(), 26, names, true)
//
//        println(solver1.max())
//
//        println("${0b1L}")
//
//        println("${flows.sum()}")
//
////        val root = BtState(null, valves, minDistances, initMinutes = 30)
////        val root2 = BtState(null, valves, minDistances, initMinutes = 26, walkers = 2)
////        val bt = BackTracker(root)
////        val bt2 = BackTracker(root2)
//
////        println("${valves}")
////        println("${paths}")
////        println("${minDistances}")
////        println("${bt.toList().max()}")
////        println("${bt2.toList().max()}")
////        println("${root.max.max}")
//    }
//}
//
