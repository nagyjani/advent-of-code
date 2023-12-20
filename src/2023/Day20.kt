package `2023`


import java.io.File
import java.lang.RuntimeException
import java.util.*

fun main() {
    Day20().solve()
}


class Day20 {

    val input1 = """
        broadcaster -> a, b, c
        %a -> b
        %b -> c
        %c -> inv
        &inv -> a
    """.trimIndent()

    val input2 = """
        broadcaster -> a
        %a -> inv, con
        &inv -> b
        %b -> con
        &con -> output
    """.trimIndent()

    class ModuleBuilder()

    fun MutableMap<String, Module>.addModule(line: String) {

        val (type0, name0) = Regex("(.)([^ ]+)").find(line)!!.destructured

        val (type, name) =
            if (type0 == "b") {
                "!" to "broadcaster"
            } else {
                type0 to name0
            }

        val (outputs0) = Regex(" -> (.+)").find(line)!!.destructured
        val outputs = outputs0.split(Regex(", "))

        if (contains(name)) {
            get(name)!!.type = type
            get(name)!!.outputs = outputs
        } else {
            val m = Module(name)
            m.type = type
            m.outputs = outputs
            set(name, m)
        }

        for (o in outputs) {
            if (contains(o)) {
                get(o)!!.inputs[name] = 0
            } else {
                val m = Module(o)
                m.inputs[name] = 0
                set(o, m)
            }
        }
    }

    class Module(val name: String) {

        val inputs = mutableMapOf<String, Int>()
        var state = 0 // off
        var type = "?"
        var outputs = listOf<String>()

        fun receive(s: String, v: Int): List<Pair<Pair<String,String>, Int>> {
            when (type) {
                "!" ->
                    return send(v)
                "%" ->
                    if (v == 0) {
                        state = 1-state
                        return send(state)
                    }
                "&" -> {
                    inputs[s] = v
                    if (inputs.all { it.value == 1 }) {
                        return send(0)
                    }
                    return send(1)
                }
                else -> return listOf()
            }
            return listOf()
        }

        fun send(v: Int): List<Pair<Pair<String,String>, Int>> {
            return outputs.map { (name to it) to v }
        }
    }

    fun MutableMap<String, Module>.pushButton(i: Int): Pair<Long, Long> {
        val pulses = mutableListOf(("button" to "broadcaster") to 0)
        var pulseIx = 0
        while (pulseIx < pulses.size) {
            val (names, v) = pulses[pulseIx]
            val (src, dst) = names
            if (src in listOf("sx", "jt", "kb", "ks") && dst == "zh" && v == 1) {
                println("! $i $src -$v-> zh")
            }
            val newPulses = get(dst)!!.receive(src, v)
            pulses.addAll(newPulses)
            ++pulseIx
        }
        var low = 0L
        var high = 0L
        pulses.forEach { if (it.second == 0) {low++} else {high++} }
        return low to high
    }

    fun solve() {
        val f = File("src/2023/inputs/day20.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sumLow = 0L
        var sumHigh = 0L
        var lineix = 0
        val lines = mutableListOf<String>()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
        }

        val modules = mutableMapOf<String, Module>()

        lines.forEach { modules.addModule(it) }

        val reachable = modules.map{it.key to it.value.outputs.toSet()}.toMap().toMutableMap()
        var updated = true
        while (updated) {
            updated = false
            for (n in modules.keys) {
                for (n1 in reachable[n]!!) {
                    val r = reachable[n]!!
                    val r1 = reachable[n1]!!
                    if (r1.subtract(r).isNotEmpty()) {
                        updated = true
                        val r2 = r.union(r1)
                        reachable[n] = r2
                    }
                }
            }
        }

        val groups = mutableMapOf<String, String>()
        for (n in modules.keys) {
            for (n1 in modules.keys) {
                if (n == n1) {
                    continue
                }
                val r = reachable[n]!!
                val r1 = reachable[n1]!!
                if (r.contains(n) && r1.contains(n1)) {
                    if (groups.contains(n1)) {
                        groups[n] = groups[n1]!!
                    } else {
                        groups[n] = n
                        groups[n1] = n
                    }
                } else {
                    groups[n] = n
                }
            }
        }

        for (i in 1..10000) {
            val (l, h) = modules.pushButton(i)
            if (i < 1001) {
                sumLow += l
                sumHigh += h
            }
        }

        // part 2 is 3877 * 3851 * 4021 * 4049 based on the printouts
        // when "sx", "jt", "kb", "ks" fires;
        // the periods:
        // kb: 3851
        // sx: 3877
        // ks: 4021
        // jt: 4049
        // all primes

        print("$sumLow $sumHigh ${sumLow * sumHigh}\n")
    }
}