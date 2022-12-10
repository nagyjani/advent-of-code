package `2021`

import java.io.File
import java.util.*

fun main() {
    Day02.solve()
}

data class Move(val dir: String, val steps: Int)

data class Pos(var depth: Int, var horizontal: Int) {
    fun apply(m: Move): Pos {
        when (m.dir) {
            "up" -> depth -= m.steps
            "down" -> depth += m.steps
            else -> horizontal += m.steps
        }
        return this
    }
}

data class Pos2(var depth: Int, var horizontal: Int, var aim: Int) {
    fun apply(m: Move): Pos2 {
        when (m.dir) {
            "up" ->
                aim -= m.steps
            "down" ->
                aim += m.steps
            else -> {
                horizontal += m.steps
                depth += aim * m.steps
            }
        }
        return this
    }
}

object Day02 {
    fun solve() {
        val f = File("src/2021/inputs/day02.in")
        val s = Scanner(f)
        val moves = mutableListOf<Move>()
        while (s.hasNextLine()) {
            val l = s.nextLine().trim()
            if (l.isNotEmpty()) {
                val i = l.split(" ")
                val dir = i[0]
                val steps = i[1].toInt()
                moves.add(Move(dir, steps))
            }
        }
        val pos = moves.fold(Pos(0, 0)){p, m -> p.apply(m)}
        val pos2 = moves.fold(Pos2(0, 0, 0)){p, m -> p.apply(m)}
        println("*: ${pos.depth * pos.horizontal} **: ${pos2.depth * pos2.horizontal}")
    }
}