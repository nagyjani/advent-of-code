import java.io.File
import java.util.*

fun main() {
    Day09().solve()
}


class Day09 {

    val input1 = """
R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2
    """.trimIndent()

    val input2 = """
R 5
U 8
L 8
D 3
R 17
D 10
L 25
U 20
    """.trimIndent()

    data class Step(val direction: Char, val size: Int)

    data class Point(val x: Long, val y: Long)

    fun Point.step(direction: Char): Point {
        return when (direction) {
            'L' -> Point(x-1, y)
            'R' -> Point(x+1, y)
            'U' -> Point(x, y-1)
            'D' -> Point(x, y+1)
            else -> throw RuntimeException()
        }
    }

    fun Point.stepTo(p: Point): Point {
        val x1 = if (x > p.x) {x-1} else if (x < p.x) {x+1} else {x}
        val y1 = if (y > p.y) {y-1} else if (y < p.y) {y+1} else {y}
        if (Point(x1, y1) == p) {
            return this
        }
        return Point(x1, y1)
    }

    fun hash(p: Point, maxDimension: Long): Long {
        return p.x + maxDimension + p.y * maxDimension * 2
    }

    fun solve() {
        val f = File("src/2022/inputs/day09.in")
        val s = Scanner(f)
//                val s = Scanner(input2)

        val steps = mutableListOf<Step>()
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            val words = line.split(" ")
            if (!words.isEmpty()) {
                steps.add(Step(words[0][0], words[1].toInt()))
            }
        }

        val maxDimension = steps.size.toLong() + 1

        val l = 10

        val rope = MutableList(l){ Point(0, 0) }

        val visited = mutableSetOf(hash(rope.last(), maxDimension))

        steps.forEach{
            for (i in 0 until it.size) {
                rope[0] = rope[0].step(it.direction)
                for (i in 1 until l) {
                    rope[i] = rope[i].stepTo(rope[i-1])
                }
                visited.add(hash(rope.last(), maxDimension))
            }}

        println("${visited.size}")
    }
}

