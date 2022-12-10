package `2021`

import common.Point
import java.io.File
import java.util.*
import kotlin.math.abs

fun main() {
    Day05().solve()
}

fun sign(a: Int): Int {
    if (a<0) {
        return -1
    }
    if (a>0) {
        return 1
    }
    return 0
}

class Line(p1: Point, p2: Point) {
    private val leftTop: Point
    private val rightBottom: Point
    init {
        if (p1 < p2) {
            leftTop = p1
            rightBottom = p2
        } else {
            leftTop = p2
            rightBottom = p1
        }
    }
    fun points(): Sequence<Point> {
        return generateSequence(leftTop){
            if (it == rightBottom) {
                null
            } else {
                val xGrad = sign(rightBottom.x-leftTop.x)
                val yGrad = sign(rightBottom.y-leftTop.y)
                Point(it.x + xGrad, it.y + yGrad)
            }
        }
    }
    fun isAligned2(): Boolean {
        return leftTop.x == rightBottom.x || leftTop.y == rightBottom.y
    }
    fun isAligned4(): Boolean {
        return isAligned2() || abs(leftTop.x - rightBottom.x) == abs(leftTop.y-rightBottom.y)
    }
}

class Bottom {
    val points = mutableMapOf<Point, Int>()
    fun drawLine(l: Line) {
        for (p in l.points()) {
            points[p] = points.getOrDefault(p, 0) + 1
        }
    }
}

class Day05 {
    fun solve() {
        val f = File("src/2021/inputs/day05.in")
        val s = Scanner(f)
        val b2 = Bottom()
        val b4 = Bottom()
        while (s.hasNextLine()) {
            val ls = s.nextLine().trim()
            val cs = ls.split(" -> ").flatMap { it.split(",")}.filter { it.isNotEmpty() }.map { it.toInt() }
            val line = Line(Point(cs[0], cs[1]), Point(cs[2], cs[3]))
            if (line.isAligned2()) { b2.drawLine(line) }
            if (line.isAligned4()) { b4.drawLine(line) }
        }
        println("${b2.points.filter { it.value>1 }.count()} ${b4.points.filter { it.value>1 }.count()}")
    }
}