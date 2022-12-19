package `2022`

import common.*
import common.Linearizer
import common.Offset
import java.io.File
import java.math.BigInteger
import java.util.*
import kotlin.collections.List

fun main() {
    Day17().solve()
}


class Day17 {

    val input1 = """
        >>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>
    """.trimIndent()
    //   3
    //   2
    //   1
    // x 012345
    //   y
    class Shape(number: Int, val l: Linearizer) {
        val offsets: List<Offset>
        val top: Int
        init {
            offsets = when (number % 5) {
                0 -> listOf(
                    l.offset(0, 0),
                    l.offset(1, 0),
                    l.offset(2, 0),
                    l.offset(3, 0),
                )

                1 -> listOf(
                    l.offset(1, 0),
                    l.offset(0, 1),
                    l.offset(1, 1),
                    l.offset(2, 1),
                    l.offset(1, 2),
                )

                2 -> listOf(
                    l.offset(0, 0),
                    l.offset(1, 0),
                    l.offset(2, 0),
                    l.offset(2, 1),
                    l.offset(2, 2),
                )

                3 -> listOf(
                    l.offset(0, 0),
                    l.offset(0, 1),
                    l.offset(0, 2),
                    l.offset(0, 3)
                )

                else -> listOf(
                    l.offset(0, 0),
                    l.offset(0, 1),
                    l.offset(1, 0),
                    l.offset(1, 1)
                )
            }
        }
        init {
            top =
                when (number % 5) {
                    0 -> 0
                    1 -> 2
                    2 -> 2
                    3 -> 3
                    else -> 1
                }
        }
    }
    data class Rock(
        val cave: Cave,
        val number: Int,
        val x: Int, val y: Int,
        val nextMove: RockState = RockState.HORIZONTAL_MOVE,
        val shape: Shape = Shape(number, cave.l)) {
        fun tiles(): Iterable<Int> {
            return shape.offsets.around(cave.l.toIndex(x, y))
        }
        fun move(wind: Wind): Rock {
            var x1 = x
            var y1 = y
            var nextMove1 = nextMove
            if (nextMove == RockState.HORIZONTAL_MOVE) {
                nextMove1 = RockState.VERTICAL_MOVE
                if (wind.next() == Move.LEFT) {
                    --x1
                } else {
                    ++x1
                }
            } else if (nextMove == RockState.VERTICAL_MOVE) {
                nextMove1 = RockState.HORIZONTAL_MOVE
                --y1
            }
            return Rock(cave, number, x1, y1, nextMove1, shape)
        }

        fun noMove(): Rock {
            val nextMove1 =
                if (nextMove == RockState.HORIZONTAL_MOVE) {
                    RockState.VERTICAL_MOVE
                } else {
                    RockState.STILL
                }
            return Rock(cave, number, x, y, nextMove1, shape)
        }

        fun isStill(): Boolean {
            return nextMove == RockState.STILL
        }

        fun top() = shape.top + y

        fun bottom() = y
    }

    class Wind(val windStr: String) {
        var counter = 0
        val l = windStr.length
        fun next(): Move {
            if (windStr[counter++%l] == '<') {
                return Move.LEFT
            }
            return Move.RIGHT
        }

        fun ix() = counter%l
    }

    enum class Move {
        DOWN, LEFT, RIGHT
    }
    enum class RockState {
        VERTICAL_MOVE, HORIZONTAL_MOVE, STILL
    }

    class Cave(windStr: String, val width: Int, size: Int = 100000) {
        val wind = Wind(windStr)
        var rockCounter = 0
        val widthWithWalls = width + 2
        val l = Linearizer(widthWithWalls, size)
        var bottom = 0
        var top = bottom
        val bits = (0 until widthWithWalls).map { 0b1 shl it }
        val innerMask = bits.subList(1, widthWithWalls-1).fold(0){acc, it1 -> acc or it1}
        val windStr1 = StringBuilder()
        var lastWind = 100000

        fun topCode(n: Int) = (n and innerMask) shr 1

        var tiles = MutableList(l.dimensions[1]) {
            if (it == 0) {
//                println(bits.fold(0){acc, it1 -> acc or it1})
                bits.fold(0){acc, it1 -> acc or it1}
            } else {
//                println(bits[0] or bits[width-1])
                bits[0] or bits[widthWithWalls-1]
            }
        }
        var rock: Rock = newRock()

        fun newRock(): Rock {
            rock = Rock(this, rockCounter++, 3, top+4)
            add(rock, true)
            return rock
        }

        fun evenTop(i: Int): Int? {
            if (i<bottom+2) {
                return null
            }
//            println(render(10))
//            val t1 = topCode(tiles[i])
//            val t2 = topCode(tiles[i-1])
//            val t3 = (topCode(tiles[i]) or topCode(tiles[i-1]))
//            innerMask
            if ((topCode(tiles[i]) or topCode(tiles[i-1])) == topCode(innerMask)) {
                return topCode(tiles[i])
            }
            return null
        }

        fun moveUntil(number: Int) {
            while (rockCounter < number || !rock.isStill()) {
//                if (rockCounter % 1000 == 0) {
//                    println("$rockCounter/$number")
//                }
//                println(render(10))
                move()
            }
            newTop(rock)
        }
        fun move() {
            if (rock.isStill()) {
                newTop(rock)
                rock = newRock()
            } else {
                rock = move(rock)
            }
        }

        fun get(x: Int, y: Int): Boolean {
            return tiles[y] and bits[x] != 0b0
        }

        fun set(x: Int, y: Int, v: Boolean) {
            if (v != get(x, y)) {
                tiles[y] = tiles[y] xor bits[x]
            }
        }

        fun get(ix: Int): Boolean {
            val c = l.toCoordinates(ix)
            return get(c[0], c[1])
        }

        fun set(ix: Int, v: Boolean) {
            val c = l.toCoordinates(ix)
            return set(c[0], c[1], v)
        }

        fun newTop(rock: Rock) {
//            for (i in rock.bottom() .. rock.top()) {
//                val t = evenTop(i)
//                if (t != null) {
//                    println("$i $t")
//                    println(render(5))
//                }
//            }

//            val t = evenTop(top)
//            if (t != null) {
//                println("$top $t")
//                println(render(5))
//            }
            top = maxOf(top, rock.top())

            val ix = wind.ix()
//            if (rockCounter % 5 == 2 && wind.ix() <= lastWind) {
//                windStr1.append("${wind.ix()} ")
//            }
            if (wind.ix() <= lastWind) {
                windStr1.append("${rockCounter % 5}: ${wind.ix()}, ")
                println("$top - $rockCounter ${rockCounter % 5}: ${wind.ix()}, ")
                println(render(20))
            }
            lastWind = wind.ix()


//            val width = l.dimensions[0]
//            val height = l.dimensions[1]
//            if (top - bottom + 10 > height) {
//                val newBottom = height/2
//                for (y in top downTo newBottom+1) {
//                    for (x in 1 .. width) {
//                        tiles[l.toIndex(x, y-newBottom)] = tiles[l.toIndex(x, y)]
//                        tiles[l.toIndex(x, y)] = 0
//                    }
//                }
//                bottom += newBottom
//            }
        }

        fun move(rock: Rock): Rock {
            val rock1 = rock.move(wind)
            remove(rock, true)
            if (!add(rock1)) {
                add(rock, true)
                return rock.noMove()
            }
            return rock1
        }
        fun add(rock: Rock, force: Boolean = false): Boolean {
            if (rock.tiles().any {get(it)}) {
                if (force) {
                    println(rock.tiles().toList())
                    println(rock.tiles().toList().map{l.toCoordinates(it).joinToString ()})
                    println(render(10))
                    throw RuntimeException()
                }
                return false
            }
            rock.tiles().forEach { set(it, true) }
            return true
        }
        fun remove(rock: Rock, force: Boolean = false): Boolean {
            if (rock.tiles().any { !get(it) }) {
                if (force) {
                    println(rock.tiles().toList())
                    println(rock.tiles().toList().map{l.toCoordinates(it).joinToString ()})
                    println(render(10))
                    throw RuntimeException()
                }
                return false
            }
            rock.tiles().forEach { set(it, false) }
            return true
        }

        fun render(h: Int): String {
            return render(maxOf(0, top-h), top+1)
        }
        fun render(bottom: Int, top: Int): String {
            val s = StringBuilder()
            s.appendLine("$bottom .. $top")
            for (y in top downTo bottom) {
                for (x in 0 until l.dimensions[0]) {
                    s.append(
                        if (get(x ,y)) {
                            '#'
                        } else {
                            '.'
                        })
                }
                s.appendLine()
            }
            return s.toString()
        }
    }

    fun solve() {
        val f = File("src/2022/inputs/day17.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
        val winds0 = StringBuilder()
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            winds0.append(line)
        }
        val winds = winds0.toString()

        val width = 7
        val c = Cave(winds, width)


//        c.moveUntil(1)
//        println(c.render(0, 20))
//        println("${winds}")

        //        c.moveUntil(12200)
//        println("${c.top}")
//        println(c.windStr1)
//        println(c.windStr.toString().takeLast(1000))

        val targetRocks = BigInteger("1000000000000")
        val rockBase = BigInteger("1724")
        val topBase = BigInteger("2617")
        val rockPeriod = BigInteger("1705")
        val topPeriod = BigInteger("2618")
        val numOfFullPeriods = targetRocks.minus(rockBase).divide(rockPeriod)
        val newRockBase = targetRocks.minus(rockPeriod.times(numOfFullPeriods)).toInt() // 3290

        c.moveUntil(1724)
        val top0 = c.top
        c.moveUntil(3290)
        val top1 = c.top
        println("$top0 $top1 ${top1 - top0}") // 2617 5008 2391
        println(newRockBase)

        val r = numOfFullPeriods.times(topPeriod).plus(topBase).plus(BigInteger("2391"))

        println(r)

//        println("${winds.length}")
//
//        val period1 = 5 * winds.length
//        val period2 = 10 * winds.length
//
//        var lastTop = 0
//        for (i in 1..20) {
//            c.moveUntil(period1 * i)
//            println("$i ${c.top() - lastTop} ${c.rockCounter}")
//            lastTop = c.top()
//            println(c.render(20))
//        }

//        c.moveUntil(period1)
//        val top1 = c.top().toLong()
//        val firstPeriodTop = top1
//        c.moveUntil(period2)
//        val top2 = c.top().toLong()
//        val middlePeriodTopDiff = top2 - top1
//
//        val rockCount = 1000000000000L
//
//        val lastRocks = rockCount % period1
//        val period3 = (period2 + lastRocks).toInt()
//        c.moveUntil(period3)
//        val top3 = c.top().toLong()
//        val lastPeriodTopDiff = top3 - top2
//
//        val middlePeriodNum = rockCount / period1 - 1
//
//        val h = firstPeriodTop + middlePeriodTopDiff * middlePeriodNum + lastPeriodTopDiff
//
//        println("$period1 $top1 $period2 $top2 $period3 $top3")
//        println("$h = $firstPeriodTop + $middlePeriodTopDiff * $middlePeriodNum + $lastPeriodTopDiff")

    }
}

