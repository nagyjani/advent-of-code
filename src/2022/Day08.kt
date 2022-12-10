import java.io.File
import java.lang.Integer.max
import java.util.*

fun main() {
    Day08().solve()
}


class Day08 {

    val input1 = """
        30373
        25512
        65332
        33549
        35390
    """.trimIndent()

    data class Tree(
        val height: Int,
        var maxLeft: Int = -1,
        var maxRight: Int = -1,
        var maxTop: Int = -1,
        var maxBottom: Int = -1) {

        fun visible(): Boolean {
            return height > maxLeft || height > maxRight || height > maxTop || height > maxBottom
        }
    }

    fun scenic(t: Int, x: Int, y: Int, trees: MutableList<Tree>): Int {
        val tree = trees[t]
        val xt = t%y
        val yt = t/y

        // left
        var ld = 0
        for (i in xt-1 downTo 0) {
            ++ld
            val left = trees[x*yt + i]
            if (left.height >= tree.height) {
                break
            }
        }

        // right
        var rd = 0
        for (i in xt+1 until x) {
            ++rd
            val right = trees[x*yt + i]
            if (right.height >= tree.height) {
                break
            }
        }

        //top
        var td = 0
        for (j in yt-1 downTo 0) {
            ++td
            val top = trees[x*j + xt]
            if (top.height >= tree.height) {
                break
            }
        }

        //bottom
        var bd = 0
        for (j in yt+1 until y) {
            ++bd
            val bottom = trees[x*j + xt]
            if (bottom.height >= tree.height) {
                break
            }
        }

        println("$t ${ld * rd * td * bd} $ld * $rd * $td * $bd")
        return ld * rd * td * bd
    }

    fun solve() {
        val f = File("src/2022/inputs/aoc08.in")
        val s = Scanner(f)
//                val s = Scanner(input1)

        val trees = mutableListOf<Tree>()
        var y0 = 0

        while (s.hasNextLine()) {
            val line = s.nextLine().trim()
            if (!line.isEmpty()) {
                trees.addAll(line.map { Tree(it - '0') })
                ++y0
            }
            println("$trees")
        }
        val y = y0
        val x = trees.size/y

        // left
        for (j in 0 until y) {
            for (i in 1 until x) {
                val tree = trees[j*x + i]
                val left = trees[j*x + i-1]
                tree.maxLeft = max(left.height, left.maxLeft)
            }
        }

        // right
        for (j in 0 until y) {
            for (i in x-2 downTo 0) {
                val tree = trees[j*x + i]
                val right = trees[j*x + i+1]
                tree.maxRight = max(right.height, right.maxRight)
            }
        }

        //top
        for (i in 0 until x) {
            for (j in 1 until y) {
                val tree = trees[j*x + i]
                val top = trees[(j-1)*x + i]
                tree.maxTop = max(top.height, top.maxTop)
            }
        }

        //bottom
        for (i in 0 until x) {
            for (j in y-2 downTo 0) {
                val tree = trees[j*x + i]
                val bottom = trees[(j+1)*x + i]
                tree.maxBottom = max(bottom.height, bottom.maxBottom)
            }
        }

        val maxs = trees.indices.maxOf { scenic(it, x, y, trees) }

        println("$maxs $x $y ${trees.sumOf { if (it.visible()) 1L else 0L}}")
    }
}

