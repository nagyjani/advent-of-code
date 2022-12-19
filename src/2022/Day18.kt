package `2022`

import common.Linearizer
import common.Offset
import common.around
import java.io.File
import java.util.*

fun main() {
    Day18().solve()
}


class Day18 {

    val input0 = """
 1,1,1
 2,1,1
    """.trimIndent()

    val input1 = """
        2,2,2
        1,2,2
        3,2,2
        2,1,2
        2,3,2
        2,2,1
        2,2,3
        2,2,4
        2,2,6
        1,2,5
        3,2,5
        2,1,5
        2,3,5
    """.trimIndent()

    val d = 20 + 2
    val l = Linearizer(d, d, d)

    val neighbours =
        listOf(
            l.offset(-1, 0, 0),
            l.offset(1, 0, 0),
            l.offset(0, -1, 0),
            l.offset(0, 1, 0),
            l.offset(0, 0, -1),
            l.offset(0, 0, 1)
            )

    fun cornerNeighbours(l: Linearizer): List<Offset> {
        val offsets = mutableListOf<Offset>()
        for (i0 in -1 .. 1) {
            for (i1 in -1 .. 1) {
                for (i2 in -1 .. 1) {
                    if (i0 != 0 || i1 != 0 || i2 != 0) {
                        offsets.add(l.offset(i0, i1, i2))
                    }
                }
            }
        }
        return offsets
    }

    val cornerNeighbours = cornerNeighbours(l)

    fun allSurface(ixs: Set<Int>): Int {
        val parts = mutableSetOf<Int>()
        var allSides = 0
        for (ix in ixs) {
            parts.add(ix)
            allSides += 6
            for (n in neighbours.around(ix)) {
                if (parts.contains(n)) {
                    allSides -= 2
                }
            }
        }
        return allSides
    }

    var nextSpaceId = 0

    data class Space(val id: Int, val parentId: Int, val ixs: Set<Int>, val isLava: Boolean, val d: Int, val children: MutableSet<Int> = mutableSetOf()) {
        override fun toString(): String {
            val firstIx = ixs.first()
            val numIx = ixs.size
            val l = Linearizer(d, d, d)
            return "$isLava $id ($parentId) $firstIx:(${l.toCoordinates(firstIx).joinToString(",")})#${numIx} [${children.toList().joinToString(",")}]"
        }
    }

    class Spaces(val day: Day18, val lava: Lava) {
        val spaces = mutableMapOf<Int, Space>()
        val rootIds = mutableSetOf<Int>()
        fun addSpace(id: Int, parentId: Int, ixs: Set<Int>) {
            spaces[id] = Space(id, parentId, ixs, lava.isLava(ixs.first()), day.d)
            if (parentId == -1) {
                rootIds.add(id)
            } else {
                spaces[parentId]!!.children.add(id)
            }
        }

        fun outerSurface(): Int {
            val outerSolidId = spaces[rootIds.first()]!!.children.first()
            return outerSurface(outerSolidId)
        }

        fun outerSurface(id: Int): Int {
            val space = spaces[id]!!
            var surface = this.day.allSurface(space.ixs)
            for (i in space.children) {
                surface -= outerSurface(i)
            }
            return surface
        }

        fun nextId(): Int {
            return spaces.size
        }
    }


    val groups = MutableList(l.size){ -1 }

    data class Lava(val ixs: Set<Int>) {
        fun isLava(ix: Int) = ixs.contains(ix)
    }

    fun fillAll(spaces: Spaces, lava: Lava) {
        val unfilled = (0 until l.size).toMutableSet()
        var nextStart = 0
        var nextParentId = -1
        while (!unfilled.isEmpty()) {
            val group = fill(nextStart, lava)
            spaces.addSpace(nextStart, nextParentId, group)
            unfilled.removeAll(group)
            unfilled.find {
                var r = false
                for (n in neighbours.around(it)) {
                    if (groups[n] != -1) {
                        nextStart = it
                        nextParentId = groups[n]
                        r = true
                        break
                    }
                }
                r
            }
        }
    }

    fun fill(start: Int, lava: Lava): Set<Int> {
        val isLava = lava.isLava(start)
        val group = mutableSetOf(start)
        val unchecked = mutableSetOf(start)
        groups[start] = start
        val neighbours1 = if (isLava) {cornerNeighbours} else {neighbours}
//        val neighbours1 = cornerNeighbours
        var surface = 0
        while (!unchecked.isEmpty()) {
            val ix = unchecked.first()
            unchecked.remove(ix)
            for (n in neighbours1.around(ix)) {
                if (n in 0 until l.size && isLava == lava.isLava(n) && !group.contains(n)) {
                    unchecked.add(n)
                    group.add(n)
                    groups[n] = start
                }
                if (n in 0 until l.size && isLava != lava.isLava(n) && groups[n] == -1) {
                    ++surface
                }
            }
        }
//        println(surface)
        return group
    }

    fun printGroups() {
        for (i in 0 until d) {
            for (j in 0 until d) {
                for (k in 0 until d) {
                    val ix = l.toIndex(i, j, k)
                    print("${groups[ix].toString().padStart(4, ' ')} (${ix.toString().padStart(4, ' ' )})")
                }
                println()
            }
            println()
        }
    }

    fun solve() {
        val f = File("src/2022/inputs/day18.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input0)

        val ixs = mutableSetOf<Int>()
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            val words = line.split(",")
            if (!words.isEmpty()) {
                val nums = words.map { it.toInt()+1 }
                val ix = l.toIndex(*nums.toIntArray())
                ixs.add(ix)
                println("${words}")
            }
        }
        println("${allSurface(ixs)} ${ixs.size}")

        val lava = Lava(ixs)
        val spaces = Spaces(this, lava)
        fillAll(spaces, lava)

        printGroups()



        println("${spaces.outerSurface()}")
    }
}

