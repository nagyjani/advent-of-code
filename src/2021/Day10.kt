package `2021`

import java.io.File
import java.math.BigInteger
import java.util.*

fun main() {
    Day10().solve()
}

class Day10 {

    val input = """
[({(<(())[]>[[{[]{<()<>>
[(()[<>])]({[<{<<[]>>(
{([(<{}[<>[]}>{[]{[(<()>
(((({<>}<{<{<>}{[]{[]{}
[[<[([]))<([[{}[[()]]]
[{[{({}]{}}([{[{{{}}([]
{<[[]]>}<{[{[{[]{()[[[]
[<(<(<(<{}))><([]([]()
<{([([[(<>()){}]>(<<{{
<{([{{}}[<[[[<>{}]]]>[]]
""".trimIndent()

    class MyStack(s: String) {
        var top = -1
        val s = MutableList(s.length){-1}
        var errorPoint = 0
        init {
            s.forEach {add(it)}
        }
        fun add(c: Char) {
            if (errorPoint>0) {
                return
            }
            val points = listOf(3, 57, 1197, 25137)
            val openerIx = "([{<".indexOf(c)
            val closeIx = ")]}>".indexOf(c)
            if (openerIx>-1) {
                s[++top] = openerIx
            } else if (closeIx<0) {
                println("ERROR ${closeIx} '${c}'")
            } else if (top == -1 || s[top] != closeIx) {
                errorPoint = points[closeIx]
            } else {
                --top
            }
        }
        fun autoCompletePoints(): BigInteger {
            if (errorPoint>0) {
                return BigInteger.ZERO
            }
            val points = listOf(1, 2, 3, 4)
            return s.slice(0..top).reversed().fold(BigInteger.ZERO){ p, it -> p.multiply(BigInteger.valueOf(5)).plus(
                BigInteger.valueOf(points[it].toLong()))}
        }
    }

    fun solve() {
        val f = File("src/2021/inputs/day10.in")
        val s = Scanner(f)
//        val s = Scanner(input)
        var sumErrorPoint = 0
        val autoCompletePoints = mutableListOf<BigInteger>()
        while (s.hasNextLine()) {
            val l = s.nextLine().trim()
            val m = MyStack(l)
            sumErrorPoint += m.errorPoint
            if (m.errorPoint == 0) {
                autoCompletePoints.add(m.autoCompletePoints())
            }
        }
        println("${sumErrorPoint} ${autoCompletePoints.sorted()[autoCompletePoints.size/2]} ${autoCompletePoints}")
    }
}