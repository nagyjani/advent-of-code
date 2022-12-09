import java.io.File
import java.util.*

fun main() {
    Day01().solve()
}


class Day01 {

    val input1 = """
1000
2000
3000

4000

5000
6000

7000
8000
9000

10000
    """.trimIndent()


    fun solve() {
        val f = File("src/2022/inputs/aoc2201.in")
        val s = Scanner(f)
//                val s = Scanner(input1)
        val cals = mutableListOf<MutableList<Int>>(mutableListOf())
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                cals.add(mutableListOf())
                continue
            }
            cals.last().add(line.toInt())
        }

        val maxCals = cals.maxOf { it.sum() }

        val sumCals = cals.map { it.sum() }.sortedDescending()

        print("$maxCals ${sumCals[0]+sumCals[1]+sumCals[2]}\n")
    }
}