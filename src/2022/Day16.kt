package `2022`

import common.BackTracker
import java.io.File
import java.util.*

fun main() {
    Day16().solve()
}


class Day16 {

    val input1 = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent()

    data class Valve(val name: String, val flow: Long, val nexts: List<String>)

    class Max(var max: Long)
    data class ValveState (var opened: Boolean, var visited: Int = 0) {
        fun visited() {
            ++visited
        }
        fun setOpened() {
            opened = true
        }
    }

    enum class StepType {STEP, OPEN}
    data class Step(val type: StepType, val to: String = "")
    class BtState(
        val p: BtState?,
        val step: Step?,
        val valves: Map<String, Valve>,
        valve0: Valve,
        var valveStates: Map<String, ValveState>,
        val minutesLeft: Int = 30,
        val flow: Long = 0,
        val max: Max = Max(0),
        val maxFlowLeft: Long = 0,
        var maxFlow: Long = 0,
        var nextChildIx: Int = 0) : BackTracker.Node<Long, BtState>(p) {

        val valve =
            Valve(
                valve0.name,
                valve0.flow,
                valve0.nexts.filter {visitable(it)})

        fun visitable(valveName: String): Boolean {
            // visited == 0 || visited < valves[valveName]!!.nexts.size-1
            val visited = valveStates[valveName]!!.visited
            return  visited == 0 || visited < valves[valveName]!!.nexts.size-1
        }

        init {
            println(toString())
        }

        fun unopenedFlow(): Long {
            return valves.values.sumOf { if (!valveStates[it.name]!!.opened) {it.flow} else {0} }
        }

        fun maxLeft(): Long {
            return (minutesLeft-1) * unopenedFlow()
        }

        fun canYieldMax(): Boolean {
            return max.max < flow + maxLeft()
        }
        override fun hasNextChild(): Boolean {
            if (minutesLeft < 1) {
                return false
            }
            if (unopenedFlow() == 0L) {
                return false
            }
            if (!canYieldMax()) {
                return false
            }
            if (valveStates[valve.name]!!.opened) {
                return nextChildIx < valve.nexts.size
            }
            return nextChildIx <= valve.nexts.size
        }

        override fun nextChild(recycledNode: BtState?, level: Int): BtState {
            val nextCurrentValveState = valveStates[valve.name]!!.copy()
            var nextValve = valve
            var plusFlow = 0L
            var step = Step(StepType.OPEN)
            if (!nextCurrentValveState.opened) {
                if (nextChildIx == 0) {
                    plusFlow = valve.flow * (minutesLeft-1L)
                    nextCurrentValveState.setOpened()
                } else {
                    step = Step(StepType.STEP, valve.nexts[nextChildIx-1])
                    nextValve = valves[valve.nexts[nextChildIx-1]]!!
                    nextCurrentValveState.visited()
                }
            } else {
                step = Step(StepType.STEP, valve.nexts[nextChildIx])
                nextValve = valves[valve.nexts[nextChildIx]]!!
                nextCurrentValveState.visited()
            }
            val valveStates1 = valveStates.toMutableMap()
            valveStates1[valve.name] = nextCurrentValveState
            ++nextChildIx
            return BtState(this, step, valves, nextValve, valveStates1, minutesLeft - 1, flow + plusFlow)
        }

        override fun getSolution(): Long? {
            if (flow > max.max) {
                max.max = flow
                return flow
            }
            return null
        }
        override fun reset(): BtState {
            this.nextChildIx = 0
            return this
        }

        override fun toString(): String {
            return p.toString() + " " + step.toString() + " " + minutesLeft.toString()+ " " + flow.toString()
        }
    }

    fun solve() {
//        val f = File("/home/janos/Downloads/day16.in")
//        val s = Scanner(f)
        val s = Scanner(input1)

        val valves = mutableMapOf<String, Valve>()

        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            val words = line.split(" ")
            if (!words.isEmpty()) {
                val valve =
                    Valve(
                        words[1],
                        words[4].split(Regex("[=;]"))[1].toLong(),
                        words.subList(9, words.size).joinToString("").split(","))
                valves[valve.name] = valve
                println("${words}")
                println("${valve}")
            }
        }

        val initialValveStates = valves.map { it.key to ValveState(it.value.flow == 0L)}.toMap()

        val root = BtState(null, null, valves, valves["AA"]!!, initialValveStates)

        val bt = BackTracker(root)

        println("${bt.toList().max()}")

        println("${true}")
    }
}

