package `2021`

import java.io.File
import java.math.BigInteger
import java.util.*

fun main() {
    Day16().solve()
}

fun Char.toBin(): List<Int> {
    return toString().toInt(16).toString(2).map { it.toString().toInt() }
        .let{MutableList(4-it.size){0}.apply{addAll(it)}}
}

fun String.toBin(): List<Int> {
    return flatMap {it.toBin()}
}

fun List<Int>.toDec(indices: IntRange): Int {
    return slice(indices).joinToString("") { it.toString() }.toInt(2)
}



class Message(val bin: List<Int>, val start: Int) {
    constructor(s: String): this(s.toBin(), 0)
    val version = bin.toDec(start..start + 2)
    val type = bin.toDec(start + 3..start + 5)
    var arguments = mutableListOf<Message>()
    var number: BigInteger? = null
    var length = 0
    val nextStart get() = start + length
    init {
        if (type == 4) {
            var n = BigInteger.ZERO
            val l = bin.slice(start + 6 until bin.size).windowed(5,5).indexOfFirst { it[0] == 0 } + 1
            length = l * 5 + 6
            val big16 = BigInteger("16")
            bin.slice(start+6 until start+length).windowed(5,5).map{
                n = n.multiply(big16).add(BigInteger(it.toDec(1..4).toString()))
            }
            number = n
        } else {
            if (bin[start+6] == 0) {
                length = bin.toDec(start+7..start+21) + 7 + 15
                var nextArgStart = start + 22
                while (nextStart - nextArgStart > 5) {
                    val nextArg = Message(bin, nextArgStart)
                    arguments.add(nextArg)
                    nextArgStart = nextArg.nextStart
                }
            } else {
                val argNum = bin.toDec(start+7..start+17)
                var nextArgStart = start + 18
                for (i in 1..argNum) {
                    val nextArg = Message(bin, nextArgStart)
                    arguments.add(nextArg)
                    nextArgStart = nextArg.nextStart
                }
                length = arguments.sumOf { it.length } + 18
            }
        }
    }
    fun versionSum(): Int {
        return arguments.sumOf{it.versionSum()} + version
    }
    fun eval(): BigInteger {
        val args = arguments.map{it.eval()}
        return when (type) {
            0 -> args.fold(BigInteger.ZERO){ sum, it -> sum.add(it) }
            1 -> args.fold(BigInteger.ONE){ prod, it -> prod.multiply(it) }
            2 -> args.fold(args[0]) { min, it -> if (min.compareTo(it) > 0) it else min}
            3 -> args.fold(args[0]) { max, it -> if (max.compareTo(it) < 0) it else max}
            4 -> number!!
            5 -> if (args.size>1 && args[0].compareTo(args[1]) > 0) BigInteger.ONE else BigInteger.ZERO
            6 -> if (args.size>1 && args[0].compareTo(args[1]) < 0) BigInteger.ONE else BigInteger.ZERO
            else -> if (args.size>1 && args[0].compareTo(args[1]) == 0) BigInteger.ONE else BigInteger.ZERO
        }
    }
}

class Day16 {
    val input = """D2FE28""".trimIndent()
    val input0 = """38006F45291200""".trimIndent()
    val input1 = """EE00D40C823060""".trimIndent()
    val input2 = """8A004A801A8002F478""".trimIndent()
    val input3 = """620080001611562C8802118E34""".trimIndent()
    val input4 = """C0015000016115A2E0802F182340""".trimIndent()
    val input5 = """A0016C880162017C3686B18A3D4780""".trimIndent()
    val input6 = """C200B40A82""".trimIndent()
    val input7 = """04005AC33890""".trimIndent()
    val input8 = """880086C3E88112""".trimIndent()
    val input9 = """CE00C43D881120""".trimIndent()
    val input10 = """D8005AC2A8F0""".trimIndent()
    val input11 = """F600BC2D8F""".trimIndent()
    val input12 = """9C005AC2F8F0""".trimIndent()
    val input13 = """9C0141080250320F1802104A08""".trimIndent()



    fun solve() {
        val f = File("src/2021/inputs/day16.in")
        val s = Scanner(f)
//        val s = Scanner(input)
//        val m = Message(input13)
        val m = Message(s.nextLine())
        println("${m.versionSum()} ${m.eval()}")
    }
}