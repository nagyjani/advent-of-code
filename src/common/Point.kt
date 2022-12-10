package common
data class Point(val x: Int, val y: Int) {
    operator fun compareTo(p: Point): Int {
        if (x < p.x) {
            return -1
        }
        if (x > p.x) {
            return 1
        }
        if (y < p.y) {
            return -1
        }
        if (y > p.y) {
            return 1
        }
        return 0
    }
}
