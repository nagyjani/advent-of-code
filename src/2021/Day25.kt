import java.io.File
import java.util.*

fun main() {
    Day25().solve()
}


class Day25 {

    class Floor(val floor: MutableList<Char>, val x: Int, val y: Int) {

        fun westOf(i: Int): Int {
            if (i%x == 0) {
                return i+x-1
            }
            return i-1
        }

        fun eastOf(i: Int): Int {
            if (i%x == x-1) {
                return i-x+1
            }
            return i+1
        }

        fun northOf(i: Int): Int {
            if (i < x) {
                return (y-1)*x + i%x
            }
            return i-x
        }

        fun southOf(i: Int): Int {
            if (i >= (y-1)*x) {
                return i%x
            }
            return i+x
        }

        fun step(): Boolean {
            var changed = false
            for (i in floor.indices) {
                if (floor[i] == '>') {
                   if (floor[eastOf(i)] == '.') {
                       floor[eastOf(i)] = 'E'
                       changed = true
                   } else {
                       floor[i] = 'E'
                   }
                }
            }
            for (i in floor.indices) {
                if (floor[i] == 'E') {
                    floor[i] = '>'
                } else if (floor[i] == '>') {
                    floor[i] = '.'
                }
            }
            for (i in floor.indices) {
                if (floor[i] == 'v') {
                    if (floor[southOf(i)] == '.') {
                        floor[southOf(i)] = 'S'
                        changed = true
                    } else {
                        floor[i] = 'S'
                    }
                }
            }
            for (i in floor.indices) {
                if (floor[i] == 'S') {
                    floor[i] = 'v'
                } else if (floor[i] == 'v') {
                    floor[i] = '.'
                }
            }
            return changed
        }

        override fun toString(): String {
            val s = StringBuilder()
            for (iy in 0 until y) {
                for (ix in 0 until x) {
                    s.append(floor[iy*x+ix])
                }
                s.append("\n")
            }
            return s.toString()
        }
    }

    val input1 = """
v...>>.vv>
.vv>>.vv..
>>.>v>...v
>>v>>.>.v.
v>v.vv.v..
>.>>..v...
.vv..>.>v.
v.v..>>v.v
....v..v.>
    """.trimIndent()


    val input2 = """
...>...
.......
......>
v.....>
......>
.......
..vvv..
    """.trimIndent()


    fun solve() {
        val f = File("/home/janos/Downloads/aoc25.in1")
        val s = Scanner(f)
//        val s = Scanner(input1)
        var x = 0
        var y = 0
        val floorBuilder = StringBuilder("")
        while (s.hasNext()) {
            val line = s.next().trim()
            if (line.isEmpty()) {
                continue
            }
            x = line.length
            ++y
            floorBuilder.append(line)
        }
        val floor = Floor(floorBuilder.toMutableList(), x, y)

        var step = 0
        while (floor.step()) {
            ++step
//            println("step ${step}\n$floor")
        }

        print("$step\n${floor}\n$step")
    }
}