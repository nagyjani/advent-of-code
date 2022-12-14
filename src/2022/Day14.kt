package `2022`

import common.Linearizer
import common.*

import java.io.File
import java.util.*
import kotlin.text.StringBuilder

fun main() {
    Day14().solve()
}


class Day14 {


    enum class Material {
        SAND, ROCK, AIR;
        fun toChar(): Char {
            return when (this) {
                SAND -> 'o'
                ROCK -> '#'
                AIR -> '.'
            }
        }
    }
    class Cave {

        val l = Linearizer(1000, 1000)
        var sandCounter = 0
        val material = hashMapOf<Int, Material>()
        var minX = 1000
        var maxX = 0
        var minY = 1000
        var maxY = 0
        var bottom = 1000

        fun setBottom() {
            bottom = maxY + 2
            maxY = bottom
        }

        fun isBlocked(ix: Int): Boolean {
            return getMaterial(ix) != Material.AIR
        }
        fun isBlocked(x: Int, y: Int): Boolean {
            return getMaterial(x,y) != Material.AIR
        }

        fun getMaterial(x: Int, y: Int): Material {
            return getMaterial(l.toIndex(x,y))
        }
        fun getMaterial(ix: Int): Material {
            if (l.toCoordinates(ix)[1] >= bottom) {
                return Material.ROCK
            }
            if (material.containsKey(ix)) {
                return material[ix]!!
            }
            return Material.AIR
        }

        fun setBlocked(ix: Int, m: Material) {
            material[ix] = m
        }
        fun setBlocked(x: Int, y: Int, m: Material) {
            setBlocked(l.toIndex(x,y), m)
        }
        fun blockedLine(x1: Int, y1: Int, x2: Int, y2: Int) {
            minX = minOf(minX, x1, x2)
            minY = minOf(minY, y1, y2)
            maxX = maxOf(maxX, x1, x2)
            maxY = maxOf(maxY, y1, y2)
            if (x1 == x2) {
                if (y1 < y2) {
                    blockedLineV(x1, y1, y2)
                } else {
                    blockedLineV(x1, y2, y1)
                }
            } else {
                if (x1 < x2) {
                    blockedLineH(y1, x1, x2)
                } else {
                    blockedLineH(y1, x2, x1)
                }
            }
        }
        fun blockedLineH(y: Int, x1: Int, x2: Int) {
            for (i in x1..x2) {
                setBlocked(i, y, Material.ROCK)
            }
        }
        fun blockedLineV(x: Int, y1: Int, y2: Int) {
            for (i in y1..y2) {
                setBlocked(x, i, Material.ROCK)
            }
        }

        fun dropSand(): Boolean {
            var ix = l.toIndex(500, minY-1)
            var moving = true
            val nextPlaces = mutableListOf(l.offset(0,1), l.offset(-1, 1), l.offset(1, 1))
            while (moving) {
                val y = l.toCoordinates(ix).get(1)
                if (y > maxY) {
                    break
                }
//                println("${l.toCoordinates(ix)[0]} ${l.toCoordinates(ix)[1]}")
                moving = false
                for (nextIx in nextPlaces.around(ix)) {
//                    println("? ${l.toCoordinates(nextIx)[0]} ${l.toCoordinates(nextIx)[1]}")
                    if (!isBlocked(nextIx)) {
//                        println("! ${l.toCoordinates(nextIx)[0]} ${l.toCoordinates(nextIx)[1]}")
                        ix = nextIx
                        moving = true
                        break
                    }
                }
            }
            if (!moving) {
                ++sandCounter
                val xy = l.toCoordinates(ix)
                if (xy[1] < minY) {
                    minY = xy[1]
                }
                setBlocked(ix, Material.SAND)
                if (xy[0] == 500 && xy[1] == 0) {
                    return true
                }
            }
//            println(render())
            return moving
        }

        fun render(): String {
            val s = StringBuilder()
            for (y in minY..maxY) {
                for (x in minX..maxX) {
                    s.append(getMaterial(x,y).toChar())
                }
                s.appendLine()
            }
            return s.toString()
        }
    }

    val input1 = """
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent()


    fun solve() {
        val f = File("/home/janos/Downloads/day14.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
        val cave = Cave()
        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            val words = line.split(" -> ")
            if (!words.isEmpty()) {
                words.windowed(2){
                    it.map{xy -> xy.split(",")}.apply {
                        cave.blockedLine(get(0)[0].toInt(), get(0)[1].toInt(), get(1)[0].toInt(), get(1)[1].toInt())
                    }
                }
                println("${words}")
            }
        }

        println("${cave.render()}")

        cave.setBottom()

        println("${cave.render()}")

        while (!cave.dropSand()) {
//            println("${cave.render()}")
        }
        println("${cave.render()}")
        println("${cave.sandCounter}")
    }
}

