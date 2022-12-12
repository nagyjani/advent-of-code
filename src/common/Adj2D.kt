package common

class Adj2D(val columns: Int, val rows: Int) {
    fun adj4(i: Int): List<Int> {
        val adj = mutableListOf<Int>()
        val c = toColumn(i)
        val r = toRow(i)
        if (c>0) {
            adj.add(i-1)
        }
        if (c<columns-1) {
            adj.add(i+1)
        }
        if (r>0) {
            adj.add(i-columns)
        }
        if (r<rows-1) {
            adj.add(i+columns)
        }
        return adj
    }
    fun adj8(i: Int): List<Int> {
        val adj = mutableListOf<Int>()
        val c = toColumn(i)
        val r = toRow(i)
        for (ic in Integer.max(0, c - 1)..Integer.min(columns - 1, c + 1)) {
            for (jr in Integer.max(0, r - 1)..Integer.min(rows - 1, r + 1)) {
                if (ic!=c || jr!=r) {
                    adj.add(toIx(ic, jr))
                }
            }
        }
        return adj
    }
    fun toRow(i: Int): Int {
        return i/columns
    }
    fun toColumn(i: Int): Int {
        return i%columns
    }
    fun toIx(c: Int, r: Int): Int {
        return r*columns+c
    }
}