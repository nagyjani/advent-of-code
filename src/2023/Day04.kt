package `2023`


import java.io.File
import java.util.*

fun main() {
    Day04().solve()
}


class Day04 {

    val input1 = """
        Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
        Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
        Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
        Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
        Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    fun solve() {
        val f = File("src/2023/inputs/day04.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var lineix = 0
        val winnums = mutableListOf<Int>()
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            lineix++
            val s1 = line.split(Regex("[:|]"))
            val numbers = s1[1].trim().split(Regex("[ ]+")).map { it.toInt() }.toSet()
            val winners = s1[2].trim().split(Regex("[ ]+")).map { it.toInt() }.toSet()
            val winningNumbers = numbers.intersect(winners)
            val p =
            if (winningNumbers.size > 0) {
                var p1 = 1
                winningNumbers.forEach { p1 *= 2 }
                p1/2
            } else {0}
            sum += p
            winnums.add(winningNumbers.size)
        }
        val cards = winnums.map { 1 }.toMutableList()
        for (i in 0 until lineix) {
            for (j in 1 .. winnums[i]) {
                if (i + j < cards.size) {
                    cards[i + j] += cards[i]
                } else {
                    break
                }
            }
            sum1 += cards[i]
        }

        print("$sum $sum1\n")
    }
}