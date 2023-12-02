package `2023`

import java.io.File
import java.util.*

fun main() {
    Day01().solve()
}


class Day01 {

    val input1 = """
1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
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


    val input3 = """
3fourrbvvlrsrbb2858
vlz4six89
75threeb
fourp783fiveseventhree
2gxvcbsmn6
896
dnblxtxxpstlsix56
4fivecl185
    """.trimIndent()

    val m =
            mapOf("1" to 1,
                    "2" to 2,
                    "3" to 3,
                    "4" to 4,
                    "5" to 5,
                    "6" to 6,
                    "7" to 7,
                    "8" to 8,
                    "9" to 9,
                    "one" to 1,
                    "two" to 2,
                    "three" to 3,
                    "four" to 4,
                    "five" to 5,
                    "six" to 6,
                    "seven" to 7,
                    "eight" to 8,
                    "nine" to 9
            )

    fun String.find1(): Int? {
        return  m.keys.map { m[it] to this.indexOf(it) }.filter { it.second != -1 }.minBy { it.second }.first
    }

    fun String.find2(): Int? {
        return m.keys.map { m[it] to this.lastIndexOf(it) }.filter { it.second != -1 }.maxBy { it.second }.first
    }

    fun solve() {
        val f = File("src/2023/inputs/day01.in")
        val s = Scanner(f)
//        val s = Scanner(input3)
//                val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
//            val next = 10 * line.find { it.isDigit() }.toString().toInt() +
//                    line.findLast { it.isDigit() }.toString().toInt()
            val next1 = line.find1()!! * 10 + line.find2()!!
//            sum += next
            sum1 += next1
        }

        print("$sum $sum1\n")
    }
}