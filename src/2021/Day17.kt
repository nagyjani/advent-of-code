package `2021`

import java.io.File
import java.lang.Integer.max
import java.lang.Math.abs
import java.util.*
import kotlin.math.sqrt

fun main() {
    Day17().solve()
}

class Day17 {
    val input1 = """target area: x=20..30, y=-10..-5""".trimIndent()
    val input2 = """target area: x=128..160, y=-142..-88""".trimIndent()
    val outVs1 ="""23,-10  25,-9   27,-5   29,-6   22,-6   21,-7   9,0     27,-7   24,-5
25,-7   26,-6   25,-5   6,8     11,-2   20,-5   29,-10  6,3     28,-7
8,0     30,-6   29,-8   20,-10  6,7     6,4     6,1     14,-4   21,-6
26,-10  7,-1    7,7     8,-1    21,-9   6,2     20,-7   30,-10  14,-3
20,-8   13,-2   7,3     28,-8   29,-9   15,-3   22,-5   26,-8   25,-8
25,-6   15,-4   9,-2    15,-2   12,-2   28,-9   12,-3   24,-6   23,-7
25,-10  7,8     11,-3   26,-7   7,1     23,-9   6,0     22,-10  27,-6
8,1     22,-8   13,-4   7,6     28,-6   11,-4   12,-4   26,-9   7,4
24,-10  23,-8   30,-8   7,0     9,-1    10,-1   26,-5   22,-9   6,5
7,5     23,-6   28,-10  10,-2   11,-1   20,-9   14,-2   29,-7   13,-3
23,-5   24,-8   27,-9   30,-7   28,-5   21,-10  7,9     6,6     21,-5
27,-10  7,2     30,-9   21,-8   22,-7   24,-9   20,-6   6,9     29,-5
8,-2    27,-8   30,-5   24,-7
"""

// vx(vx+1)/2 >= xmin
// (vx+1)^2 >= 2*xmin
// vx >= sqrt(2*xmin)-1

    fun solve() {
//        val f = File("src/2021/inputs/day16.in")
//        val s = Scanner(f)

        // assuming x>=0 and y<=0
        val s = Scanner(input2).useDelimiter("target area: x=|\\.\\.|, y=")

        val minX = s.nextInt()
        val maxX = s.nextInt()

        val maxY = -1 * s.nextInt()
        val minY = -1 * s.nextInt()

        val s1 = Scanner(outVs1).useDelimiter("\\s+|,")
        val vs1 = mutableSetOf<Pair<Int, Int>>()
        for (i in 1..112) {
            vs1.add(s1.nextInt() to s1.nextInt())
        }

        val ts = mutableSetOf<Int>()
        val yts = mutableMapOf<Int, MutableSet<Int>>()
        fun MutableMap<Int, MutableSet<Int>>.add(v: Int, t: Int) {
            this[v]?.add(t) ?: (this.set(v, mutableSetOf(t)))
        }

        for (vy in -maxY..maxY-1) {
            // -maxY <= vy <= 0
            // minY <= (2*vy+t-1)*t/2 <= maxY
            if (vy <= 0) {
                var t = 0
                while ((-2*vy+t-1)*t/2 <= maxY) {
                    if (minY <= (-2*vy+t-1)*t/2) {
                        ts.add(t)
                        yts.add(vy, t)
                    }
                    t += 1
                }
            // 0 <= vy <= maxY-1
            // y=0: t0=2*vy+1 if vy>0
            // t = t0 + t1
            // minY <= (2*vy-t1+3)*t1/2 <= maxY
            } else {
                val t0 = 2*vy+1
                var t1 = 0
                while ((2*vy+t1+1)*t1/2 <= maxY) {
                    if ((2*vy+t1+1)*t1/2 >= minY) {
                        ts.add(t0 + t1)
                        yts.add(vy, t0 + t1)
                    }
                    t1 += 1
                }
            }
        }

        val maxVy = yts.keys.maxOf { it }

        val txs = mutableMapOf<Int, MutableSet<Int>>()
        for (vx in 0..maxX) {
            for (t in ts) {
                val xt =
                    if (t <= vx) {
                        (2 * vx - t + 1) * t / 2
                    } else {
                        (vx + 1) * vx / 2
                    }
                if (xt in minX..maxX) {
                    txs.add(t, vx)
                }
            }
        }

        val vs = mutableSetOf<Pair<Int, Int>>()
        yts.forEach { it.value.forEach {
                it1 ->
            (txs[it1] ?: setOf()).forEach {it2 -> vs.add(it2 to it.key)}
        }}

        val commonVs = vs.intersect(vs1)
        val onlyVs = vs.subtract(vs1)
        val onlyVs1 = vs1.subtract(vs)

        println("${maxVy*(maxVy+1)/2} ${vs.size}")
    }
}