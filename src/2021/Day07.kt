package `2021`

import java.io.File
import java.lang.Math.abs
import java.util.*

fun main() {
    Day07().solve()
}

fun List<Int>.fuel(pos: Int): Int {
    return sumOf{((abs(it-pos))*(abs(it-pos)+1))/2}
}

class Day07 {
    fun solve() {
        val f = File("src/2021/inputs/day07.in")
        val s = Scanner(f)
//        val s = Scanner("16,1,2,0,4,2,7,1,2,14")
        val posList = s.nextLine().trim().split(",").map{it.toInt()}.sorted()
        val median = posList[posList.size/2]
        println("${posList.map {abs(it-median)}.sum()} ${median}")
        var minFuelPos = posList.first()
        var minFuel = posList.fuel(minFuelPos)
        for (i in posList.first()..posList.last()) {
            val fuel = posList.fuel(i)
            if (fuel < minFuel) {
                minFuelPos = i
                minFuel = fuel
            }
        }
        println("${minFuelPos} ${posList.fuel(minFuelPos)}")
    }
}