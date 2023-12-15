package `2023`


import java.io.File
import java.util.*

fun main() {
    Day15().solve()
}


class Day15 {

    val input1 = """
        rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    fun hash(s: String): Int {
        var r = 0
        s.forEach {
            r = ((r + it.code)*17 )%256
        }
        return r
    }

    fun solve() {
        val f = File("src/2023/inputs/day15.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0L
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

        val cmds = lines[0].split(',')

        sum = cmds.map {
            val r = hash(it)
            r
        }.sum()

        val m = mutableMapOf<Int, MutableMap<String, Pair<Int, Int>>>()
        (0..255).forEach{m[it] = mutableMapOf() }

        var i = 0
        cmds.forEach {
            val t = it.split(Regex("[-=]"))
            val label = t[0]
            val h = hash(label)
            if (it[label.length] == '=') {
                val f = t[1].toInt()
                if (m[h]!!.contains(label)) {
                    val i0 = m[h]!![label]!!.second
                    m[h]!![label] = f to i0
                } else {
                    m[h]!![label] = f to i++
                }
            } else {
                m[h]!!.remove(label)
            }
        }

        m.forEach {
            boxIx, m1 ->
                val r0 =
                if (m1.isEmpty()) {
                    0
                } else {
                val r = m1.values.sortedBy { it.second }.mapIndexed { ix, it1 ->
                    val r1 = (ix + 1).toLong() * (boxIx + 1) * (it1.first)
                    r1
                }.sum()
                r
                }
                    sum1 += r0
        }

        print("$sum $sum1\n")
    }
}