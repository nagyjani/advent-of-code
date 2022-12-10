import java.io.File
import java.util.*

fun main() {
    Day05().solve()
}


class Day05 {

    val input1 = """
    [D]    
[N] [C]    
[Z] [M] [P]
 1   2   3 

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2
    """.trimIndent()



    fun solve() {
        val f = File("src/2022/inputs/day05.in")
        val s = Scanner(f)
//                val s = Scanner(input1)

        val stacks = MutableList<MutableList<Char>>(9){mutableListOf()}

        while (s.hasNextLine()) {
            val line = s.nextLine()
            if (line.startsWith(" 1")) {
                s.nextLine()
                break
            }
            println("$line")
            val chunks = line.split("[")
            var offset = chunks[0].length/4
            val restOfChunks = chunks.toMutableList()
            restOfChunks.removeFirst()
            restOfChunks.forEach{
                val c = it[0]
                stacks[offset].add(c)
                val t = it.split("]")
                if (t.size != 2) {
                    throw RuntimeException()
                }
                println("$offset $c")
                offset += t[1].length / 4 + 1
            }
            println("$chunks")
        }

        val stacks1 = stacks.map { it.reversed().toMutableList() }
        println("$stacks1")

        s.useDelimiter("[^\\d]")
        while (s.hasNext()) {
            val line = s.nextLine().trim()
            val chunks = line.split(Regex("[^\\d]+"))
            val n = chunks[1].toInt()
            val f = chunks[2].toInt()
            val t = chunks[3].toInt()
            println("$f $t $n")
//            for (i in 1..n) {
//                stacks1[t - 1].add(stacks1[f - 1].last())
//                stacks1[f - 1].removeLast()
//            }
            val toMove = stacks1[f - 1].takeLast(n)
            stacks1[t - 1].addAll(toMove)
            for (i in 1..n) {
                stacks1[f - 1].removeLast()
            }
            println("$stacks1")
        }

        println("$stacks1")

        val top = stacks1.map{if (it.size>0) {it.last()} else {' '}}


        println("${top.joinToString("")}")
    }
}

