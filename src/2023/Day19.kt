package `2023`


import java.io.File
import java.lang.RuntimeException
import java.math.BigInteger
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day19().solve()
}


class Day19 {

    val input1 = """
        px{a<2006:qkq,m>2090:A,rfg}
        pv{a>1716:R,A}
        lnx{m>1548:A,A}
        rfg{s<537:gd,x>2440:R,A}
        qs{s>3448:A,lnx}
        qkq{x<1416:A,crn}
        crn{x>2662:A,R}
        in{s<1351:px,qqz}
        qqz{s>2770:qs,m<1801:hdj,R}
        gd{a>3333:R,R}
        hdj{m>838:A,pv}

        {x=787,m=2655,a=1222,s=2876}
        {x=1679,m=44,a=2067,s=496}
        {x=2036,m=264,a=79,s=2244}
        {x=2461,m=1339,a=466,s=291}
        {x=2127,m=1623,a=2188,s=1013}
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()


    class Gear(val x: Int, val m: Int, val a: Int, val s: Int) {
        operator fun get(c: String): Int = when (c) {"x" -> x; "m" -> m; "a" -> a; else -> s}

        fun sum(): Int = x + m + a + s
    }

    class Gpr(val lower: Int = 1, val upper: Int = 4001)

    class Gprs(val gprs: Map<String, Gpr> = mapOf("x" to Gpr(), "m" to Gpr(), "a" to Gpr(), "s" to Gpr())) {
        operator fun get(c: String): Gpr = gprs[c]!!

        fun splitLt(c: String, n: Int): Pair<List<Gprs>, List<Gprs>> {
            val g = get(c)
            if (g.upper <= n) {
                return listOf(this) to listOf()
            }
            if (g.lower >= n) {
                return listOf<Gprs>() to listOf(this)
            }
            val g1 = Gpr(g.lower, n)
            val g2 = Gpr(n, g.upper)
            val gprs1 = gprs.toMutableMap().also { it[c] = g1 }
            val gprs2 = gprs.toMutableMap().also { it[c] = g2 }
            return listOf(Gprs(gprs1)) to listOf(Gprs(gprs2))
        }

        fun splitGt(c: String, n: Int): Pair<List<Gprs>, List<Gprs>> {
            val g = get(c)
            if (g.lower >= n+1) {
                return listOf(this) to listOf()
            }
            if (g.upper <= n+1) {
                return listOf<Gprs>() to listOf(this)
            }
            val g1 = Gpr(n+1, g.upper)
            val g2 = Gpr(g.lower, n+1)
            val gprs1 = gprs.toMutableMap().also { it[c] = g1 }
            val gprs2 = gprs.toMutableMap().also { it[c] = g2 }
            return listOf(Gprs(gprs1)) to listOf(Gprs(gprs2))
        }

        fun num(): BigInteger {
            return gprs.values.fold(BigInteger.ONE){p, g -> p * BigInteger.valueOf(g.upper.toLong() - g.lower) }
        }
    }

    fun List<Gprs>.num() = sumOf { it.num() }

    class Workflow(val name: String, val rules: List<Rule>, val e: String) {
        fun apply(g: Gear): String {
            for (i in rules.indices) {
                val r = rules[i].apply(g)
                if (r != null) {
                    return r
                }
            }
            return e
        }

        fun apply(g: Gprs): Map<String, List<Gprs>> = apply(listOf(g))

        fun apply(gs: List<Gprs>): Map<String, List<Gprs>> {
            val result = mutableMapOf<String, List<Gprs>>()
            var inGs = gs
            for (i in rules.indices) {
                val (matches, nomatches) = rules[i].apply(inGs)
                if (matches.isNotEmpty()) {
                    val next = rules[i].next
                    if (result.contains(next)) {
                        result[next] = result[next]!!.toMutableList().apply{addAll(matches)}
                    } else {
                        result[next] = matches
                    }
                }
                inGs = nomatches
            }
            if (inGs.isNotEmpty()) {
                if (result.contains(e)) {
                    result[e] = result[e]!!.toMutableList().apply{addAll(inGs)}
                } else {
                    result[e] = inGs
                }
            }
            return result
        }
    }

    class Rule(val p: String, val r: String, val n: Int, val next: String) {
        fun apply(g: Gear): String? {
            if (r == ">" && g[p] > n || r == "<" && g[p] < n) {
                return next
            }
            return null
        }

        fun apply(gs: List<Gprs>): Pair<List<Gprs>, List<Gprs>> {
            val result = mutableListOf<Gprs>() to mutableListOf<Gprs>()
            gs.forEach {
                val (match, nomatch) = apply(it)
                result.first.addAll(match)
                result.second.addAll(nomatch)
            }
            return result
        }

        fun apply(g: Gprs): Pair<List<Gprs>, List<Gprs>>  {
            val result =
                when (r) {
                    "<" -> g.splitLt(p, n)
                    ">" -> g.splitGt(p, n)
                    else -> throw RuntimeException()
                }
            return result
        }
    }

    fun String.toRule(): Rule {
        val (p, r, n0, next) = Regex("([^<>])+([<>])(\\d+):(.+)").find(this)!!.destructured
        return Rule(p, r, n0.toInt(), next)
    }

    fun String.toWorkflow(): Workflow {
        val (name, rules, e) = Regex("(\\w+)\\{(.+),([^:]+)}").find(this)!!.destructured
        val rules1 = rules.split(",").map { it.toRule() }
        return Workflow(name, rules1, e)
    }

    fun String.toGear(): Gear {
        val (x, m, a, s) = Regex("x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)").find(this)!!.destructured
        return Gear(x.toInt(), m.toInt(), a.toInt(), s.toInt())
    }

    fun solve() {
        val f = File("src/2023/inputs/day19.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        var lines = mutableListOf<String>()
        var lines1 = lines
        var lines2 = lines
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty() && lines.isNotEmpty()) {
                lines = mutableListOf<String>()
                continue
            }
            lines.add(line)
        }
        lines2 = lines

        val a = "px{a<2006:qkq,m>2090:A,rfg}".toWorkflow()
        val b ="{x=2461,m=1339,a=466,s=291}".toGear()

        val wfs = mutableMapOf<String, Workflow>()
        lines1.forEach {
            val wf = it.toWorkflow();
            wfs[wf.name] = wf
        }
        val gears = lines2.map { it.toGear() }

        for (g in gears) {
            var next = "in"
            while (next != "A" && next != "R") {
                next = wfs[next]!!.apply(g)
            }
            if (next == "A") {
                sum += g.sum()
            }
        }

        val gs = mutableMapOf("in" to listOf( Gprs()))
        val gsA =  mutableListOf<Gprs>()
        val gsR =  mutableListOf<Gprs>()
        while (gs.isNotEmpty()) {
            val nextWfName = gs.keys.first()
            val wfGs = gs[nextWfName]!!
            gs.remove(nextWfName)
            val newGs0 = wfs[nextWfName]!!.apply(wfGs)
            val newGs1 = newGs0.toMutableMap().apply {
                remove("A")
                remove("R")
            }
            if (newGs0.contains("R")) {
                gsR.addAll(newGs0["R"]!!)
            }
            if (newGs0.contains("A")) {
                gsA.addAll(newGs0["A"]!!)
            }
            for (wfName in newGs1.keys) {
                if (gs.contains(wfName)) {
                    gs[wfName] = gs[wfName]!!.toMutableList().apply{addAll(newGs1[wfName]!!)}
                } else {
                    gs[wfName] = newGs1[wfName]!!
                }
            }
        }

        val suml = 0L

        print("$sum $sum1 ${gsA.num()}\n")
    }
}