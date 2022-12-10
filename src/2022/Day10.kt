import java.io.File
import java.util.*

fun main() {
    Day10().solve()
}


class Day10 {

    val input1 = """
addx 15
addx -11
addx 6
addx -3
addx 5
addx -1
addx -8
addx 13
addx 4
noop
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx -35
addx 1
addx 24
addx -19
addx 1
addx 16
addx -11
noop
noop
addx 21
addx -15
noop
noop
addx -3
addx 9
addx 1
addx -3
addx 8
addx 1
addx 5
noop
noop
noop
noop
noop
addx -36
noop
addx 1
addx 7
noop
noop
noop
addx 2
addx 6
noop
noop
noop
noop
noop
addx 1
noop
noop
addx 7
addx 1
noop
addx -13
addx 13
addx 7
noop
addx 1
addx -33
noop
noop
noop
addx 2
noop
noop
noop
addx 8
noop
addx -1
addx 2
addx 1
noop
addx 17
addx -9
addx 1
addx 1
addx -3
addx 11
noop
noop
addx 1
noop
addx 1
noop
noop
addx -13
addx -19
addx 1
addx 3
addx 26
addx -30
addx 12
addx -1
addx 3
addx 1
noop
noop
noop
addx -9
addx 18
addx 1
addx 2
noop
noop
addx 9
noop
noop
noop
addx -1
addx 2
addx -37
addx 1
addx 3
noop
addx 15
addx -21
addx 22
addx -6
addx 1
noop
addx 2
addx 1
noop
addx -10
noop
noop
addx 20
addx 1
addx 2
addx 2
addx -6
addx -11
noop
noop
noop
    """.trimIndent()

    val input2 = """
noop
addx 3
addx -5
    """.trimIndent()

    enum class CommandName {
        NOOP, ADDX
    }

    data class Command(val name: CommandName, val param: Long = 0)

    fun solve() {
        val f = File("src/2022/inputs/day10.in")
        val s = Scanner(f)
//        val s = Scanner(input1)

        val commands = mutableListOf<Command>()
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            val words = line.split(" ")
            if (words.size == 1) {
                commands.add(Command(CommandName.NOOP))
            } else if (words.size == 2) {
                commands.add(Command(CommandName.ADDX, words[1].toLong()))
            }
        }

        val cycles = mutableListOf<Long>()
        var x = 1L

        for (c in commands) {
            cycles.add(x)
            if (c.name == CommandName.ADDX) {
                cycles.add(x)
                x += c.param
            }
        }

        var r = 0L

        for (i in 20 .. 220 step 40) {
            println("${cycles[i-1]} * $i = ${cycles[i-1] * i}")
            r += cycles[i-1] * i
        }

        println("${r}")

//        val screen = MutableList(240){'.'}

        for (j in 0 until 6) {
            for (i in 0 until 40) {
                x = cycles[j * 40 + i]
                if (i == (x-1).toInt() || i == (x+0).toInt() || i == (x+1).toInt()) {
//                    screen[j * 40 + i] = '#'
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
    }
}
