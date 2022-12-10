package `2021`

import common.Point
import java.io.File
import java.util.*

fun main() {
    Day13().solve()
}

class Day13 {
    val input0 = """
6,10
0,14
9,10
0,3
10,4
4,11
6,0
6,12
4,1
0,13
10,12
3,4
3,0
8,4
1,10
2,14
8,10
9,0

fold along y=7
fold along x=5
    """.trimIndent()

    private data class Instruction(val axis: String, val location: Int) {
        fun apply(p: Point): Point {
            when (axis) {
                "x" -> return p.foldX(location)
                "y" -> return p.foldY(location)
                else -> throw RuntimeException()
            }
        }
    }

    fun solve() {
        val f = File("src/2021/inputs/day13.in")
                val s = Scanner(f)
//        val s = Scanner(input0)

        var points = mutableSetOf<Point>()
        val instructions = mutableListOf<Instruction>()
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            if (line.contains("fold along ")) {
                line.removePrefix("fold along ").split("=").let {
                    instructions.add(Instruction(it[0], it[1].toInt()))
                }
            } else if (line.contains(",")) {
                line.split(",").let {
                    points.add(Point(it[0].toInt(), it[1].toInt()))
                }
            }
        }
        var points1 = points.map {  instructions[0].apply(it) }.toMutableSet()
        println(points.render())
        instructions.forEach{ instruction ->
            points = points.map {  instruction.apply(it) }.toMutableSet()
            println(points.render())
        }
        println("${points1.size} ${points.size}")
        println(points.render())
    }
}

private fun Set<Point>.render(): String {
    var r = StringBuilder("")
    for (y in 0..this.maxOf { it.y }) {
        for (x in 0..this.maxOf { it.x }) {
            r.append(
                if (this.contains(Point(x, y))) {
                    "#"
                } else {
                    "."
                })
        }
        r.append("\n")
    }
    return r.toString()
}

private fun Point.foldY(y1: Int): Point {
    if (y == y1 || y>2*y1) {
        throw RuntimeException()
    }
    if (y<y1) {
        return this
    }
    return Point(x, 2*y1-y)
}

private fun Point.foldX(x1: Int): Point {
    if (x == x1 || x>2*x1) {
        throw RuntimeException()
    }
    if (x<x1) {
        return this
    }
    return Point(2*x1-x, y)
}