package `2021`

import java.io.File
import java.lang.Math.abs
import java.util.*
import kotlin.math.absoluteValue

fun main() {
    Day19().solve()
}

class Day19 {

    companion object {
        val allRoll90s: List<Roll90> = allRoll90s()

        fun allRoll90s(): List<Roll90> {
            val newPosPermutations = mutableListOf<List<Int>>()
            for (i in 0..2) {
                for (j in 0..2) {
                    for (k in 0..2) {
                        if (i!=j && i!=k && j!=k) {
                            newPosPermutations.add(listOf(i, j, k))
                        }
                    }
                }
            }
            val signsPermutations = mutableListOf<List<Int>>()
            for (i in listOf(-1, 1)) {
                for (j in listOf(-1, 1)) {
                    for (k in listOf(-1, 1)) {
                        signsPermutations.add(listOf(i, j, k))
                    }
                }
            }
            val allRoll90s = mutableListOf<Roll90>()
            for (newPos in newPosPermutations) {
                for (signs in signsPermutations) {
                    allRoll90s.add(Roll90(newPos, signs))
                }
            }
            return allRoll90s
        }
    }

    class Point(val c: List<Long>) {

        operator fun get(i: Int): Long {
            return c[i]
        }

        fun asList(): List<Long> {
            return c
        }

        fun diff(other: Point): Point {
            return Point(c.zip(other.c).map { it.first - it.second })
        }

        override operator fun equals(other: Any?): Boolean =
            (other is Point) && c.zip(other.c).all { it.first == it.second}

        override fun hashCode(): Int {
            return c.fold(0) {h, it -> h*1000+it.toInt()}
        }

        override fun toString(): String {
            return "[" + c.map { it.toString() }.joinToString (",") + "]"
        }

        operator fun unaryMinus(): Point {
            return Point(c.map{-it})
        }
    }

    abstract class Transformation {
        abstract fun applyTo(p: Point): Point
        abstract fun inverse(): Transformation
    }

    class Move(val c: List<Long>): Transformation() {
        override fun applyTo(p: Point): Point {
            return p.asList().zip(c).map{it.first + it.second}.let{Point(it)}
        }
        override fun inverse(): Transformation {
            return c.map{-it}.let{Move(it)}
        }
    }

    class CompositeTransformation(val ts: List<Transformation>): Transformation()  {
        override fun applyTo(p: Point): Point {
            return ts.fold(p){p1, it -> it.applyTo(p1)}
        }
        override fun inverse(): Transformation {
            return CompositeTransformation(ts.asReversed().map { it.inverse() })
        }
    }

    class Roll90(val newPos: List<Int>, val signs: List<Int>): Transformation() {
        override fun applyTo(p: Point): Point {
            val c = p.asList().toMutableList()
            for (i in newPos.indices) {
                c[newPos[i]] = signs[newPos[i]] * p.asList()[i]
            }
            return Point(c)
        }
        override fun inverse(): Transformation {
            val newPos1 = mutableListOf(0, 0, 0)
            val signs1 = mutableListOf(0, 0, 0)
            for (i in 0..2) {
                newPos1[newPos[i]] = i
                signs1[i] = signs[newPos[i]]
            }
            return Roll90(newPos1, signs1)
        }
    }

    class Scanner(val name: String, val beacons: List<Point>) {
        override fun toString(): String {
            return name
        }

        val pointViews: Map<Point, List<Point>>
        val pointHashes: Map<Point, List<Long>>

        init {
            val pv = mutableMapOf<Point, List<Point>>()
            val ph = mutableMapOf<Point, List<Long>>()
            for (p in beacons) {
                pv[p] = beacons.filter { it !== p }.map{it.diff(p)}
                ph[p] = beacons.filter { it !== p }.map{it.diff(p).roll90Hash()}
            }
            pointViews = pv
            pointHashes = ph
        }

        fun overlap(other: Scanner): List<Transformation> {
            for (pv0 in pointViews) {
                val p0 = pv0.key
                val ps0 = pv0.value
                for (pv1 in other.pointViews) {
                    val p1 = pv1.key
                    val ps1 = pv1.value
                    // TODO: check hashes
                    val commonHashes = pointHashes[p0]!!.intersect(other.pointHashes[p1]!!)
                    if (commonHashes.size < 11) {
                        continue
                    }
                    for (r90 in allRoll90s) {
                        val ps090 = ps0.map{r90.applyTo(it)}
                        val common = ps1.intersect(ps090).size + 1
                        if (common > 11) {
                            val t = CompositeTransformation(listOf(Move((-p0).asList()), r90, Move(p1.asList())))
                            val common1 = beacons.map { t.applyTo(it) }.intersect(other.beacons)
//                            println("${common1.size} $common1")
                            // TODO: more than 1 mapping
                            // TODO: check if the rest is out of range for the other
                            return listOf(t)
                        }
                    }
                }
            }
            return listOf()
        }
    }

    class ScannerBuilder {
        val beacons = mutableListOf<Point>()
        var name = ""
        fun add(line: String): Boolean {
            if (line.startsWith("---")) {
                name = line
                return true
            }
            if (line.isEmpty()) {
                return false
            }
            beacons.add(line.toPoint())
            return true
        }
        fun build(): Scanner? {
            if (name == "") {
                return null
            }
            return Scanner(name, beacons)
        }
    }

    val input = """
--- scanner 0 ---
404,-588,-901
528,-643,409
-838,591,734
390,-675,-793
-537,-823,-458
-485,-357,347
-345,-311,381
-661,-816,-575
-876,649,763
-618,-824,-621
553,345,-567
474,580,667
-447,-329,318
-584,868,-557
544,-627,-890
564,392,-477
455,729,728
-892,524,684
-689,845,-530
423,-701,434
7,-33,-71
630,319,-379
443,580,662
-789,900,-551
459,-707,401

--- scanner 1 ---
686,422,578
605,423,415
515,917,-361
-336,658,858
95,138,22
-476,619,847
-340,-569,-846
567,-361,727
-460,603,-452
669,-402,600
729,430,532
-500,-761,534
-322,571,750
-466,-666,-811
-429,-592,574
-355,545,-477
703,-491,-529
-328,-685,520
413,935,-424
-391,539,-444
586,-435,557
-364,-763,-893
807,-499,-711
755,-354,-619
553,889,-390

--- scanner 2 ---
649,640,665
682,-795,504
-784,533,-524
-644,584,-595
-588,-843,648
-30,6,44
-674,560,763
500,723,-460
609,671,-379
-555,-800,653
-675,-892,-343
697,-426,-610
578,704,681
493,664,-388
-671,-858,530
-667,343,800
571,-461,-707
-138,-166,112
-889,563,-600
646,-828,498
640,759,510
-630,509,768
-681,-892,-333
673,-379,-804
-742,-814,-386
577,-820,562

--- scanner 3 ---
-589,542,597
605,-692,669
-500,565,-823
-660,373,557
-458,-679,-417
-488,449,543
-626,468,-788
338,-750,-386
528,-832,-391
562,-778,733
-938,-730,414
543,643,-506
-524,371,-870
407,773,750
-104,29,83
378,-903,-323
-778,-728,485
426,699,580
-438,-605,-362
-469,-447,-387
509,732,623
647,635,-688
-868,-804,481
614,-800,639
595,780,-596

--- scanner 4 ---
727,592,562
-293,-554,779
441,611,-461
-714,465,-776
-743,427,-804
-660,-479,-426
832,-632,460
927,-485,-438
408,393,-506
466,436,-512
110,16,151
-258,-428,682
-393,719,612
-211,-452,876
808,-476,-593
-575,615,604
-485,667,467
-680,325,-822
-627,-443,-432
872,-547,-609
833,512,582
807,604,487
839,-516,451
891,-625,532
-652,-548,-490
30,-46,-14
    """.trimIndent()

    val output = """
-892,524,684
-876,649,763
-838,591,734
-789,900,-551
-739,-1745,668
-706,-3180,-659
-697,-3072,-689
-689,845,-530
-687,-1600,576
-661,-816,-575
-654,-3158,-753
-635,-1737,486
-631,-672,1502
-624,-1620,1868
-620,-3212,371
-618,-824,-621
-612,-1695,1788
-601,-1648,-643
-584,868,-557
-537,-823,-458
-532,-1715,1894
-518,-1681,-600
-499,-1607,-770
-485,-357,347
-470,-3283,303
-456,-621,1527
-447,-329,318
-430,-3130,366
-413,-627,1469
-345,-311,381
-36,-1284,1171
-27,-1108,-65
7,-33,-71
12,-2351,-103
26,-1119,1091
346,-2985,342
366,-3059,397
377,-2827,367
390,-675,-793
396,-1931,-563
404,-588,-901
408,-1815,803
423,-701,434
432,-2009,850
443,580,662
455,729,728
456,-540,1869
459,-707,401
465,-695,1988
474,580,667
496,-1584,1900
497,-1838,-617
527,-524,1933
528,-643,409
534,-1912,768
544,-627,-890
553,345,-567
564,392,-477
568,-2007,-577
605,-1665,1952
612,-1593,1893
630,319,-379
686,-3108,-505
776,-3184,-501
846,-3110,-434
1135,-1161,1235
1243,-1093,1063
1660,-552,429
1693,-557,386
1735,-437,1738
1749,-1800,1813
1772,-405,1572
1776,-675,371
1779,-442,1789
1780,-1548,337
1786,-1538,337
1847,-1591,415
1889,-1729,1762
1994,-1805,1792""".trimIndent()

    fun solve() {
        val f = File("src/2021/inputs/day19.in")
        val s = Scanner(f)

//        val s = Scanner(input)
        val scanners = mutableListOf<Scanner>()

        while (s.hasNextLine()) {
            val sb = ScannerBuilder()
            while (s.hasNextLine() && sb.add(s.nextLine())) {}
            sb.build()?.let{scanners.add(it)}
        }

        val transformTo0 = mutableMapOf<Int, Transformation>(0 to Move(listOf(0, 0, 0)))
        val beacons = scanners[0].beacons.toMutableSet()

        loop@ while (transformTo0.size < scanners.size) {
            for (i in scanners.indices.subtract(transformTo0.keys)) {
                for (j in transformTo0.keys) {
                    val t = scanners[i].overlap(scanners[j])
                    if (t.size > 0) {
                        val t1 = CompositeTransformation(listOf(t[0], transformTo0[j]!!))
                        beacons.addAll(scanners[i].beacons.map{
//                            println("${it} ${t1.applyTo(it)}")
                            t1.applyTo(it)
                        })
                        transformTo0[i] = t1
                        println("${transformTo0.size}/${scanners.size}")
                        continue@loop
                    }
                }
            }
        }

        val scannerCenters = transformTo0.values.map { it.applyTo(Point(listOf(0,0,0))) }

        var maxDist = 0
        for (i in scannerCenters) {
            for (j in scannerCenters) {
                val dist = i.diff(j).asList().fold(0){d, it -> d + abs(it.toInt())}
                if (dist > maxDist) {
                    maxDist = dist
                }
            }
        }

//        val so = Scanner(output)
//        val outBeacons = mutableSetOf<Point>()
//        while (so.hasNextLine()) {
//            val soStr = so.nextLine()
//            if (soStr.isNotEmpty()) {
//                outBeacons.add(soStr.toPoint())
//            }
//        }
//
//        val bd1 = outBeacons.subtract(beacons)
//        val bd2 = beacons.subtract(outBeacons)
        println("e ${beacons.size} $maxDist")
    }
}

fun String.toPoint(): Day19.Point {
    return Day19.Point(split(",").map { it.toLong() })
}

fun Day19.Point.canRoll90To(p: Day19.Point): Boolean {
    return roll90Hash() == p.roll90Hash()
}

fun Day19.Point.roll90Hash(): Long {
    return c.map { it.absoluteValue }.sorted().fold(0){acc, it -> acc * 2003 + it}
}

fun Day19.Point.moveTo(other: Day19.Point): Day19.Move {
    return Day19.Move(other.diff(this).asList())
}