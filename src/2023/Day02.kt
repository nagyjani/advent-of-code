package `2023`


import java.io.File
import java.util.*
import kotlin.math.max

fun main() {
    Day02().solve()
}


class Day02 {

    val input1 = """
Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """.trimIndent()

    val input2 = """
two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen
    """.trimIndent()

    class Cubes() {

        var red = 0
        var green = 0
        var blue = 0
        fun from(s: String): Cubes {
            val m =
            s.split(",").filter { !it.isEmpty() }.map {
                val l = it.split(" ").filter { !it.isEmpty() }
                val n = l[0].toInt()
                val c = l[1]
                when (c) {
                    "red" -> red = n
                    "blue" -> blue = n
                    "green" -> green = n
                }
            }
            return this
        }

        fun leTo(c: Cubes): Boolean {
            return red <= c.red && green <= c.green && blue <= c.blue
        }

        fun add(c: Cubes): Cubes {
            val c1 = Cubes()
            c1.red = max(red, c.red)
            c1.green = max(green, c.green)
            c1.blue = max(blue, c.blue)
            return c1
        }

        fun power(): Int {
            return red * green * blue
        }
    }


    fun solve() {
        val f = File("src/2023/inputs/day02.in")
        val s = Scanner(f)
//                val s = Scanner(input1)
//        val s = Scanner(input2)
        val c = Cubes()
        c.from("12 red, 13 green, 14 blue")
        var sum1 = 0
        var sum2 = 0
        var lineix = 0
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            val games = line.split(Regex("[:;]")).drop(1)
            val c1 = Cubes()
            if (games.all { c1.from(it).leTo(c) }) {
                sum1 = sum1 + lineix
            }
            var c2 = Cubes()
            games.forEach { c2 = c1.from(it).add(c2) }
            val p = c2.power()
            sum2 += p
        }

        print("$sum1 $sum2\n")
    }
}