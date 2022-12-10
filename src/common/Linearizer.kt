package common

class Linearizer(vararg val dimensions: Int) {

    val numOfDimensions = dimensions.size
    val size = dimensions.fold(1){ acc, it -> acc*it}
    val indexes
        get() = 0 until size

    fun toCoordinates(ix: Int): IntArray {
        val coordinates = IntArray(numOfDimensions){0}
        toCoordinates(ix, coordinates)
        return coordinates
    }

    fun toCoordinates(ix: Int, coordinates: IntArray) {
        var ix1 = ix
        for (i in 0 until numOfDimensions) {
            coordinates[i] = ix1 % dimensions[i]
            ix1 = ix1 / dimensions[i]
        }
    }

    fun toIndex(vararg coordinates: Int): Int {
        var resultIx = 0
        for (i in numOfDimensions-1 downTo 0) {
            resultIx = resultIx * dimensions[i] + coordinates[i]
        }
        return resultIx
    }

    fun offset(vararg vix: Int): Offset {
        return Offset(this, vix)
    }

//    fun createOffsetIterator(): Offsets {
//        return Offset
//    }

    // maybe have an array as coordinates too?
}

fun <E> List<E>.get(l: Linearizer, vararg coordinates: Int): E {
    return get(l.toIndex(*coordinates))
}

fun <E> MutableList<E>.set(v: E, l: Linearizer, vararg coordinates: Int): E {
    return set(l.toIndex(*coordinates), v)
}

fun Iterable<Offset>.around(ix: Int): Iterable<Int> {
    return AppliedOffsets(ix, this)
}

// maybe
class Offset(val linearizer: Linearizer, val vector: IntArray) {
    // todo uniform vector
    val linearOffset = linearizer.toIndex(*vector)

    fun apply(ix: Int): Int? {
        var ix1 = ix
        for (i in 0 until linearizer.numOfDimensions) {
            val x = ix1%linearizer.dimensions[i] + vector[i]
            if (x<0 || x>=linearizer.dimensions[i]) {
                // todo wraparound
                return null
            }
            ix1 = ix1/linearizer.dimensions[i]
        }
        return ix + linearOffset
    }

    // todo: apply to index (in place)
    // todo: transform index

    // todo fun for validations
    // * the vector has the right length
    // * they are within the ranges of linearizer
    // * if both set, they are the same in the linearizer
}

class AppliedOffsets(val ix: Int, val offsets: Iterable<Offset>): Iterable<Int> {

    override fun iterator(): ResultIterator {
        return ResultIterator()
    }

    inner class ResultIterator: Iterator<Int> {

        val offsetIterator = offsets.iterator()
        var next: Int? = null

        override fun hasNext(): Boolean {
            if (next == null) {
                updateNext()
            }
            return next != null
        }

        override fun next(): Int {
            return next!!.also { updateNext() }
        }

        fun updateNext() {
            next = null
            while (next == null && offsetIterator.hasNext()) {
                next = offsetIterator.next().apply(ix)
            }
        }
    }
}