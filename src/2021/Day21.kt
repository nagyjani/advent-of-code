import java.io.File
import java.math.BigInteger
import java.util.*
import kotlin.Comparator

fun main() {
    Day21().solve()
}


class Day21 {

    val input = """
Player 1 starting position: 4
Player 2 starting position: 8
    """.trimIndent()

    val input2 = """
Player 1 starting position: 8
Player 2 starting position: 1
    """.trimIndent()

    class Dice {
        var rolls = 0
        var last = 100
        fun roll(): Int {
            ++rolls
            last = last.addMod(1, 100)
            return last
        }
        fun roll(n: Int): Int {
            return (1..n).map{roll()}.sum()
        }
    }

    class DiracState(val pos1: Int, val score1: Int, val pos2: Int, val score2: Int, val next1: Boolean, val winScore: Int) {
        val player1won: Boolean get() = score1 >= winScore
        val player2won: Boolean get() = score2 >= winScore
        val ended: Boolean get() = player1won || player2won
        fun roll(n: Int): DiracState {
            val newPos1 = if (next1) pos1.addMod(n) else pos1
            val newScore1 = if (next1) score1 + newPos1 else score1
            val newPos2 = if (next1) pos2 else pos2.addMod(n)
            val newScore2 = if (next1) score2 else score2 + newPos2
            val newNext1 = !next1
            return DiracState(newPos1, newScore1, newPos2, newScore2, newNext1, winScore)
        }
        override fun hashCode(): Int {
            val m = listOf(31, 31, 11, 11, 2)
            return toIntList().zip(m).fold(0) {acc, it -> acc * it.second + it.first}
        }
        override operator fun equals(other: Any?): Boolean =
            (other is DiracState) && hashCode() == other.hashCode()
        fun toIntList(): List<Int> {
            return listOf(score1, score2, pos1, pos2, if (next1) 0 else 1)
        }
        override fun toString(): String {
            return "(" + hashCode() + ":" + toIntList().joinToString (",") + ")"
        }
    }

    fun solve() {

        val f = File("src/2021/inputs/day21.in")
        val s = Scanner(f)
//        val s = Scanner(input)

        s.useDelimiter("(\\s*Player . starting position: )|(\\s+)")

        val p1start = s.nextInt()
        val p2start = s.nextInt()

        var p1pos = p1start
        var p1score = 0
        var p2pos = p2start
        var p2score = 0
        var p1next = true
        val dice = Dice()

        while (p1score<1000 && p2score<1000) {
            if (p1next) {
                p1pos = p1pos.addMod(dice.roll(3))
                p1score += p1pos
            } else {
                p2pos = p2pos.addMod(dice.roll(3))
                p2score += p2pos
            }
            p1next = !p1next
        }

        val losingScore = if (p1score>p2score) p2score else p1score

        println("${losingScore * dice.rolls}")

        val startState1 = DiracState(p1start, 0, p2start, 0, true, 1000)
        var nextState1: DiracState = startState1

        val dice1 = Dice()
        while (!nextState1.ended) {
            nextState1 = nextState1.roll(dice1.roll(3))
        }
        val losingScore1 = if (nextState1.player1won) nextState1.score2 else nextState1.score1

        println("${losingScore1 * dice1.rolls}")

        val diracStates = TreeMap<DiracState, BigInteger>(object: Comparator<DiracState> {
            override fun compare(p0: DiracState?, p1: DiracState?): Int {
                if (p0 == null && p1 == null) return 0
                if (p0 == null) return 1
                if (p1 == null) return -1
                return p0.hashCode().compareTo(p1.hashCode())
            }
        })

        val startState = DiracState(p1start, 0, p2start, 0, true, 21)

        diracStates[startState] = BigInteger.ONE
        val diceMax = 3
        var p1won = BigInteger.ZERO
        var p2won = BigInteger.ZERO

        val diracDice =
            (1..3).flatMap { it1 -> (1..3).flatMap { it2 -> (1..3).map { it3 -> it1 + it2 + it3 } } }
                .fold(mutableMapOf<Int, BigInteger>()){acc, it -> acc[it] = (acc[it]?:BigInteger.ZERO).plus(BigInteger.ONE); acc}

        var sumUniverses: BigInteger
        var nextState: DiracState? = startState
        while (nextState != null) {
            if (!nextState.ended) {
                diracDice.forEach {
                    val rolledState = nextState!!.roll(it.key)

                    if (diracStates.get(rolledState) == null) {
                        diracStates.put(rolledState, BigInteger(diracStates.get(nextState)!!.toString()).times(it.value))
                    } else {
                        diracStates.put(
                            rolledState,
                            diracStates.get(nextState)!!.times(it.value).plus(diracStates.get(rolledState)!!)
                        )
                    }
                }
            } else {
                if (nextState.player1won) {
                    p1won = p1won.plus(diracStates.get(nextState)!!)
                } else {
                    p2won = p2won.plus(diracStates.get(nextState)!!)
                }
            }
            val lastState = nextState
//            diracStates.remove(nextState)
            nextState = diracStates.higherKey(nextState)
//            sumUniverses = diracStates.tailMap(nextState).values.fold(BigInteger.ZERO){acc, it -> acc.plus(it)}
            if (nextState != null && lastState.hashCode() > nextState.hashCode()) {
                println("${lastState.hashCode()} ${nextState.hashCode()}")
            }
        }

        println("$p1won $p2won")
    }

}

fun Int.addMod(n: Int, m: Int = 10): Int {
    val s = (this+n) % 10
    return if (s == 0) {m} else s
}
