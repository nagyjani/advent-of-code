package `2022`

import java.io.File
import java.math.BigInteger
import java.util.*

fun main() {
    Day21().solve()
}


class Day21 {

    val input1 = """
        root: pppw + sjmn
        dbpl: 5
        cczh: sllz + lgvd
        zczc: 2
        ptdq: humn - dvpt
        dvpt: 3
        lfqf: 4
        humn: 5
        ljgn: 2
        sjmn: drzm * dbpl
        sllz: 4
        pppw: cczh / lfqf
        lgvd: ljgn * ptdq
        drzm: hmdt - zczc
        hmdt: 32

    """.trimIndent()

    class Message(val list: List<String>) {
        fun resolve(resolved: Map<String, BigInteger>): BigInteger? {
            if (list.size == 1) {
                return list[0].toBigInteger()
            }
            val arg1 = resolved[list[0]]
            if (arg1 == null) {
                return null
            }
            val arg2 = resolved[list[2]]
            if (arg2 == null) {
                return null
            }
            val op = list[1]
            return when (op) {
                "+" -> arg1 + arg2
                "*" -> arg1 * arg2
                "/" -> arg1 / arg2
                else -> arg1 - arg2
            }
        }

        fun resolveBack(result: BigInteger, resolved: Map<String, BigInteger>): List<BigInteger>{
            val r = mutableListOf<BigInteger>()
            val arg1 = resolved[list[0]]
            val arg2 = resolved[list[2]]
            val op = list[1]
            if (arg1 == null && arg2 == null) {
                throw RuntimeException()
            }
            if (arg2 == null) {
                when (op) {
                    "+" ->
                        r.add(result - arg1!!)
                    "*" ->
                        if (result % arg1!! == BigInteger.ZERO) {
                            r.add(result / arg1)
                        }
                    "/" ->
                        throw RuntimeException()
                    else ->
                        r.add(-(result - arg1!!))
                }
            }
            if (arg1 == null) {
                when (op) {
                    "+" ->
                        r.add(result - arg2!!)
                    "*" ->
                        if (result % arg2!! == BigInteger.ZERO) {
                            r.add(result / arg2)
                        }
                    "/" -> {
                        r.add(result * arg2!!)
//                        for (i in 0 until arg2!!.toInt()) {
//                            r.add((result * arg2) + i.toBigInteger())
//                        }
                    }
                    else ->
                        r.add(result + arg2!!)
                }
            }
            return r
        }

        override fun toString(): String {
            return "(" + list.joinToString(" ") + ")"
        }
        // TODO toString()
    }

    fun equals(i: Int, path: List<Message>, resolved: MutableMap<String, BigInteger>, result: BigInteger) {
        if (i == path.size - 1) {
            println(result)
            return
        }
        for (r in path[i].resolveBack(result, resolved)) {
            equals(i+1, path, resolved, r)
        }
    }

    fun solve() {
        val f = File("/home/janos/Downloads/day21.in")
        val s = Scanner(f)
//        val s = Scanner(input1)

        val monkeys = mutableMapOf<String, Message>()
        val resolved = mutableMapOf<String, BigInteger>()

        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            val words = line.split(" ")
            if (!words.isEmpty()) {
                val name = words[0].split(":")[0]
                if (words.size == 2) {
                    monkeys[name] = Message(words.subList(1, 2))
                } else {
                    monkeys[name] = Message(words.subList(1, 4))
                }
            }
        }

        val unresolved0 = monkeys.keys.toMutableSet()
        while (!unresolved0.isEmpty()) {
            val toUnresolve = mutableSetOf<String>()
            for (m in unresolved0) {
                val n = monkeys[m]!!.resolve(resolved)
                if (n != null) {
                    resolved[m] = n
                    toUnresolve.add(m)
                }
            }
            unresolved0.removeAll(toUnresolve)
        }
        println("${resolved["root"]}")

        resolved.clear()

        var updated = true
        val unresolved = monkeys.keys.toMutableSet().also { it.remove("humn") }
        while (updated) {
            updated = false
            val toUnresolve = mutableSetOf<String>()
            for (m in unresolved) {
                val n = monkeys[m]!!.resolve(resolved)
                if (n != null) {
                    resolved[m] = n
                    toUnresolve.add(m)
                    updated = true
                }
            }
            unresolved.removeAll(toUnresolve)
        }

        val l = mutableListOf("humn")
        while (l.last() != "root") {
            val k = monkeys.filter { it.value.list.contains(l.last()) }
            k.forEach{println("${l.last()} ${it.value.list} ${it.value.list.map{resolved[it]}}")}
            if (k.size != 1) {
                println("k: $k")
            }
            l.add(k.toList().first().first)
        }
        println("root ${monkeys["root"]!!.list.map{resolved[it]}}")
        val path = l.reversed().map{monkeys[it]!!}

        println("${monkeys[l.last()]!!.list}")
        println("l: $l")
        println("$path")
        println("${resolved["root"]} ${resolved}")

//        equals(1, path, resolved, 150.toBigInteger())
        equals(1, path, resolved, 26605796414957.toBigInteger())
    }
}

