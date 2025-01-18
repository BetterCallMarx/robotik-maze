package graphing

import enums.Direction
import enums.TileColor

/**
 * Object das den Baum hält
 *
 * @constructor Create empty Tree
 */
object Tree {
    var root: Node? = null
    var rootSet = false
    private val mazeCoordinateMap = mutableMapOf<Pair<Int,Int>,Node>()

    /**
     * Funktion um Nodes miteinander zu verbinden
     *
     * @param tileCoordinate
     * @param parentCoordinate
     * @param directionParent
     * @param tile
     */
    fun addTileToTile(tileCoordinate: Pair<Int,Int>, parentCoordinate: Pair<Int,Int>, directionParent: Direction, tile: Tile){

        val parentNode = mazeCoordinateMap[parentCoordinate]?:throw IllegalArgumentException("Parent node at $parentCoordinate does not exist")
        val node = Node(tile)

        when(directionParent){
            Direction.NORTH -> {
                parentNode.north = node
                node.south = parentNode
            }

            Direction.SOUTH -> {
                parentNode.south = node
                node.north = parentNode
            }
            Direction.WEST -> {
                parentNode.west = node
                node.east = parentNode
            }
            Direction.EAST -> {
                parentNode.east = node
                node.west = parentNode
            }
        }
        mazeCoordinateMap[tileCoordinate] = node
    }

    /**
     * Nodes werden miteinander verbunden oder ersetzt
     *
     * @param tileCoordinate
     * @param parentCoordinate
     * @param directionParent
     * @param newTile
     */
    fun addOrReplaceTileToTile(tileCoordinate: Pair<Int, Int>, parentCoordinate: Pair<Int, Int>, directionParent: Direction, newTile: Tile) {
        val existingNode = mazeCoordinateMap[tileCoordinate]

        if (existingNode != null) {
            val newNode = Node(newTile)

            // Disconnect the old tile from its neighbors
            val directions = listOf(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
            for (direction in directions) {
                when (direction) {
                    Direction.NORTH -> existingNode.north?.south = null
                    Direction.SOUTH -> existingNode.south?.north = null
                    Direction.EAST -> existingNode.east?.west = null
                    Direction.WEST -> existingNode.west?.east = null
                }
            }

            // Reconnect the new node to its neighbors
            for (direction in directions) {
                when (direction) {
                    Direction.NORTH -> {
                        existingNode.north?.south = newNode
                        newNode.north = existingNode.north
                    }
                    Direction.SOUTH -> {
                        existingNode.south?.north = newNode
                        newNode.south = existingNode.south
                    }
                    Direction.EAST -> {
                        existingNode.east?.west = newNode
                        newNode.east = existingNode.east
                    }
                    Direction.WEST -> {
                        existingNode.west?.east = newNode
                        newNode.west = existingNode.west
                    }
                }
            }

            // Replace the old node in the map with the new node
            mazeCoordinateMap[tileCoordinate] = newNode
            println("Node replaced")
        } else {

            val parentNode = mazeCoordinateMap[parentCoordinate]
                ?: throw IllegalArgumentException("Parent node at $parentCoordinate does not exist")
            val node = Node(newTile)

            when (directionParent) {
                Direction.NORTH -> {
                    parentNode.north = node
                    node.south = parentNode
                }
                Direction.SOUTH -> {
                    parentNode.south = node
                    node.north = parentNode
                }
                Direction.WEST -> {
                    parentNode.west = node
                    node.east = parentNode
                }
                Direction.EAST -> {
                    parentNode.east = node
                    node.west = parentNode
                }
            }
            mazeCoordinateMap[tileCoordinate] = node
            println("Node added")
        }
    }

    /**
     * Erstellen vom root
     *
     * @param tile
     */
    fun addRoot(tile: Tile){
        val initNode = Node(tile)
        root = initNode
        mazeCoordinateMap[tile.coordinates] = initNode
        rootSet = true
    }

    /**
     * Algorhitmus um den kurzesten Weg zu finden, benutzt BFS
     *
     * @param startCoordinate
     * @param colors
     * @return
     */
    fun findShortestPathThroughColorsAndReturn(
        startCoordinate: Pair<Int, Int>,
        colors: Set<TileColor>
    ): List<Pair<Int, Int>> {

        val startNode = mazeCoordinateMap[startCoordinate]
            ?: return emptyList()

        val visited = mutableSetOf<Pair<Node, Set<TileColor>>>() // Track visited nodes with remaining colors
        val queue: ArrayDeque<Triple<Node, List<Pair<Int, Int>>, Set<TileColor>>> = ArrayDeque()
        queue.add(Triple(startNode, listOf(startCoordinate), colors)) // Start BFS with all colors remaining

        while (queue.isNotEmpty()) {
            val (currentNode, path, remainingColors) = queue.removeFirst()

            // If we reached the root and visited all colors, return the path
            if (currentNode == root && remainingColors.isEmpty()) {
                return path
            }

            // Mark the current state (node and remaining colors) as visited
            visited.add(Pair(currentNode, remainingColors))

            // Check if the current node has a color and update the remaining colors
            val updatedColors = if (currentNode.tile.color in remainingColors) {
                remainingColors - currentNode.tile.color
            } else {
                remainingColors
            }


            val neighbors = listOf(
                currentNode.north to Direction.NORTH,
                currentNode.south to Direction.SOUTH,
                currentNode.east to Direction.EAST,
                currentNode.west to Direction.WEST
            )

            for ((neighbor, direction) in neighbors) {
                if (neighbor != null && Pair(neighbor, updatedColors) !in visited) {
                    // Compute the new coordinate based on direction
                    val neighborCoordinate = when (direction) {
                        Direction.NORTH -> Pair(path.last().first, path.last().second - 30)
                        Direction.SOUTH -> Pair(path.last().first, path.last().second + 30)
                        Direction.EAST -> Pair(path.last().first - 30, path.last().second)
                        Direction.WEST -> Pair(path.last().first + 30, path.last().second)
                    }

                    queue.add(Triple(neighbor, path + neighborCoordinate, updatedColors))
                }
            }
        }

        return emptyList()
    }



}