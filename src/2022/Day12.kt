package `2022`

import common.Adj2D
import java.io.File
import java.util.*

fun main() {
    Day12().solve()
}


class Day12 {

    val input1 = """
        Sabqponm
        abcryxxl
        accszExk
        acctuvwj
        abdefghi
    """.trimIndent()


    data class Square(
        val height: Int,
        var ix: Int,
        var minStep: Int = 20000,
        var finished: Boolean = false
    )

    // 20000 > 136 * 41
    val MAX_STEP = 20000

    fun List<Square>.shortest(startIx: Int, endIx: Int, a: Adj2D): Int {

        val startSquare = get(startIx)
        val endSquare = get(endIx)

        var nextSquare = startSquare
        startSquare.finished = true
        startSquare.minStep = 0
        while (!endSquare.finished) {
//            println(nextSquare.ix)
//            println(a.adj4(nextSquare.ix))
//            println(a.adj4(nextSquare.ix).map{get(it)})
            a.adj4(nextSquare.ix).map{get(it)}.filter {
                !it.finished && (it.height-nextSquare.height < 2)
            }.forEach {
                if (it.minStep > nextSquare.minStep+1) {
                    it.minStep = nextSquare.minStep+1
                }
            }
            nextSquare = filter { !it.finished }.minBy { it.minStep }
            nextSquare.finished = true
            if (nextSquare.minStep == MAX_STEP) {
                break
            }
//            println(nextSquare)
        }

        return endSquare.minStep
    }
    fun solve() {
        val f = File("src/2022/inputs/day12.in")
        val s = Scanner(f)
//                val s = Scanner(input1)

        var y0 = 0

        val squares = mutableListOf<Square>()

        var ix = 0
        var startIx = 0
        var endIx = 0

        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            if (!line.isEmpty()) {
                ++y0
                squares.addAll(line.map{
                    if (it == 'S') {
                        startIx = ix
                        Square(0, ix++)
                    } else if (it == 'E') {
                        endIx = ix
                        Square(25, ix++)
                    } else {
                        Square(it - 'a', ix++)
                    }
                })
            }
        }

        val y = y0
        val x = squares.size/y

        val a = Adj2D(x, y)

        val minNum = squares.filter { it.height == 0 }.size
        var mix = 0
        val min1 = squares.filter { it.height == 0 }.map {
            val m = squares.map { it.copy() }.shortest(it.ix, endIx, a)
            println("$m ${++mix}/$minNum")
            m
        }.filter { it != MAX_STEP }.min()

        println("${x} ${y} ${min1} ${squares.shortest(startIx, endIx, a)}")
    }
}

