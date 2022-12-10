package `2021`

import java.io.File
import java.math.BigInteger
import java.util.*

fun main() {
    Day18().solve()
}

class Day18 {
    val input1 = """
[1,1]
[2,2]
[3,3]
[4,4]
    """.trimMargin()
    val output1 = """
[[[[1,1],[2,2]],[3,3]],[4,4]]
    """.trimMargin()
    val input2 = """
[1,1]
[2,2]
[3,3]
[4,4]
[5,5]
    """.trimMargin()
    val output2 = """
[[[[3,0],[5,3]],[4,4]],[5,5]]
    """.trimMargin()

    val input3 = """[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
[[[5,[2,8]],4],[5,[[9,9],0]]]
[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
[[[[5,4],[7,7]],8],[[8,3],8]]
[[9,3],[[9,9],[6,[4,9]]]]
[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]""".trimIndent()

    val input4 = """
[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
[7,[5,[[3,8],[1,4]]]]
[[2,[2,2]],[8,[8,1]]]
[2,9]
[1,[[[9,3],9],[[9,0],[0,7]]]]
[[[5,[7,4]],7],1]
[[[[4,2],2],6],[8,7]]
    """.trimMargin()

    class SnailInt(val input: String, val start: Int, var parent: SnailInt?) {
        var number: Int?
        var leftChild: SnailInt?
        var rightChild: SnailInt?
        val length: Int
        constructor(): this("")
        constructor(input: String): this(input, 0, null)
        init {
            if (input.length>start && input[start].isDigit()) {
                val numberStr = input.substring(start).takeWhile { it.isDigit() }
                number = numberStr.toInt()
                leftChild = null
                rightChild = null
                length = numberStr.length
            } else if (input.length>start && input[start] == '[') {
                number = null
                leftChild = SnailInt(input, start+1, this)
                rightChild = SnailInt(input, start + leftChild!!.length + 2, this)
                length = leftChild!!.length + rightChild!!.length + 3
            } else {
                number = null
                leftChild = null
                rightChild = null
                length = 0
            }
        }
        override fun toString(): String {
            if (leftChild != null || rightChild != null) {
                return "[$leftChild,$rightChild]" + if (depth()>=4) "!" else ""
            }
            return number.toString() + if (number != null && number!! > 9) "!" else ""
        }
        operator fun plus(other: SnailInt): SnailInt {
            return SnailInt().also {
                it.leftChild = this
                this.parent = it
                it.rightChild = other
                other.parent = it
            }.apply {reduce()}
        }
        fun depth(): Int {
            var p = parent
            var r = 0
            while (p != null) {
                ++r
                p = p.parent
            }
            return r
        }
        fun incNumber(n: Int) {
            number = number?.plus(n)
        }
        fun explode(): Boolean {
            var i: SnailInt? = firstNum()
            while (i != null) {
                val ip = i.parent!!
                if (ip.depth() >= 4) {
                    val iprev = ip.leftChild!!.prevNum()
                    val inext = ip.rightChild!!.nextNum()
                    val lv = ip.leftChild!!.number!!
                    val rv = ip.rightChild!!.number!!
                    iprev?.incNumber(lv)
                    inext?.incNumber(rv)
                    ip.leftChild = null
                    ip.rightChild = null
                    ip.number = 0
                    return true
                }
                i = i.nextNum()
            }
            return false
        }
        fun split(): Boolean {
            var i: SnailInt? = firstNum()
            while (i != null) {
                if (i.number!! >= 10) {
                    i.leftChild =
                        SnailInt().also {
                            it.number = i!!.number!!/2
                            it.parent = i
                        }
                    i.rightChild =
                        SnailInt().also {
                            it.number = (i!!.number!!+1)/2
                            it.parent = i
                        }
                    i.number = null
                    return true
                }
                i = i.nextNum()
            }
            return false
        }
        tailrec fun reduce(): Boolean {
            if (explode()) {
                return reduce()
            } else if (split()) {
                return reduce()
            }
            return false
        }
        fun firstNum(): SnailInt {
            return if (leftChild != null) leftChild!!.firstNum() else this
        }
        fun lastNum() : SnailInt {
            return if (rightChild != null) rightChild!!.lastNum() else this
        }
        fun prevNum(): SnailInt? {
            var p = this
            while (p.parent?.leftChild == p) {
                p = p.parent!!
            }
            if (p.parent?.rightChild == p) {
                return p.parent!!.leftChild!!.lastNum()
            }
            return null
        }
        fun nextNum(): SnailInt? {
            var p = this
            while (p.parent?.rightChild == p) {
                p = p.parent!!
            }
            if (p.parent?.leftChild == p) {
                return p.parent!!.rightChild!!.firstNum()
            }
            return null
        }
        fun magnitude(): BigInteger {
            if (number != null) {
                return BigInteger(number.toString())
            }
            return (leftChild!!.magnitude().times(BigInteger("3"))).plus(
                rightChild!!.magnitude().times(BigInteger("2")))
        }
    }

    fun solve() {
                val f = File("src/2021/inputs/day18.in")
                val s = Scanner(f)
//
//        val s = Scanner(input3)

//        var snailInt = SnailInt(s.nextLine())
//        while (s.hasNextLine()) {
//            snailInt += SnailInt(s.nextLine())
//        }
//
//        println("${snailInt.magnitude()} $snailInt")

        val snailIntStrs = mutableListOf<String>()
        while (s.hasNextLine()) {
            snailIntStrs.add(s.nextLine())
        }
        var maxMagnitude = BigInteger.ZERO
        for (i in snailIntStrs.indices) {
            for (j in snailIntStrs.indices) {
                if (i != j) {
                    val sumMagnitude = SnailInt(snailIntStrs[i]).plus(SnailInt(snailIntStrs[j])).magnitude()
                    if (sumMagnitude.compareTo(maxMagnitude)>0) {
                        maxMagnitude = sumMagnitude
                    }
                }
            }
        }

        println("$maxMagnitude")
    }
}