package `2022`

import java.io.File
import java.math.BigInteger
import java.util.*

fun main() {
    Day11().solve()
}

class Day11 {

    val input1 = """
        Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1
    """.trimIndent()

    val input2 = """
    """.trimIndent()


    enum class Op {
        ADD, MUL, SQUARE
    }

    class Operation(val op: Op, val other: BigInteger) {
        fun apply(item: BigInteger): BigInteger {
            if (op == Op.MUL) {
                return item.multiply(other)
            } else if (op == Op.SQUARE) {
                return item.multiply(item)
            }
            return item.add(other)
        }
    }

    class Test(val n: BigInteger) {
        fun test(k: BigInteger): Boolean {
            return k.mod(n) == BigInteger.ZERO
        }
    }

    data class Throw(val monkey: Int, val items: MutableList<BigInteger> = mutableListOf()) {
        fun add(item: BigInteger) {
            items.add(item)
        }
    }

    class Monkey(val test: Test, val operation: Operation, val toMonkeys: List<Int>) {
        val items = mutableListOf<BigInteger>()
        var inspectNum = 0L
        fun add(stuff: List<BigInteger>) {
            items.addAll(stuff)
        }

        fun throws(mod: BigInteger? = null): List<Throw> {
            val r = toMonkeys.map{Throw(it)}
            items.forEach {
                val w =
                    if (mod == null) {
                        operation.apply(it).div(BigInteger("3"))
                    } else {
                        operation.apply(it).mod(mod)
                    }
                ++inspectNum
                if (test.test(w)) {
                    r[0].add(w)
                } else {
                    r[1].add(w)
                }
            }
            items.removeAll{true}
            return r
        }
    }

    fun solve() {
        val f = File("src/2022/inputs/day11.in")
        val s = Scanner(f)
//                val s = Scanner(input1)

        val monkeys = mutableListOf<Monkey>()
        var mod = BigInteger.ONE
        while (s.hasNextLine()) {
            var line = s.nextLine().trim()
            if (line.isEmpty()) {
                continue
            }
            if (!line.startsWith("Monkey")) {
                throw RuntimeException()
            }
            line = s.nextLine().trim()
            if (!line.startsWith("Starting items: ")) {
                throw RuntimeException()
            }
            var rest = line.substring(16)
            val items = rest.split(", ").map{it.toBigInteger()}
            line = s.nextLine().trim()
            if (!line.startsWith("Operation: new = old ")) {
                throw RuntimeException()
            }
            val opChar = line[21]
            rest = line.substring(23)
            val operation =
                if (rest == "old") {
                    Operation(Op.SQUARE, BigInteger.ONE)
                } else if (opChar == '*') {
                    Operation(Op.MUL, rest.toBigInteger())
                } else if (opChar == '+') {
                    Operation(Op.ADD, rest.toBigInteger())
                } else {
                    throw RuntimeException()
                }
            line = s.nextLine().trim()
            if (!line.startsWith("Test: divisible by ")) {
                throw RuntimeException()
            }
            rest = line.substring(19)
            val d = rest.toBigInteger()
            mod = mod.times(d)
            line = s.nextLine().trim()
            if (!line.startsWith("If true: throw to monkey ")) {
                throw RuntimeException()
            }
            rest = line.substring(25)
            val m1 = rest.toInt()
            line = s.nextLine().trim()
            if (!line.startsWith("If false: throw to monkey ")) {
                throw RuntimeException()
            }
            rest = line.substring(26)
            val m2 = rest.toInt()

            val monkey = Monkey(Test(d), operation, listOf(m1, m2))
            monkey.add(items)

            monkeys.add(monkey)

//            println("")
        }

//        for (i in 1..20) {
//            for (m in monkeys) {
//                val t = m.throws()
//                t.map { monkeys[it.monkey].add(it.items) }
//            }
//        }
//
//        // [23, 63, 198, 241, 256, 385, 421, 433]
//        val sortedInspectNums = monkeys.map { it.inspectNum }.sorted()

        for (i in 1..10000) {

            for (m in monkeys) {
                val t = m.throws(mod)
                t.map { monkeys[it.monkey].add(it.items) }
            }
        }

        // [23, 63, 198, 241, 256, 385, 421, 433]
        val sortedInspectNums = monkeys.map { it.inspectNum }.sortedDescending()

        println("${sortedInspectNums[0] * sortedInspectNums[1]}")
    }
}
