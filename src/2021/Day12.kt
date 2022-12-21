//package `2021`
//
//import java.io.File
//import java.util.*
//
//fun main() {
//    Day12().solve()
//}
//
//class Day12 {
//
//    val input0 = """
//        start-A
//        start-b
//        A-c
//        A-b
//        b-d
//        A-end
//        b-end
//    """.trimIndent()
//
//    val input1 = """
//dc-end
//HN-start
//start-kj
//dc-start
//dc-HN
//LN-dc
//HN-end
//kj-sa
//kj-HN
//kj-dc
//    """.trimIndent()
//
//    val input2 = """
//fs-end
//he-DX
//fs-he
//start-DX
//pj-DX
//end-zg
//zg-sl
//zg-pj
//pj-he
//RW-he
//fs-DX
//pj-RW
//zg-RW
//start-pj
//he-WI
//zg-he
//pj-fs
//start-RW
//    """.trimIndent()
//
////    class CaveMapIteratorState(val path: CavePath) {
////        val tried = mutableSetOf<String>()
////        fun nextPath(): CavePath? {
////            val cave = path.lastCave()
////            return
////        }
////    }
//
//    class CavePaths(val caves: Caves, val visitNum: Int): Iterable<CavePath> {
//        override fun iterator(): Iterator<CavePath> {
//            return CavePathIterator(caves, visitNum)
//        }
//    }
//
//    class CavePathIterator(caves: Caves, val visitNum: Int): Iterator<CavePath> {
//        val nodes = mutableListOf(CavePathIteratorNode(CavePath(), caves, visitNum))
//        var next = findNext()
//        override fun hasNext(): Boolean {
//            return next != null
//        }
//        override fun next(): CavePath {
//            val r = next
//            next = findNext()
//            return r!!
//        }
//        tailrec fun findNext(): CavePath? {
//            if (nodes.isEmpty()) {
//                return null
//            }
//            if (nodes.last().hasNext) {
//                nodes.add(nodes.last().next())
//            } else {
////                println("-:${nodes.last().path.path}")
//                nodes.removeLast()
//            }
//            if (nodes.isEmpty()) {
//                return null
//            }
//            if (nodes.last().path.complete) {
////                println("+:${nodes.last().path.path}")
//                return nodes.last().path
//            }
//            return findNext()
//        }
//    }
//
//    class CavePathIteratorNode(val path: CavePath, val caves: Caves, val visitNum: Int) {
//        val remainingNextCaves = mutableSetOf<String>().apply{
//            addAll(caves.neighbours(path.lastCave).filter{ it.isBig() || !path.visited(it, visitNum)})
//        }
//        val hasNext get() = path.lastCave != "end" && remainingNextCaves.isNotEmpty()
//        fun next(): CavePathIteratorNode {
//            val nextCave = remainingNextCaves.first().also{ remainingNextCaves.remove(it) }
//            return CavePathIteratorNode(path.next(nextCave), caves, visitNum)
//        }
//    }
//
//    class CavePath(val lastCave: String = "start", val basePath: CavePath? = null) {
//        val complete get() = lastCave == "end"
//        val path: String get() = basePath?.path?.plus(",")?.plus(lastCave)?:lastCave
//        fun visited(cave: String): Boolean {
//            return visited(cave, this)
//        }
//        tailrec fun visited(cave: String, path: CavePath?): Boolean {
//            if (path == null) {
//                return false
//            }
//            if (path.lastCave == cave) {
//                return true
//            }
//            return visited(cave, path.basePath)
//        }
//        fun visited(cave: String, num: Int): Boolean {
//            return visited(cave, this, num)
//        }
//        tailrec fun visited(cave: String, path: CavePath?, num: Int): Boolean {
//            if (cave == "start") {
//                return true
//            }
//            if (path == null) {
//                return false
//            }
//            val m = generateSequence(path){it.basePath}
//                        .map { it.lastCave }
//                        .filter{!it.isBig()}
//                        .fold(mutableMapOf<String, Int>()){acc, it -> acc[it] = acc.getOrDefault(it,0)+1; acc}
//            return !(m.getOrDefault(cave, 0) < 1 || m.getOrDefault(cave, 0)<2 && m.values.all{it<2})
////            if (path.lastCave == cave) {
////                if (num == 1) {
////                    return true
////                }
////                return visited(cave, path.basePath, num-1)
////            }
////            return visited(cave, path.basePath, num)
//        }
//        fun next(nextCave: String): CavePath {
//            return CavePath(nextCave, this)
//        }
//    }
//
//    class CavesBuilder {
//        val caves = mutableMapOf<String, MutableSet<String>>()
//        fun add(c1: String, c2: String): CavesBuilder {
//            caves[c1]?.add(c2)?:mutableSetOf(c2).let{caves[c1] = it}
//            caves[c2]?.add(c1)?:mutableSetOf(c1).let{caves[c2] = it}
//            return this
//        }
//        fun build(): Caves {
//            return Caves(caves)
//        }
//    }
//
//    class Caves(val caves: Map<String, Set<String>>) {
//        fun neighbours(cave: String): Set<String> {
//            return caves[cave]!!
//        }
//    }
//
//    fun solve() {
//        val f = File("src/2021/inputs/day12.in")
//        val s = Scanner(f)
////        val s = Scanner(input0)
////        val s = Scanner(input1)
////        val s = Scanner(input2)
//        val cb = CavesBuilder()
//        while (s.hasNextLine()) {
//            s.nextLine().split("-").let { cb.add(it[0], it[1]) }
//        }
//        val c = cb.build()
//        val paths = CavePaths(c, 2)
//        println("${paths.toList().size}")
////        println("${paths.toList().map { it.path }}")
//    }
//}
