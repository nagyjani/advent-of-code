package common

class BackTracker<Solution, N: BackTracker.Node<Solution, N>>(private val root: N): Iterable<Solution> {
    abstract class Node<Solution, N: Node<Solution, N>>(val parent: N?) {
        abstract fun hasNextChild(): Boolean
        abstract fun nextChild(recycledNode: N?, level: Int): N
        abstract fun getSolution(): Solution?
        abstract fun reset(): N
    }
    override fun iterator(): Iterator<Solution> {
        return NodeIterator(root.reset())
    }
    inner class NodeIterator(root: N): Iterator<Solution> {
        private val nodes = mutableListOf(root)
        private var isNodeCheckedForSolution = false
        private var maxNodeIx = 0
        private var currentNodeIx = 0
        var next: Solution? = null
        override fun hasNext(): Boolean {
            if (next == null) {
                next = nextSolution()
            }
            return next != null
        }
        override fun next(): Solution {
            return next!!.also { next = nextSolution() }
        }
        private tailrec fun nextSolution(): Solution? {
            if (currentNodeIx == 0 && isNodeCheckedForSolution && !nodes[0].hasNextChild()) {
                return null
            }
            if (!isNodeCheckedForSolution) {
                isNodeCheckedForSolution = true
                val s = nodes[currentNodeIx].getSolution()
                if (s != null) {
                    return s
                }
            }
            if (nodes[currentNodeIx].hasNextChild()) {
                val parentNode = nodes[currentNodeIx]
                ++currentNodeIx
                isNodeCheckedForSolution = false
                if (maxNodeIx < currentNodeIx) {
                    maxNodeIx = currentNodeIx
                    nodes.add(parentNode.nextChild(null, currentNodeIx))
                } else {
                    val recycledNode = nodes[currentNodeIx]
                    nodes[currentNodeIx] = parentNode.nextChild(recycledNode, currentNodeIx)
                }
            } else {
                --currentNodeIx
            }
            return nextSolution()
        }
    }
}
