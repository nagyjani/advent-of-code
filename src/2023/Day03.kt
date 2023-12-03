package `2023`


import java.io.File
import java.util.*

fun main() {
    Day03().solve()
}


class Day03 {

    val input1 = """
        467..114..
        ...*......
        ..35..633.
        ......#...
        617*......
        .....+.58.
        ..592.....
        ......755.
        ...$.*....
        .664.598..
    """.trimIndent()

    val input2 = """
        
    """.trimIndent()

    class PartNumber {
        var digits = ""
        val indexes = mutableListOf<Int>()
        fun add(index: Int, char: Char): PartNumber {
            digits += char
            indexes.add(index)
            return this
        }

        fun isSymbol(): Boolean {
            return !digits.all { it.isDigit() }
        }

        fun num() : Int {
            return digits.toInt()
        }

        fun neighbours(x: Int, y: Int): List<Int> {
            val r = mutableSetOf<Int>()
            for (ix in indexes) {
                for (i in -1..1) {
                    for (j in -1..1) {
                        val x1 = ix%x+i
                        val y1 = ix/x+j
                        val ix1 = y1*x + x1
                        if (x1 in 0 until x && y1 in 0 until y && ix1 !in indexes) {
                            r.add(ix1)
                        }
                    }
                }
            }
            return r.toList()
        }

        fun isGear(): Boolean {
            return digits == "*"
        }
    }

    fun solve() {
        val f = File("src/2023/inputs/day03.in")
        val s = Scanner(f)
//                val s = Scanner(input1)
//        val s = Scanner(input2)
        var sum = 0
        var sum1 = 0
        var x = 0
        var y = 0
        var lineix = 0
        val parts = mutableMapOf<Int, PartNumber>()
        val partList = mutableListOf<PartNumber>()
        while (s.hasNextLine()) {
            lineix++
            val line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            x = line.length
            val ll = line.toCharArray().toList()
            ll.mapIndexed {
           index, c ->
                val index1 = (lineix-1) * x + index
                if (c != '.') {
                    val p =
                            if (index1 == 0 || !c.isDigit() || index1 - 1 !in parts.keys || parts[index1 - 1]!!.isSymbol()) {
                                val p1 = PartNumber()
                                partList.add(p1)
                                p1
                            } else {
                                parts[index1 - 1]!!
                            }
                    parts[index1] = p.add(index1, c)
                }
            }
            ++y
        }

        for (p in partList) {
            val n = p.neighbours(x, y)
            if (n.any { it in parts.keys && parts[it]!!.isSymbol()} && !p.isSymbol()) {
                sum += p.num()
            }
            if (p.isGear()) {
                val ns = n.filter{it in parts.keys}.map{parts[it]!!}.filter { !it.isSymbol() }.toSet().toList()
                if (ns.size == 2) {
                    sum1 += ns[0].num() * ns[1].num()
                }
            }
        }

        print("$sum $sum1\n")
    }
}