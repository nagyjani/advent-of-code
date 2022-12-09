import java.io.File
import java.util.*

fun main() {
    Day2207().solve()
}


class Day2207 {

    data class Node (
        val name: String,
        val isDir: Boolean,
        val parent: Node? = null,
        val size: Long = 0,
        val content: MutableMap<String, Node> = mutableMapOf()
    )
    {
        fun size(): Long {
            return size + content.values.sumOf { it.size() }
        }
        fun add(name: String, isDir: Boolean, size: Long) {
            if (content[name] == null) {
                content[name] = Node(name, isDir, this, size)
            }
        }
        fun get(name: String): Node {
            if (name == "..") {
                return parent!!
            }
            add(name, true, 0)
            return content[name]!!
        }
        fun sizeAndMax(max: Int): Pair<Long, Long> {
            val children = content.values
            val sumFiles = children.filter { !it.isDir }.sumOf { it.size }
            val sumDirs = children.filter { it.isDir }.fold(Pair(0L, 0L)){
                acc, node ->
                val p = node.sizeAndMax(max)
                Pair(acc.first + p.first, acc.second + p.second)
            }
            val sumSize = sumFiles + sumDirs.first
            if (sumSize > max) {
                return Pair(sumSize, sumDirs.second)
            }
            return Pair(sumSize, sumDirs.second + sumSize)
        }
        fun minLarger(limit: Long, current: Long): Long {
            val children = content.values
            val subdirs = children.filter { it.isDir }
            var c = current
            if (!subdirs.isEmpty()) {
                c = subdirs.minOf { it.minLarger(limit, current) }
            }
            val s = size()
            if (s < c && s >= limit) {
                return s
            }
            return c
        }
    }

    val root = Node("", true)

    val input1 = """
        ${'$'} cd /
${'$'} ls
dir a
14848514 b.txt
8504156 c.dat
dir d
${'$'} cd a
${'$'} ls
dir e
29116 f
2557 g
62596 h.lst
${'$'} cd e
${'$'} ls
584 i
${'$'} cd ..
${'$'} cd ..
${'$'} cd d
${'$'} ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k
    """.trimIndent()

    fun solve() {
        val f = File("src/2022/inputs/aoc2207.in")
        val s = Scanner(f)
//                val s = Scanner(input1)

        var current = root
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            val tokens = line.split(" ")
            if (tokens[0] == "$") {
                if (tokens[1] == "cd") {
                    if (tokens[2] == "/") {
                        current = root
                    } else {
                        current = current.get(tokens[2])
                    }
                }
            } else if (tokens[0] == "dir") {
                current.add(tokens[1], true, 0)
            } else {
                current.add(tokens[1], false, tokens[0].toLong())
            }
        }

        val minDelete = 30000000 - (70000000 - root.size())

        val minLarger = root.minLarger(minDelete, root.size())

        println("$minLarger $minDelete ${root.sizeAndMax(100000)}")
    }
}

