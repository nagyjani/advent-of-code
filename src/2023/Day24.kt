package `2023`


import `2021`.sign
import java.io.File
import java.lang.RuntimeException
import java.math.BigInteger
import java.util.*
import kotlin.math.sign

fun main() {
    Day24().solve()
}


class Day24 {

    val input1 = """
        

        19, 13, 30 @ -2,  1, -2
        18, 19, 22 @ -1, -1, -2
        20, 25, 34 @ -2, -2, -4
        12, 31, 28 @ -1, -2, -1
        20, 19, 15 @  1, -5, -3

    """.trimIndent()

    val input2 = """
    """.trimIndent()


    class Hail3D(val p: List<Long>, val v: List<Long>)

    class Hail2D(val x0: Long, val y0: Long, val vx: Long, val vy: Long) {
        fun collides(other: Hail2D, min: Double, max: Double): Boolean {


            val a = vy.toDouble() / vx.toDouble()
            val b = (y0*vx - x0*vy).toDouble() / vx.toDouble()
            val a1 = other.vy.toDouble() / other.vx.toDouble()
            val b1 = (other.y0*other.vx - other.x0*other.vy).toDouble() / other.vx.toDouble()
            val x = (b - b1) / (a1 - a)
            val y = a * x + b
            val t = (x-x0.toDouble()) / vx.toDouble()
            val t1 = (x-other.x0.toDouble()) / other.vx.toDouble()

            if (min > x || min > y || max < x || max < y || t < 0 || t1 < 0) {
                return false
            }
            return true
        }

    }

    fun String.toHail2D(): Hail2D {
        val (x0, y0, vx, vy) = Regex("(\\w+), +(\\w+), +.+@ +([-\\d]+), +([-\\d]+)").find(this)!!.destructured
        return Hail2D(x0.toLong(), y0.toLong(), vx.toLong(), vy.toLong())
    }

    fun String.toHail3D(): Hail3D {
        val (x0, y0, z0, vx, vy, vz) = Regex("(\\w+), +(\\w+), +(\\w+).*@ +([-\\d]+), +([-\\d]+), +([-\\d]+)").find(this)!!.destructured
        return Hail3D(listOf(x0.toLong(), y0.toLong(), z0.toLong()), listOf(vx.toLong(), vy.toLong(), vz.toLong()))
    }

    fun solve() {
        val f = File("src/2023/inputs/day24.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        val lines = mutableListOf<String>()
        val hails2d = mutableListOf<Hail2D>()
        val hails3d = mutableListOf<Hail3D>()
        val same = listOf(mutableMapOf<Int, Int>(),mutableMapOf<Int, Int>(),mutableMapOf<Int, Int>())

        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lines.add(line)
            hails2d.add(line.toHail2D())
            hails3d.add(line.toHail3D())
            (0..2).forEach{same[it][hails3d.last().v[it].toInt()] = same[it].getOrDefault(hails3d.last().v[it].toInt(), 0) + 1}
        }

        val ts = listOf(0 to 21, 0 to -88, 0 to -154, 0 to 63, 1 to -18, 1 to -36, 1 to -12, 1 to -81,
        2 to 8, 2 to -59, 2 to -108, 2 to -15)
        val vts = mutableMapOf<Int, Set<Int>>()
        for (t in ts) {
            val (ix, value) = t
            val hs = hails3d.filter { it.v[ix] == value.toLong() }
            if (hs.size < 2) {
                continue
            }
            val ns = mutableSetOf<Int>()
            for (n in 1..2000) {
                val a = hs[0].p[ix] % n
                if (hs.all { it.p[ix] % n == a }) {
                    ns.add(value-n)
                    ns.add(value+n)
                }
            }
            if (vts.contains(ix)) {
                vts[ix] = ns.intersect(vts[ix]!!)
            } else {
                vts[ix] = ns
            }
//            println(ns)
        }

        val vx = -153
        val vy = -150
        val vz = 296

        val dvx0 = BigInteger.valueOf(vx-hails3d[0].v[0])
        val dvy0 = BigInteger.valueOf(vy-hails3d[0].v[1])
        val dvz0 = BigInteger.valueOf(vz-hails3d[0].v[2])
        val dvx1 = BigInteger.valueOf(vx-hails3d[1].v[0])
        val dvy1 = BigInteger.valueOf(vy-hails3d[1].v[1])
        val x0 = BigInteger.valueOf(hails3d[0].p[0])
        val y0 = BigInteger.valueOf(hails3d[0].p[1])
        val z0 = BigInteger.valueOf(hails3d[0].p[2])
        val x1 = BigInteger.valueOf(hails3d[1].p[0])
        val y1 = BigInteger.valueOf(hails3d[1].p[1])

        val yn = (x0 * dvy0 * dvy1 - y0 * dvx0 * dvy1 - x1 * dvy1 * dvy0 + y1 * dvx1 * dvy0)
        val yd = (dvx1 * dvy0 - dvx0 * dvy1)
        val yd1 = BigInteger.valueOf(vx-hails3d[2].v[0]) * BigInteger.valueOf(vy-hails3d[0].v[1]) -
                BigInteger.valueOf(vx-hails3d[2].v[1]) * BigInteger.valueOf(vy-hails3d[0].v[0])

        val v = listOf(-153, -150, 296)
        val min = mutableListOf<BigInteger?>(null, null, null)
        val max = mutableListOf<BigInteger?>(null, null, null)

        hails3d.forEach {
            h ->
            (0..2). forEach {
                ix ->
                val dv = v[ix] - h.v[ix]
                if (dv < 0 && (min[ix] == null || min[ix]!! < BigInteger.valueOf(h.p[ix]))) {
                    min[ix] = BigInteger.valueOf(h.p[ix])
                }
                if (dv > 0 && (max[ix] == null || max[ix]!! > BigInteger.valueOf(h.p[ix]))) {
                    max[ix] = BigInteger.valueOf(h.p[ix])
                }
            }
        }

        val m = yn % yd
        val y = yn / yd

        val t0 = (y0 - y)/dvy0
        val tm0 = (y0 - y)%dvy0

        val x = x0 - t0 * dvx0
        val z = z0 - t0 * dvz0

        for (i in 0 until hails2d.size) {
            for (j in i+1 until hails2d.size) {
                if (hails2d[i].collides(hails2d[j], 7.toDouble(), 27.toDouble())) {
                    sum ++
                }
                if (hails2d[i].collides(hails2d[j], 200000000000000.toDouble(), 400000000000000.toDouble())) {
                    sum1 ++
                }
            }
        }

        print("$sum $sum1\n")
        println(x + y + z)
    }
}