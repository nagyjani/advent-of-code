package `2022`

import java.io.File
import java.lang.StringBuilder
import java.util.*

fun main() {
    Day22().solve()
}


class Day22 {

    val input1 = """
        ...#
        .#..
        #...
        ....
...#.......#
........#...
..#....#....
..........#.
        ...#....
        .....#..
        .#......
        ......#.

10R5L5R10L4R5L5
    """.trimIndent()

    val input2 = """
    ........
    ........
    ........
    ........
    ....
    ....
    ....
    ....
........
........
........
........
....
....
....
....

0R1L1R11
    """.trimIndent()

    enum class Dir {
        RIGHT, DOWN, LEFT, UP;
        fun turn(d: String): Dir {
            if (d == "R") {
                return Dir.values()[(ordinal+1)%4]
            }
            return Dir.values()[(ordinal+3)%4]
        }

        fun toChar(): Char {
            return when (this) {
                Dir.LEFT -> '<'
                Dir.UP -> '^'
                Dir.RIGHT -> '>'
                Dir.DOWN -> 'v'
            }
        }
    }


    data class Pos(val b: Int, val x: Int, val y: Int, val d: Dir) {
        fun next(): Pos {
            return when (d) {
                Dir.LEFT -> Pos(b, x-1, y, d)
                Dir.UP -> Pos(b, x, y-1, d)
                Dir.RIGHT -> Pos(b, x+1, y, d)
                Dir.DOWN -> Pos(b, x, y+1, d)
            }
        }
    }
    class Boards {
        var walked = mutableSetOf<Pos>()

        var cube = false

        val boards = mutableListOf<Board>()
        fun addRow(line: String) {
            if (boards.size == 0) {
                boards.add(Board(line))
            } else if (!boards.last().addRow(line)) {
                boards.add(Board(line))
            }
        }

        fun isBlocked(p: Pos): Boolean {
            return boards[p.b].rows[p.y][p.x] == '#'
        }
        fun step(p: Pos): Pos {
            val D = boards[0].rows.size
            val board = boards[p.b]
            var next = p.next()
            if (cube == false) {
                if (next.x >= board.width) {
                    next = Pos(next.b, 0, next.y, p.d)
                } else if (next.x < 0) {
                    next = Pos(next.b, board.width - 1, next.y, p.d)
                } else if (next.y >= board.rows.size) {
                    for (nextB in (p.b + 1..p.b + boards.size).map { it % boards.size }) {
                        val nextBoard = boards[nextB]
                        val nextX = p.x + board.firstColumn - nextBoard.firstColumn
                        if (nextX < nextBoard.width && nextX >= 0) {
                            next = Pos(nextB, nextX, 0, p.d)
                            break
                        }
                    }
                } else if (next.y < 0) {
                    for (nextB in (p.b + boards.size - 1 downTo p.b).map { it % boards.size }) {
                        val nextBoard = boards[nextB]
                        val nextX = p.x + board.firstColumn - nextBoard.firstColumn
                        if (nextX < nextBoard.width && nextX >= 0) {
                            next = Pos(nextB, nextX, nextBoard.rows.size - 1, p.d)
                            break
                        }
                    }
                }
            } else {
                if (next.x >= board.width) {
                    next = when (p.b) {
                        0 -> {
                            val nextB = 2
                            val nextD = Dir.LEFT
                            val nextX = 2*D-1
                            val nextY = D-1 - p.y
                            Pos(nextB, nextX, nextY, nextD)
                        }

                        1 -> {
                            val nextB = 0
                            val nextD = Dir.UP
                            val nextX = D+p.y
                            val nextY = D-1
                            Pos(nextB, nextX, nextY, nextD)
                        }

                        2 -> {
                            val nextB = 0
                            val nextD = Dir.LEFT
                            val nextX = 2*D-1
                            val nextY = D-1 - p.y
                            Pos(nextB, nextX, nextY, nextD)
                        }

                        else -> {
                            val nextB = 2
                            val nextX = p.y + D
                            val nextY = D-1
                            val nextD = Dir.UP
                            Pos(nextB, nextX, nextY, nextD)
                        }
                    }
                } else if (next.x < 0) {
                    next = when (p.b) {
                        0 -> {
                            val nextB = 2
                            val nextX = 0
                            val nextY = D-1-p.y
                            val nextD = Dir.RIGHT
                            Pos(nextB, nextX, nextY, nextD)
                        }

                        1 -> {
                            val nextB = 2
                            val nextX = p.y
                            val nextY = 0
                            val nextD = Dir.DOWN
                            Pos(nextB, nextX, nextY, nextD)
                        }

                        2 -> {
                            val nextB = 0
                            val nextX = 0
                            val nextY = D-1-p.y
                            val nextD = Dir.RIGHT
                            Pos(nextB, nextX, nextY, nextD)
                        }

                        else -> {
                            val nextB = 0
                            val nextX = p.y
                            val nextY = 0
                            val nextD = Dir.DOWN
                            Pos(nextB, nextX, nextY, nextD)
                        }
                    }
                } else if (next.y >= board.rows.size) {
                    next = when (p.b) {
                        0 -> if (p.x<D) {
                                val nextB = 1
                                val nextX = p.x
                                val nextY = 0
                                val nextD = Dir.DOWN
                                Pos(nextB, nextX, nextY, nextD)
                            } else {
                                val nextB = 1
                                val nextD = Dir.LEFT
                                val nextX = D-1
                                val nextY = p.x-D
                                Pos(nextB, nextX, nextY, nextD)
                            }
                        1 -> {
                            val nextB = 2
                            val nextD = Dir.DOWN
                            val nextX = p.x+D
                            val nextY = 0
                            Pos(nextB, nextX, nextY, nextD)
                        }
                        2 -> if (p.x<D) {
                            val nextB = 3
                            val nextD = Dir.DOWN
                            val nextX = p.x
                            val nextY = 0
                            Pos(nextB, nextX, nextY, nextD)
                        } else {
                            val nextB = 3
                            val nextD = Dir.LEFT
                            val nextX = D-1
                            val nextY = p.x-D
                            Pos(nextB, nextX, nextY, nextD)
                        }
                        else -> {
                            val nextB = 0
                            val nextX = D + p.x
                            val nextY = 0
                            val nextD = Dir.DOWN
                            Pos(nextB, nextX, nextY, nextD)
                        }
                    }
                } else if (next.y < 0) {
                    next = when (p.b) {
                        0 -> if (p.x<D) {
                            val nextB = 3
                            val nextX = 0
                            val nextY = p.x
                            val nextD = Dir.RIGHT
                            Pos(nextB, nextX, nextY, nextD)
                        } else {
                            val nextB = 3
                            val nextD = Dir.UP
                            val nextX = p.x-D
                            val nextY = D-1
                            Pos(nextB, nextX, nextY, nextD)
                        }
                        1 -> {
                            val nextB = 0
                            val nextD = Dir.UP
                            val nextX = p.x
                            val nextY = D-1
                            Pos(nextB, nextX, nextY, nextD)
                        }
                        2 -> if (p.x<D) {
                            val nextB = 1
                            val nextD = Dir.RIGHT
                            val nextX = 0
                            val nextY = p.x
                            Pos(nextB, nextX, nextY, nextD)
                        } else {
                            val nextB = 1
                            val nextD = Dir.UP
                            val nextX = p.x-D
                            val nextY = D-1
                            Pos(nextB, nextX, nextY, nextD)
                        }
                        else -> {
                            val nextB = 2
                            val nextX = p.x
                            val nextY = D-1
                            val nextD = Dir.UP
                            Pos(nextB, nextX, nextY, nextD)
                        }
                    }
                }
            }
//            println(next)
            if (isBlocked(next)) {
                return p
            }
            return next
        }
        fun step(n: Int, startPos: Pos): Pos {
            var currentPos = startPos
            for (i in 1..n) {
                val nextPos = step(currentPos)
                walked.add(nextPos)
                if (nextPos == currentPos) {
                    return currentPos
                }
                currentPos = nextPos
            }
            return currentPos
        }

        fun step(commands: List<String>): Pos {
            return step(Pos(0, 0, 0, Dir.RIGHT), commands)
//            return Pos(0, 0, 0, Dir.RIGHT)
        }
//
        fun step(startPos: Pos, commands: List<String>): Pos {
            walked.add(startPos)
            var currentPos = startPos
            for (c in commands) {
                if (c == "R" || c == "L") {
                    currentPos = Pos(currentPos.b, currentPos.x, currentPos.y, currentPos.d.turn(c))
                    walked.add(currentPos)
                } else {
                    currentPos = step(c.toInt(), currentPos)
                    walked.add(currentPos)
                }
            }
            return currentPos
        }

        fun password(p: Pos): Int {
            val column = p.x + boards[p.b].firstColumn + 1
            val row = p.y + (0 until p.b).sumOf { boards[it].rows.size } + 1
            val d = p.d.ordinal
            val r = 1000 * row + 4 * column + d
            println("$r = 1000 * $row + 4 * $column + $d")
            return r
        }

        fun render(): String {
            val sb = StringBuilder()
            for (i in 0 until boards.size) {
                sb.append(boards[i].toString(i, walked))
            }
            return sb.toString()
        }
    }
    class Board(line: String) {
        val firstLine = line
        val firstColumn = firstLine.indexOfFirst { it != ' ' }
        val width = firstLine.length - firstColumn
        var rows = mutableListOf(firstLine.trim())
        fun addRow(line: String): Boolean {
            val newFirstColumn = line.indexOfFirst { it != ' ' }
            val newWidth = line.length - newFirstColumn
            if (newFirstColumn == firstColumn && newWidth == width) {
                rows.add(line.trim())
                return true
            }
            return false
        }

        fun toString(b: Int, walked: Set<Pos>): String {
            val sb = StringBuilder()
            val prefix = List(firstColumn){' '}.joinToString("")
            for (i in 0 until rows.size) {
                val row = rows[i].toMutableList()
                for (j in 0 until width) {
                    for (k in Dir.values()) {
                        if (walked.contains(Pos(b, j, i, k))) {
                            row[j] = k.toChar()
                        }
                    }
                }
                sb.append(prefix)
                sb.append(row.joinToString(""))
                sb.appendLine()
            }
            return sb.toString()
        }
    }

    fun solve() {
        val f = File("/home/janos/Downloads/day22.in")
        val s = Scanner(f)
//        val s = Scanner(input1)
//                val s = Scanner(input2)

        val boards = Boards()
        var commands = mutableListOf<String>()

        while (s.hasNextLine()) {
            val line = s.nextLine().trimEnd()
            if (!line.isEmpty()) {
                if (line.firstOrNull{it == '.' || it == '#'} == null) {
                    val s1 = line.split(Regex("[RL]"))
                    val s2 = line.split(Regex("[0-9]+"))
                    commands.add(s1.first())
                    for (i in 1 until s1.size) {
                        commands.add(s2[i])
                        commands.add(s1[i])
                    }
                    println(line)
//                    println(commands.joinToString ("" ))
                    println(commands)
                    println( commands.joinToString ("" ).length == line.length)
                    break
                }
                boards.addRow(line)
            }
        }

        boards.cube = true
        val p1 = boards.step(commands)
        boards.password(p1)

        println("${p1}")

        println(boards.render())
    }
}

