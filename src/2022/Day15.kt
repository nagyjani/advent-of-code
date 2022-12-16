package `2022`

import common.Interval
import common.Point
import java.io.File
import java.util.*
import kotlin.math.abs

fun main() {
    Day15().solve()
}


class Day15 {

    val input1 = """
        Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        Sensor at x=9, y=16: closest beacon is at x=10, y=16
        Sensor at x=13, y=2: closest beacon is at x=15, y=3
        Sensor at x=12, y=14: closest beacon is at x=10, y=16
        Sensor at x=10, y=20: closest beacon is at x=10, y=16
        Sensor at x=14, y=17: closest beacon is at x=10, y=16
        Sensor at x=8, y=7: closest beacon is at x=2, y=10
        Sensor at x=2, y=0: closest beacon is at x=2, y=10
        Sensor at x=0, y=11: closest beacon is at x=2, y=10
        Sensor at x=20, y=14: closest beacon is at x=25, y=17
        Sensor at x=17, y=20: closest beacon is at x=21, y=22
        Sensor at x=16, y=7: closest beacon is at x=15, y=3
        Sensor at x=14, y=3: closest beacon is at x=15, y=3
        Sensor at x=20, y=1: closest beacon is at x=15, y=3
    """.trimIndent()

    class ExcludeLine {
        val exclusions = mutableListOf<Interval>()
        val beacons = mutableSetOf<Int>()
        fun exclude(start: Int, length: Int) {
            var ix: Int = 0
            var interval = Interval(start, length)
            while (ix<exclusions.size) {
                when (interval.compare(exclusions[ix])) {
                    0 -> {
                        interval = interval.merge(exclusions[ix])
                        exclusions.removeAt(ix)
                    }
                    1 -> {
                        exclusions.add(ix, interval)
                        return
                    }
                    -1 ->
                        ++ix
                }
            }
            exclusions.add(interval)
            return
        }

        fun minPos(start: Int, end: Int): Int {
            var r = -1
            exclusions.windowed(2){
                if (it[0].end() in start until end) {
                    r = it[0].end()+1
                }}
            return r
        }

        fun addBeacon(pos: Int) {
            beacons.add(pos)
        }

        fun excluded(pos: Int): Boolean {
            return exclusions.any { it.has(pos) }
        }

        fun noBeaconSize(): Int {
            var s = exclusions.sumOf { it.length }
            for (b in beacons) {
                if (excluded(b)) {
                    --s
                }
            }
            return s
        }

        fun render(start: Int, end: Int): String {
            val s = StringBuilder()
            for (i in start .. end) {
                if (beacons.contains(i)) {
                    s.append('B')
                } else if (excluded(i)) {
                    s.append('#')
                } else {
                    s.append('.')
                }
            }
            return s.toString()
        }
    }

    fun solve() {
        val f = File("/home/janos/Downloads/day15.in")
        val s = Scanner(f)
        val l = 2000000
//        val s = Scanner(input1)
//        val l = 10

        val beacons = mutableListOf<Point>()
        val scanners = mutableListOf<Point>()

        val min = 0
//        val max = 20
        val max = 4000000

        val excludeLines = MutableList<ExcludeLine> (max+1) {ExcludeLine()}

        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            val words = line.split(" ")
            if (!words.isEmpty()) {
                val sX = words[2].split(Regex("[=,:]"))[1].toInt()
                val sY = words[3].split(Regex("[=,:]"))[1].toInt()
                val bX = words[8].split(Regex("[=,:]"))[1].toInt()
                val bY = words[9].split(Regex("[=,:]"))[1].toInt()

                val scanner = Point(sX, sY)
                val beacon = Point(bX, bY)
                val distance = abs(sX-bX) + abs(sY-bY)

                for (y in min..max) {
                    val vd = abs(sY - y)
                    if (vd <= distance) {
                        val hd = abs(distance - vd)
                        excludeLines[y].exclude(sX - hd, hd*2+1)
                    }
                }

                if (bY >= min && bY <= max) {
                    excludeLines[bY].addBeacon(bX)
                }

                println("${words}")
                println("$sX $sY $bX $bY")
            }
        }

        println("${true}")

        println("${excludeLines[10].render(-10, 30)}")
        println("${excludeLines[11].render(-10, 30)}")
        println("${excludeLines[10].noBeaconSize()}")
        println("${excludeLines[2000000].noBeaconSize()}")

        var rx = 0
        var ry = 0

        for (y in min..max) {
            var x = excludeLines[y].minPos(min, max)
            if (x != -1) {
                println("$x $y ${x.toLong()*4000000+y.toLong()}")
            }
        }

    }
}

