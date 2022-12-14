import java.io.File
import java.math.BigInteger
import java.util.*
import java.util.regex.Pattern

fun main() {
    Day24().solve()
}

class Day24 {

    val input1 = """
inp w
add z w
mod z 2
div w 2
add y w
mod y 2
div w 2
add x w
mod x 2
div w 2
mod w 2
    """.trimIndent()

    enum class Register {
        W, X, Y, Z
    }

    enum class Command(val numOfParameters: Int, val infix: String) {
        INP(1, "") {
            override fun apply(a: Long, b: Long): Long {
                assert(false)
                return 0
            }
        },
        ADD(2, "+") {
            override fun apply(a: Long, b: Long): Long {
                return Math.addExact(a, b)
            }
        },
        MUL(2, "*") {
            override fun apply(a: Long, b: Long): Long {
                return Math.multiplyExact(a, b)
            }
        },
        DIV(2, "/") {
            override fun apply(a: Long, b: Long): Long {
                return a / b
            }
        },
        MOD(2, "%") {
            override fun apply(a: Long, b: Long): Long {
                return a % b
            }
        },
        EQL(2, "==") {
            override fun apply(a: Long, b: Long): Long {
                return if (a == b) 1 else 0
            }
        };
        abstract fun apply(a: Long, b: Long): Long
        fun isBinOp(): Boolean {
            return numOfParameters == 2
        }
    }

    class RawParameter(s: String) {
        val number: Long?
        val register: Register?
        init {
            if (s[0].isDigit() || s[0] == '-') {
                number = s.toLong()
                register = null
            } else {
                number = null
                register = Register.valueOf(s.uppercase(Locale.getDefault()))
            }
            assert(number != null || register != null)
        }
        override fun toString(): String {
            return register?.name ?: number.toString()
        }
    }

    data class Parameter(
        val number: Long? = null,
        val instruction: Instruction? = null
    ) {
        companion object {
            fun number(n: Long): Parameter {
                return Parameter(n)
            }
            fun instruction(i: Instruction): Parameter {
                return Parameter(null, i)
            }
        }

        fun isNumber() = number != null
        fun isInstruction() = instruction != null

        override fun toString(): String {
            if (number != null) {
                return "$number"
            }
            return "I#${instruction!!.index}"
        }

        fun resolve(inputs: BigInteger, results: MutableMap<Int, Long>): Long {
            if (isInstruction()) {
                return instruction!!.resolve(inputs, results)
            }
            return number!!
        }
    }

    class Computer {
        val registers = mutableMapOf(
            Register.W to Parameter.number(0),
            Register.X to Parameter.number(0),
            Register.Y to Parameter.number(0),
            Register.Z to Parameter.number(0)
        )
        var nextInput = 0L
        var nextInstructionIndex = 0
        val instructions = mutableListOf<Instruction>()
        fun resolveParameter(p: RawParameter): Parameter {
            if (p.number != null) {
                return Parameter.number(p.number)
            }
            return registers[p.register]!!
        }
        fun applyInstruction(ri: RawInstruction) {
            val register = ri.getResultRegister()
            val parameters =
                if (ri.command == Command.INP) {
                    listOf(Parameter(nextInput++))
                } else {
                    ri.parameters.map{resolveParameter(it)}
                }
            val i = Instruction(ri.command, parameters, nextInstructionIndex++, ri.toString())
            instructions.add(i)
            registers[register] = i.toParameter()
        }
        fun debug(inputs: BigInteger) {
            val results = mutableMapOf<Int, Long>()
            instructions.forEach{it.debug(inputs, results)}
            println("${results.keys.filter { it % 18 == 17 }.map{results[it]}}")
        }
    }

    class RawInstructionBuilder(s: String) {
        val command = Command.valueOf(s.uppercase(Locale.getDefault()))
        var parameters = mutableListOf<RawParameter>()
        fun hasEnoughParameters(): Boolean {
            return command.numOfParameters == parameters.size
        }
        fun addParameter(s: String): Boolean {
            parameters.add(RawParameter(s))
            return hasEnoughParameters()
        }
        fun build(): RawInstruction {
            return RawInstruction(command, parameters)
        }
    }

    data class RawInstruction(val command: Command, val parameters: List<RawParameter>) {
        fun getResultRegister(): Register {
            return parameters[0].register!!
        }
        override fun toString(): String {
            return "${command.name} ${parameters.map{it.toString()}.joinToString(" ")}".lowercase(Locale.getDefault())
        }
    }

    class Instruction(val command: Command, val parameters: List<Parameter>, val index: Int, val rawString: String) {
        init {
            assert(parameters.size == command.numOfParameters)
            assert(command != Command.DIV || (parameters[1].number == 26L || parameters[1].number == 1L))
            assert(command != Command.MOD || parameters[1].number == 26L)
        }
        init {
            println("$this")
        }
        override fun toString(): String {
            val rawPrefix = "${rawString.padEnd(9, ' ')}"
            val instruction =
                if (command == Command.INP) {
                    "I#$index[i#${parameters[0]}]"
                } else {
                    "I#$index[${parameters[0]} ${command.infix} ${parameters[1]}]"
                }
            return "$rawPrefix | $instruction"
        }
//        fun isIdentity(): Boolean {
//            when (command) {
//                Command.ADD ->
//                    return parameters[1].number == 0
//                Command.DIV ->
//                    return parameters[1].number == 1
//                Command.MUL ->
//                    return parameters[1].number == 1
//                else ->
//                    return false
//            }
//        }
//        fun isZero(): Boolean {
//            return command == Command.MUL && parameters[1].number == 0
//        }
        fun toParameter(): Parameter {
//            if (command == Command.INP && parameters.all { it.isNumber() }) {
//                val a = parameters[0].number!!
//                val b = parameters[1].number!!
//                return Parameter.number(command.apply(a, b))
//            }
            return Parameter.instruction(this)
        }

        fun resolve(inputs: BigInteger, results: MutableMap<Int, Long>): Long {
            if (results.containsKey(index)) {
                return results[index]!!
            }
            val result =
                if (command == Command.INP) {
                    inputs.toString().get(parameters[0].number!!.toInt()).digitToInt().toLong()
                } else {
                    command.apply(parameters[0].resolve(inputs, results), parameters[1].resolve(inputs, results))
                }
            results[index] = result
//            assert(result >= 0)
            return result
        }

        fun debug(inputs: BigInteger, results: MutableMap<Int, Long>) {
            val prefix = if (index % 18 == 17) {"!!! "} else {""}
            val result = resolve(inputs, results)
            val debugStr =
                if (command == Command.INP) {
                    "<- $result"
                } else {
                    val a = parameters[0].resolve(inputs, results)
                    val b = parameters[1].resolve(inputs, results)
                    val op = command.infix
                    "$a $op $b = $result"
                }
            println("$prefix$this: $debugStr")
        }
    }

    // TODO: endless recursion?
    // TODO: pass stringbuilder

    data class P(val d: Long, val a: Long, val b: Long) {
        companion object {
            fun toPs(f: File): List<P> {
                val s = Scanner(f)
                s.useDelimiter("inp w")
//                val pattern = Pattern.compile(".*?div z (\\d+).*?add x (\\d+).*?add y w.*?add y (\\d+).*")
                val pattern = Pattern.compile("div z (\\d+).*add x ([-\\d]+).*add y w.*add y ([-\\d]+)", Pattern.DOTALL)
//                val pattern = Pattern.compile("div z (\\d+)[.\\n]*add x (\\d+)[.\\n]*add y w[.\\n]*add y (\\d+)")
                val r = mutableListOf<P>()
                while (s.hasNext()) {
                    val next = s.next()
                    if (next.isEmpty()) {
                        continue
                    }
                    val m = pattern.matcher(next)
                    val found = m.find()
                    val d = m.group(1).toLong()
                    val a = m.group(2).toLong()
                    val b = m.group(3).toLong()
                    r.add(P(d, a, b))
                }
                return r
            }
        }
    }

    fun applyInput(z: Long, w: Int, p: P): Long {
        assert(z>=0L)
        assert(w in 1..9)
        val wl = w.toLong()
        val x = if (wl != ((z%26L)+p.a)) 1L else 0L
        // FIXME: valamit elszurtam itt
        return (z/p.d)*(x*25L+1)+(wl+p.b)*x
    }

    fun solve(ps: List<P>): BigInteger {
        var nextZs = mutableMapOf<Long, BigInteger>(0L to BigInteger.ZERO)
        val b = (0..10).map{BigInteger(it.toString())}

        for (p in ps) {
            val currentZs = nextZs.toMap()
            println("${currentZs.size}")
            nextZs = mutableMapOf()
            for (z in currentZs) {
                for (w in 1..9) {
                    val z1 = applyInput(z.key, w, p)
                    val v = z.value.times(b[10]).add(b[w])
                    if (!nextZs.containsKey(z1) || nextZs[z1]!! > v) {
                        nextZs[z1] = v
                    }
                }
            }
        }
        return nextZs[0]!!
    }

    fun solve() {
        val f = File("/home/janos/Downloads/aoc24.in1")
        val s = Scanner(f)
//        val s = Scanner(input1)
        s.useDelimiter("\\s+")
        val computer = Computer()
        while (s.hasNext()) {
            val commandStr = s.next()
            val rawInstructionBuilder = RawInstructionBuilder(commandStr)
            while (!rawInstructionBuilder.hasEnoughParameters()) {
                val paramStr = s.next()
                rawInstructionBuilder.addParameter(paramStr)
            }
            computer.applyInstruction(rawInstructionBuilder.build())
        }
//        println("e")
        println("${computer.registers[Register.Z]}")
        val ps = P.toPs(f)
        val r = solve(ps)
        computer.debug(r)
        println("${ps}")
        println("$r")
    }
}