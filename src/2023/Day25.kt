package `2023`


import java.io.File
import java.util.*

fun main() {
    Day25().solve()
}


class Day25 {

    val input1 = """
        jqt: rhn xhk nvd
        rsh: frs pzl lsr
        xhk: hfx
        cmg: qnr nvd lhk bvb
        rhn: xhk bvb hfx
        bvb: xhk hfx
        pzl: lsr hfx nvd
        qnr: nvd
        ntq: jqt hfx bvb xhk
        nvd: lhk
        lsr: lhk
        rzs: qnr cmg lsr rsh
        frs: qnr lhk lsr
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    // for one node
    // the shortest path to another (~1500)
    // remove one node from the path (K)
    // find the shortest again (3)


    fun Map<String, Set<String>>.findPath(source: String, target: String, removed: Set<Pair<String, String>>): List<String>? {
        return findPath(target, listOf(source), removed)
    }

    fun Map<String, Set<String>>.findPath(target: String, path: List<String>, removed: Set<Pair<String, String>>): List<String>? {
        val nextNodes = get(path.last())!!
        if (nextNodes.contains(target)) {
            return path.toMutableList().apply {  add(target)}
        }
        for (n in nextNodes) {
            if (path.contains(n)) {
                continue
            }
            if (removed.contains(path.last() to n)) {
                continue
            }
            val r = findPath(target, path.toMutableList().apply { add(n) }, removed)
            if (r == null) {
                continue
            }
            return r
        }
        return null
    }

    fun Map<String, Set<String>>.findPath2(source: String, target: String, removed: Set<Pair<String, String>>): Pair<List<Pair<String, String>>?,Int> {
        val paths = mutableMapOf<String, String>()
        val toCheck = get(source)!!.map { it to source }.filter{!removed.contains(it) && !removed.contains(it.second to it.first)}.toMap().toMutableMap()
        if (removed.size == 3 && (removed.contains("nvd" to "jqt") || removed.contains("jqt" to "nvd")) && (removed.contains("pzl" to "hfx") || removed.contains("hfx" to "pzl")) && (removed.contains("cmg" to "bvb") || removed.contains("bvb" to "cmg"))
        ) {
            Unit
        }
        while (toCheck.isNotEmpty()) {
            val n = toCheck.keys.first()
            val s = toCheck[n]!!
            toCheck.remove(n)
            if (paths.contains(n)) {
                continue
            }
            paths[n] = s
            get(n)!!.filter { it != source && !paths.contains(it) && !removed.contains(it to n) && !removed.contains(n to it) }.forEach { toCheck[it] = n }
        }

        if (paths.contains(target)) {
            val r = mutableListOf<Pair<String,String>>()
            var n = target
            while (n != source) {
                r.add(paths[n]!! to n)
                n = paths[n]!!
            }
            return r.reversed() to paths.size
        }

        return null to paths.size
    }




    fun solve() {
        val f = File("src/2023/inputs/day25.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        val lines = mutableListOf<String>()
        val nodes = mutableMapOf<String, MutableSet<String>>()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
            val t1 = line.split(Regex(": "))
            val node = t1[0]
            t1[1].split(" ").forEach {
                if (nodes.contains(node)) {
                    nodes[node]!!.add(it)
                } else {
                    nodes[node] = mutableSetOf(it)
                }
                if (nodes.contains(it)) {
                    nodes[it]!!.add(node)
                } else {
                    nodes[it] = mutableSetOf(node)
                }
            }
        }

        val e = nodes.findPath2(nodes.keys.first(), nodes.keys.last(), setOf())

        val source = nodes.keys.first()
        val targets = nodes.keys.toMutableList().apply{remove(source)}

        main@ for (t in targets) {
            val (d, _) = nodes.findPath2(source, t, setOf())
            for (removed1 in d!!) {
                val (d1, _) = nodes.findPath2(source, t, setOf(removed1))
                if (t == "nvd" && (removed1 == "nvd" to "jqt" || removed1 == "jqt" to "nvd")) {
                    Unit
                }
                if (d1 == null) {
                    println("1: $removed1")
                    continue
                }
                for (removed2 in d1!!) {
                    val (d2, _) = nodes.findPath2(source, t, setOf(removed1, removed2))
                    if (t == "nvd" && (removed1 == "nvd" to "jqt" || removed1 == "jqt" to "nvd") && (removed2 == "pzl" to "hfx" || removed2 == "hfx" to "pzl")) {
                        Unit
                    }
                    if (d2 == null) {
                        println("2: $removed1 $removed2")
                        continue
                    }
                    for (removed3 in d2!!) {
                        val (d3, s3) = nodes.findPath2(source, t, setOf(removed1, removed2, removed3))
                        if (d3 == null) {
                            val d4 = nodes.findPath2(source, t, setOf(removed1, removed2, removed3))
                            println("3: $removed1 $removed2 $removed3 ($s3) (${(s3 + 1) * (nodes.size - s3 -1)})")
                            break@main
                        }
                    }
                }
            }
        }

        print("$sum $sum1\n")
    }
}