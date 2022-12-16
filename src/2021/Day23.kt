import java.io.File
import java.util.*
import kotlin.math.abs

fun main() {
    Day23().solve()
}

private enum class LocationType {CORRIDOR, ROOM}
// #############
// #01.2.3.4.56#
// ###0#1#2#3###
//   #.#.#.#.#
private data class Location(val type: LocationType, val index: Int) {
    companion object {
        val allOrdered =
            listOf(
                Location(LocationType.CORRIDOR, 0),
                Location(LocationType.CORRIDOR, 1),
                Location(LocationType.ROOM, 0),
                Location(LocationType.CORRIDOR, 2),
                Location(LocationType.ROOM, 1),
                Location(LocationType.CORRIDOR, 3),
                Location(LocationType.ROOM, 2),
                Location(LocationType.CORRIDOR, 4),
                Location(LocationType.ROOM, 3),
                Location(LocationType.CORRIDOR, 5),
                Location(LocationType.CORRIDOR, 6))
        val all: List<Location> get() = allOrdered
        val rooms = all.filter { it.isRoom }
        val corridor = all.filter { it.isCorridor }
        val corridorIndices = corridor.map { toOrderIx(it) }
        fun toOrderIx(l: Location): Int {
            return allOrdered.indexOf(l)
        }
        fun fromOrderIx(ix: Int): Location {
            return allOrdered[ix]
        }
    }
    val orderIx: Int get() = toOrderIx(this)
    val isRoom: Boolean get() = type == LocationType.ROOM
    val isCorridor: Boolean get() = type == LocationType.CORRIDOR
    override fun toString(): String {
        return "( ${if (isRoom) "R" else "C"}$index/${orderIx})"
    }
    fun distance(other: Location): Int {
        return abs(stepsTo(other))
    }
    fun stepsTo(other: Location): Int {
        if (orderIx == other.orderIx) {
            return 0
        }
        val extraSteps = listOf(this, other).count{it.isRoom}
        val diff = other.orderIx - orderIx
        if (diff>0) {
            return diff + extraSteps
        }
        return diff - extraSteps
    }
    fun indicesUntil(other: Location): IntRange {
        return if (orderIx < other.orderIx) {
            orderIx+1..other.orderIx-1
        } else {
            other.orderIx+1..orderIx-1
        }
    }
    fun locationsUntil(other: Location): List<Location> {
        return indicesUntil(other).map { all[it] }
    }
    fun indicesTo(other: Location): IntRange {
        return if (orderIx < other.orderIx) {
            orderIx+1..other.orderIx
        } else {
            other.orderIx..orderIx-1
        }
    }
    fun locationsTo(other: Location): List<Location> {
        return indicesTo(other).map { all[it] }
    }
}

private enum class Agent(val roomIndex: Int, val stepCost: Int) {
    A(0, 1),
    B(1, 10),
    C(2, 100),
    D(3, 1000);
    fun isHomeRoom(l: Location): Boolean {
        return l.isRoom && isHomeRoom(l.index)
    }
    fun isHomeRoom(index: Int): Boolean {
        return index == roomIndex
    }
    fun homeRoom(): Location {
        return Location(LocationType.ROOM, roomIndex)
    }
    fun minHomeDistance(location: Location): Int {
        return location.distance(homeRoom())
    }
    fun minReturnCost(location: Location): Int {
        return minHomeDistance(location) * stepCost
    }
}

class Day23 {

    val input1 =
"""#############
#...........#
###B#C#B#D###
  #A#D#C#A#
  #########"""

    val input2 =
"""#############
#...........#
###C#D#D#A###
  #B#A#B#C#
  #########"""

    val input3 =
"""#############
#...........#
###B#C#B#D###
  #D#C#B#A#
  #D#B#A#C#
  #A#D#C#A#
  #########"""

    val input4 =
"""#############
#...........#
###C#D#D#A###
  #D#C#B#A#
  #D#B#A#C#
  #B#A#B#C#
  #########"""

// #############
// #01.2.3.4.56#
// ###0#1#2#3###
//   #.#.#.#.#

    private data class Move(val agent: Agent, val source: Location, val target: Location, val distance: Int, val fromState: State? = null) {
        override fun toString(): String {
            return "$agent: $source -> $target ($distance/${cost()})"
        }
        fun cost(): Int {
            return agent.stepCost * distance
        }
    }

    private class MinCostStateFinder(initialState: State) {
        val states = PriorityQueue<State>(100, {a, b -> a.minFinishCost.compareTo(b.minFinishCost)})
        var minState: State? = null
        var maxSize = 0
        var added = 0
        val agentsStates = mutableMapOf<AgentsState, State>()
        init {
            if (initialState.isFinished()) {
                minState = initialState
            } else {
                add(initialState)
            }
        }
        fun isOptimal(s1: State): Boolean {
            val s0 = agentsStates.get(AgentsState(s1))
            if (s0 == null) {
                return true
            }
            if (s0 === s1) {
                return true
            }
            assert(s0.minReturnCost == s1.minReturnCost)
            return s1.cost < s0.cost
        }
        fun add(s: State) {
            states.add(s)
            agentsStates[AgentsState(s)] = s
            ++added
            if (states.size>maxSize) {
                maxSize = states.size
            }
//            if (added % 10000 == 0) {
//                println("${added} ${maxSize} ${s.steps.size} ${s.minFinishCost} ${minState!=null} ${agentsStates.size}")
//            }
        }
        fun isLessCostThanMin(s: State): Boolean {
            return s.minFinishCost < (minState?.cost ?: Int.MAX_VALUE)
        }
        tailrec fun find(): State? {
            if (states.isEmpty()) {
                return minState
            }
            val nextState = states.remove()
            if (!isLessCostThanMin(nextState)) {
                return find()
            }
            if (!isOptimal(nextState)) {
                return find()
            }
            val nextStates = nextState.nextStates()
            for (s in nextStates) {
                if (isLessCostThanMin(s)) {
                    if (s.isFinished()) {
                        minState = s
                    } else if (isOptimal(s)) {
                        add(s)
                    }
                }
            }
            return find()
        }
    }

    private class AgentsState(val s: State) {
        override fun equals(other: Any?): Boolean {
            return (other is AgentsState) && s.corridor.equals(other.s.corridor) && s.rooms.equals(other.s.rooms)
        }
        override fun hashCode(): Int {
            return (s.corridorToString() + s.roomsToString()).hashCode()
        }
    }

    private class State(val rooms: List<List<Agent?>>,
                        val corridor: List<Agent?> = List(7){null},
                        val cost: Int = 0,
                        val minReturnCost: Int =
                            Location.rooms.map{r ->
                                val room = rooms[r.index]
                                room.map { it?.minReturnCost(r) ?: 0 }.sum()
                            }.sum(),
                        val steps: List<Move> = listOf()) {

        val roomSize = rooms[0].size
        val minFinishCost: Int get() = cost + minReturnCost

        init {
            assert(rooms.size == 4)
            assert(rooms.all{it.size == roomSize})
            assert(corridor.size == 7)
            if (isFinished()) {
                assert(minReturnCost == 0)
            }
        }

        fun getAgent(location: Location): Agent? {
            if (location.isCorridor) {
                return corridor[location.index]
            }
            val room = rooms[location.index]
            return room.firstNotNullOfOrNull { it }
        }

        fun getAgentAfterMove(location: Location, move: Move): Agent? {
            if (location == move.source) {
                return null
            }
            if (location == move.target) {
                return getAgent(move.source)
            }
            return getAgent(location)
        }

        fun move(source: Location, target: Location): Move? {
            if (source == target) {
                return null
            }
            if (source.isCorridor && target.isCorridor) {
                return null
            }
            val agent = getAgent(source)
            if (agent == null) {
                return null
            }
            if (target.isRoom && (!agent.isHomeRoom(target) || hasRoomAlien(target.index))) {
                return null
            }
            if (target.isCorridor && source.isRoom && !hasRoomAlien(source.index)) {
                return null
            }
            if (source.locationsTo(target).any {
                it.isCorridor && getAgent(it) != null
            }) {
                return null
            }
            val distance = abs(target.orderIx - source.orderIx) +
                    (if (target.isRoom) getEmptySpace(target.index) else 0) +
                    (if (source.isRoom) getEmptySpace(source.index) + 1 else 0)
//            val move = Move(agent, source, target, distance)
            val move = Move(agent, source, target, distance, this)
            if (causesDeadlock(move)) {
                return null
            }
            return move
        }

        fun causesDeadlock(move: Move): Boolean {
            return causesDeadlock(listOf(move.target), move)
        }

        fun causesDeadlock(blockChain: List<Location>, move: Move): Boolean {
            val lastLocation = blockChain.last()
            val lastAgent = getAgentAfterMove(lastLocation, move)!!
            val nextBlockers = lastLocation.locationsUntil(lastAgent.homeRoom()).filter{getAgentAfterMove(it, move) != null}
            for (l in nextBlockers) {
                if (blockChain.indexOf(l) != -1) {
                    return true
                } else {
                    return causesDeadlock(blockChain.toMutableList().apply{add(l)}, move)
                }
            }
            return false
        }

        fun nextState(source: Location, target: Location): State? {
            val move = move(source, target) ?: return null
            val agent = getAgent(source)!!
            val newCost = cost + move.cost()
            val newRooms = rooms.map{it.toMutableList()}
            val newCorridor = corridor.toMutableList()
            val newMinReturnCost =
                minReturnCost - agent.minReturnCost(source) + agent.minReturnCost(target)
            val newSteps = steps.toMutableList().also{it.add(move)}
            if (source.isRoom) {
                val sourceRoom = newRooms[source.index]
                sourceRoom[sourceRoom.indexOfFirst { it != null }] = null
            } else {
                newCorridor[source.index] = null
            }
            if (target.isRoom) {
                val targetRoom = newRooms[target.index]
                targetRoom[targetRoom.lastIndexOf(null)] = agent
            } else {
                newCorridor[target.index] = agent
            }
            return State(newRooms, newCorridor, newCost, newMinReturnCost, newSteps)
        }

        fun nextStates(): List<State> {
            return Location.all.flatMap{it1 -> Location.all.map{it2 -> nextState(it1, it2)}.filter{it != null}.map{it!!}}.sortedBy { it.cost }
        }

        fun isFinished(): Boolean {
            return rooms.indices.all { isRoomFinishished(it) }
        }

        fun hasRoomAlien(roomIndex: Int): Boolean {
            return rooms[roomIndex].any{it != null && !it.isHomeRoom(roomIndex)}
        }

        fun getEmptySpace(roomIndex: Int): Int {
            return rooms[roomIndex].count { it == null }
        }

        fun isRoomFinishished(roomIndex: Int): Boolean {
            return rooms[roomIndex].all{it != null && it.isHomeRoom(roomIndex)}
        }

        fun corridorToString(): String {
            return Location.all.map{ if (it.isRoom) {"${it.index}"} else {corridor[it.index]?.toString()?:"."} }.joinToString("")
        }

        fun roomToString(roomIndex: Int): String {
            return rooms[roomIndex].map{it?.toString()?:"."}.joinToString("")
        }

        fun roomsToString(): String {
            return rooms.indices.map{"R$it:${roomToString(it)}:${getEmptySpace(it)}"}.joinToString("|")
        }

        override fun toString(): String {
            return "[C:${corridorToString()}|${roomsToString()}]($cost/$minFinishCost)"
        }
    }

    fun solve() {
//        val f = File("src/2021/inputs/day23.in")
//        val s = Scanner(f)
        val s = Scanner(input4)
        s.useDelimiter("[^ABCD]+")
        val initialRoom = listOf<Agent?>()
        val initialRooms = List(4){initialRoom.toMutableList()}
        var i = 0
        while (s.hasNext()) {
            initialRooms[i].add(Agent.valueOf(s.next("[ABCD]")))
            i = (i+1) % 4
        }
        val state = State(initialRooms)
        val finder = MinCostStateFinder(state)
        val bestFinalState = finder.find()!!
        println("${bestFinalState.cost} ${finder.maxSize} ${finder.added}")
    }
}