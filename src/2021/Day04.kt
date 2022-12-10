package `2021`

import java.io.File
import java.util.*

fun main() {
    Day04().solve()
}

data class BingoResult(val turn: Int, val score: Int)

class BingoBoard(s: Scanner) {
    data class BingoField(val number: Int, var isDrawn: Boolean = false)
    val numbers = List(25){BingoField(s.nextInt())}
    val numberMap: Map<Int, Int>
    var result: BingoResult? = null
    init {
        val m = mutableMapOf<Int, Int>()
        numbers.forEachIndexed{ix, it -> m.put(it.number, ix)}
        numberMap = m.toMap()
    }
    fun draw(turn: Int, n: Int): Boolean {
        if (result != null) {
            return true
        }
        val ix = numberMap.get(n)
        if (ix != null) {
            numbers[ix].isDrawn = true
            if (allDrawn(row(ix)) || allDrawn(column(ix))) {
                result = BingoResult(turn, score(ix))
                return true
            }
        }
        return false
    }
    fun allDrawn(ixs: IntProgression): Boolean {
        for (i in ixs) {
            if (!numbers[i].isDrawn) {
                return false
            }
        }
        return true
    }
    fun row(ix: Int): IntProgression {
        val start = ix - ix % 5
        return start..start+4
    }
    fun column(ix: Int): IntProgression {
        val start = ix % 5
        return start..20+start step 5
    }
    fun score(ix: Int): Int {
        return numbers.filterNot { it.isDrawn }.map { it.number }.sum() * numbers[ix].number
    }
}

class Day04 {
    fun solve() {
        val f = File("src/2021/inputs/day04.in")
        val s = Scanner(f)
        val nums = s.nextLine().trim().split(",").map { it.toInt() }
        val boards = mutableListOf<BingoBoard>()
        while (s.hasNextInt()) {
            boards.add(BingoBoard(s))
        }
        for (ix in nums.indices) {
            if (boards.map { it.draw(ix, nums[ix]) }.all{it}) {
                break
            }
        }
        val sortedBoards = boards.sortedBy { it.result!!.turn }
        println("${sortedBoards.first().result} ${sortedBoards.last().result}")
    }
}