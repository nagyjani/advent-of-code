package `2023`


import common.Linearizer
import common.around
import java.io.File
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.util.*
import javax.sound.sampled.Line

fun main() {
    Day23().solve()
}


class Day23 {

    val input1 = """
        #.#####################
        #.......#########...###
        #######.#########.#.###
        ###.....#.>.>.###.#.###
        ###v#####.#v#.###.#.###
        ###.>...#.#.#.....#...#
        ###v###.#.#.#########.#
        ###...#.#.#.......#...#
        #####.#.#.#######.#.###
        #.....#.#.#.......#...#
        #.#####.#.#.#########v#
        #.#...#...#...###...>.#
        #.#.#v#######v###.###v#
        #...#.>.#...>.>.#.###.#
        #####v#.#.###v#.#.###.#
        #.....#...#...#.#.#...#
        #.#########.###.#.#.###
        #...###...#...#...#.###
        ###.###.#.###v#####v###
        #...#...#.#.>.>.#.>.###
        #.###.###.#.###.#.#v###
        #.....###...###...#...#
        #####################.#

    """.trimIndent()

    val input2 = """
        
    """.trimIndent()


    class Max(var m: Int? = null) {
        fun set(v : Int) {
            if (m == null || m!!<v) {
                m = v
            }
        }
    }

    fun solve(startIx: Int, map: String, l: Linearizer): Int? {
        val m = Max()
        solve(startIx, setOf(startIx), map, l, m)
        return m.m
    }

    fun solve(last: Int, visited: Set<Int>, map: String, l: Linearizer, m: Max) {
        val ns = listOf(l.offset(1, 0), l.offset(0, 1), l.offset(-1, 0), l.offset(0, -1))
        val nixs = ns.around(last).filter { map[it] != '#' }
        val (x0, y0) = l.toCoordinates(last)
        for (n in nixs) {
            if (visited.contains(n)) {
                continue
            }
            val (x, y) = l.toCoordinates(n)
            if (y == l.dimensions[1]-1) {
                m.set(visited.size)
                continue
            }
            if (map[last] == 'v' && y-y0 == -1) {
                continue
            }
            if (map[last] == '^' && y-y0 == 1) {
                continue
            }
            if (map[last] == '>' && x-x0 == -1) {
                continue
            }
            if (map[last] == '<' && x-x0 == 1) {
                continue
            }
            val visited1 = visited.toMutableSet()
            visited1.add(n)
            solve(n, visited1, map, l, m)
        }
    }

    // 0 - wall, 1 - point, 2 - edge
//    fun fieldType(ix: )

    fun isPoint(ix: Int, map: String, l: Linearizer): Boolean {
        if (map[ix] == '#') {
            return false
        }
        val (x, y) = l.toCoordinates(ix)
        if (x == 0 || x == l.dimensions[1]-1) {
            return true
        }
        val ns = listOf(l.offset(1, 0), l.offset(0, 1), l.offset(-1, 0), l.offset(0, -1))
        val nixs = ns.around(ix).filter { map[it] != '#' }
        if (nixs.size > 2) {
            return true
        }
        if (nixs.size == 1) {
            return true
        }
        return false
    }

    fun buildGraph(map: String, l: Linearizer): Map<Int, Map<Int, Int>> {
        val points = map.indices.filter { isPoint(it, map, l) }
        val r = mutableMapOf<Int, Map<Int, Int>>()
        points.forEach { val edges = getEdges(it, map, l); r[it] = edges }
        return r
    }

    fun isEdge(ix: Int, map: String, l: Linearizer): Boolean {
        if (map[ix] == '#') {
            return false
        }
        val ns = listOf(l.offset(1, 0), l.offset(0, 1), l.offset(-1, 0), l.offset(0, -1))
        val nixs = ns.around(ix).filter { map[it] != '#' }
        if (nixs.size == 2) {
            return true
        }
        return false
    }

    fun getEdges(point: Int, map: String, l: Linearizer): Map<Int, Int> {
        val r = mutableMapOf<Int, Int>()
        if (!isPoint(point, map, l)) {
            throw RuntimeException()
        }
        val ns = listOf(l.offset(1, 0), l.offset(0, 1), l.offset(-1, 0), l.offset(0, -1))
        val nixs = ns.around(point).filter { map[it] != '#'}
        for (n in nixs) {
            val (p, d) = runEdge(n, point, 1, map, l)
            r[p] = d
        }
        return r
    }

    fun runEdge(ix: Int, from: Int, distance: Int, map: String, l: Linearizer): Pair<Int, Int> {
        if (isPoint(ix, map, l)) {
            return ix to distance
        }
        if (!isEdge(ix, map, l)) {
            throw RuntimeException()
        }
        val ns = listOf(l.offset(1, 0), l.offset(0, 1), l.offset(-1, 0), l.offset(0, -1))
        val nixs = ns.around(ix).filter { map[it] != '#' && it != from}
        if (nixs.size != 1) {
            throw RuntimeException()
        }
        val nix = nixs[0]
        return runEdge(nix, ix, distance + 1, map, l)
    }


    fun solve(startIx: Int, endIx: Int, g: Map<Int, Map<Int, Int>>): Int? {
        val m = Max()
        solve(startIx, endIx, 0, setOf(startIx), g, m)
        return m.m
    }
    fun solve(ix: Int, endIx: Int, distance: Int, visited: Set<Int>, g: Map<Int, Map<Int, Int>>, m: Max) {

        if (ix == endIx) {
            m.set(distance)
        }

        for ((nix, d) in g[ix]!!) {
            if (visited.contains(nix)) {
                continue
            }
            val visited1 = visited.toMutableSet()
            visited1.add(nix)
            solve(nix, endIx, distance+d, visited1, g, m)
        }
    }


    fun solve() {
        val f = File("src/2023/inputs/day23.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        val lines = mutableListOf<String>()
        val mapB = StringBuilder()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
            mapB.append(line)
        }

        val map = mapB.toString()
        val l = Linearizer(lines[0].length, lineix)

        val startX = lines[0].indexOf('.')
        val startIx = l.toIndex(startX, 0)
        val endX = lines[lineix-1].indexOf('.')
        val endIx = l.toIndex(endX, l.dimensions[1]-1)

//        val r = solve(startIx, map, l)

        val g = buildGraph(map, l)

        val r1 = solve(startIx, endIx, g)

        print("$r1 $sum $sum1\n")
    }
}