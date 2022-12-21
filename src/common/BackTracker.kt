package common


class BackTracker<Solution, N: BackTracker.Node<Solution, N>>(
    root: N,
    collector: SolutionCollector<Solution>) {

    abstract class Context<Node> {
        abstract fun getParent(): Node?
        abstract fun getLevel(): Int
        abstract fun getFinishedChildNode(): Node?
        abstract fun getFinishedNextLeveNode(): Node?
        abstract fun getProgress(): Pair<Long, Long>
        abstract fun printProgress(ticks: Long)

        // stop?
    }

    abstract class SolutionCollector<Solution> {
        abstract fun add(solution: Solution)
        abstract fun get(): Solution
    }

    abstract class Node<Solution, N: Node<Solution, N>> {
        abstract fun nextChild(context: Context<N>): N?
        abstract fun getSolution(collector: SolutionCollector<Solution>)
        fun getProgress(): Pair<Long, Long> {
            return 1L to 1L
        }
    }

    enum class ChildIterationState {
        NOT_STARTED, ONGOING, FINISHED
    }

    class ContextImpl<Solution, N: Node<Solution, N>>(
        root: N,
        val collector: SolutionCollector<Solution>): Context<N>()
    {
        class NodeIterationState<Solution, N: Node<Solution, N>>(var node: N) {
            var isCheckedForSolution = false
            var childIterationState = ChildIterationState.NOT_STARTED
            fun setNewNode(n: N) {
                node = n
                isCheckedForSolution = false
                childIterationState = ChildIterationState.NOT_STARTED
            }
            fun finished() = isCheckedForSolution && childIterationState == ChildIterationState.FINISHED
            fun hasChildren() = childIterationState == ChildIterationState.ONGOING
            fun checkSolution(collector: SolutionCollector<Solution>): Boolean {
                if (isCheckedForSolution) {
                    return false
                }
                node.getSolution(collector)
                isCheckedForSolution = true
                return true
            }
            fun nextChild(context: Context<N>): N? {
                if (childIterationState == ChildIterationState.FINISHED) {
                    return null
                }
                val child = node.nextChild(context)
                if (child == null) {
                    childIterationState = ChildIterationState.FINISHED
                }
                return child
            }
        }

        private val nodes = mutableListOf(NodeIterationState(root))
        private var currentNodeIx = 0

        override fun getParent(): N? {
            if (currentNodeIx == 0) {
                return null
            }
            return nodes[currentNodeIx-1].node
        }

        override fun getLevel(): Int {
            return currentNodeIx
        }

        override fun getFinishedChildNode(): N? {
            if (nodes[currentNodeIx].hasChildren()) {
                return getFinishedNextLeveNode()
            }
            return null
        }

        override fun getFinishedNextLeveNode(): N? {
            if (currentNodeIx < nodes.size-1) {
                return nodes[currentNodeIx+1].node
            }
            return null
        }

        override fun getProgress(): Pair<Long, Long> {
            TODO("Not yet implemented")
        }

        override fun printProgress(ticks: Long) {
            TODO("Not yet implemented")
        }

        tailrec fun solve() {
            val currentNode = nodes[currentNodeIx]
            if (currentNodeIx == 0 && currentNode.finished()) {
                return
            }
            currentNode.checkSolution(collector)
            val nextChild = currentNode.nextChild(this)
            if (nextChild != null) {
                if (currentNodeIx == nodes.size-1) {
                    nodes.add(NodeIterationState(nextChild))
                } else {
                    nodes[currentNodeIx + 1].setNewNode(nextChild)
                }
                ++currentNodeIx
            } else {
                --currentNodeIx
            }
            return solve()
        }
    }

    val context = ContextImpl(root, collector)

    fun solve() {
        context.solve()
    }
}
