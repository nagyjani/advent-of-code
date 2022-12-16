package common

data class Interval(val start: Int, val length: Int) {

    fun end(): Int = start + length - 1
    fun mergable(e: Interval): Boolean {
        if (start >= e.start && start <= e.end()+1 || e.start >= start && e.start <= end()+1) {
            return true
        }
        return false
    }

    fun compare(e: Interval): Int {
        if (mergable(e)) {
            return 0
        }
        if (start > e.start) {
            return -1
        }
        return 1
    }
    fun merge(e: Interval): Interval {
        val newStart = minOf(start, e.start)
        val newEnd = maxOf(end(), e.end())
        return Interval(newStart, newEnd-newStart+1)
    }

    fun has(pos: Int): Boolean {
        return start <= pos && pos <= end()
    }
    // intersect
    //
}