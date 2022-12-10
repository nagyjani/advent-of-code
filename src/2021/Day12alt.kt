package `2021`

import common.BackTracker
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

fun main() {
    Day12alt().solve()
}

fun String.isBig(): Boolean {
    return this.first().isUpperCase()
}

fun List<String>.asPath(): String {
    return joinToString(",")
}

class Day12alt {

    val input0 = """
        start-A
        start-b
        A-c
        A-b
        b-d
        A-end
        b-end
    """.trimIndent()

    val input1 = """
dc-end
HN-start
start-kj
dc-start
dc-HN
LN-dc
HN-end
kj-sa
kj-HN
kj-dc
    """.trimIndent()

    val input2 = """
fs-end
he-DX
fs-he
start-DX
pj-DX
end-zg
zg-sl
zg-pj
pj-he
RW-he
fs-DX
pj-RW
zg-RW
start-pj
he-WI
zg-he
pj-fs
start-RW
    """.trimIndent()


    class CaveBtNode(var cave: String, parent: CaveBtNode?, val visitable: Visitable):
        BackTracker.Node<List<String>, CaveBtNode>(parent) {
        val visited: MutableMap<String, Int> = HashMap<String, Int>(parent?.visited ?: mapOf())
        init {
            visited[cave] = visited.getOrDefault(cave, 0) + 1
        }
        val path: MutableList<String> = ArrayList(parent?.path ?: listOf<String>())
        init {
            path.add(cave)
        }
        private var leftToVisit = visitable(this)

        override fun hasNextChild(): Boolean {
            return cave != "end" && leftToVisit.isNotEmpty()
        }

        override fun nextChild(recycledNode: CaveBtNode?, level: Int): CaveBtNode {
            val nextCave = leftToVisit.first().also{ leftToVisit.remove(it) }
            val childNode1 = CaveBtNode(nextCave, this, visitable)
            return childNode1
        }

        override fun getSolution(): List<String>? {
            if (cave == "end") {
                if (path.last() != cave) {
                    println("3")
                }
                return path
            }
            return null
        }

        override fun reset(): CaveBtNode {
            leftToVisit = visitable(this)
            return this
        }
    }

    abstract class Visitable(val caves: Caves) {
        operator fun invoke(node: CaveBtNode): MutableSet<String> {
            return caves.neighbours(node.cave).subtract(unvisitable(node)).toMutableSet()
        }
        abstract fun unvisitable(node: CaveBtNode): Set<String>
    }

    class Visitable1(caves: Caves): Visitable(caves) {
        override fun unvisitable(node: CaveBtNode): Set<String> {
            return node.visited.filterKeys { !it.isBig() }.filterValues { it>0 }.keys
        }
    }

    class Visitable2(caves: Caves): Visitable(caves) {
        override fun unvisitable(node: CaveBtNode): Set<String> {
            val smallVisited = node.visited.filterKeys { !it.isBig() }
            if (smallVisited.values.find { it > 1 } != null) {
                return smallVisited.filterValues { it>0 }.keys
            } else {
                return setOf("start")
            }
        }
    }

    class CavesBuilder {
        val caves = mutableMapOf<String, MutableSet<String>>()
        fun add(c1: String, c2: String): CavesBuilder {
            caves[c1]?.add(c2)?:mutableSetOf(c2).let{caves[c1] = it}
            caves[c2]?.add(c1)?:mutableSetOf(c1).let{caves[c2] = it}
            return this
        }
        fun build(): Caves {
            return Caves(caves)
        }
    }

    class Caves(val caves: Map<String, Set<String>>) {
        fun neighbours(cave: String): Set<String> {
            return caves[cave]!!
        }
    }

    fun solve() {
        val f = File("src/2021/inputs/day12.in")
        val s = Scanner(f)
//        val s = Scanner(input0)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        val cb = CavesBuilder()
        while (s.hasNextLine()) {
            s.nextLine().split("-").let { cb.add(it[0], it[1]) }
        }
        val c = cb.build()
        val paths = BackTracker(CaveBtNode("start", null, Visitable1(c)))
        println("${paths.toList().size}")
//        println("${paths.toList().map { it.asPath() }}")
    }
}
