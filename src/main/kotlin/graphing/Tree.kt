package graphing

import enums.Direction
import enums.TileColor

object Tree {
    //root of the node corresponds to the starting node, which is the entrance
    var root: Node? = null
    var rootSet = false
    private val mazeCoordinateMap = mutableMapOf<Pair<Int,Int>,Node>()

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

    fun addRoot(tile: Tile){
        val initNode = Node(tile)
        root = initNode
        mazeCoordinateMap[tile.coordinates] = initNode
        rootSet = true
    }


    fun printTree() {
        if (root == null) {
            println("The tree is empty.")
            return
        }

        println("Maze Tree:")
        printNode(root, mutableSetOf())
    }

    private fun printNode(node: Node?, visited: MutableSet<Pair<Int, Int>>) {
        if (node == null || visited.contains(node.tile.coordinates)) return

        // Mark this node as visited
        visited.add(node.tile.coordinates)

        // Print the current node's details
        println("Node at ${node.tile.coordinates}:")
        if (node.north != null) println("  North -> ${node.north?.tile?.coordinates}")
        if (node.east != null) println("  East -> ${node.east?.tile?.coordinates}")
        if (node.south != null) println("  South -> ${node.south?.tile?.coordinates}")
        if (node.west != null) println("  West -> ${node.west?.tile?.coordinates}")

        // Recursively print connected nodes
        printNode(node.north, visited)
        printNode(node.east, visited)
        printNode(node.south, visited)
        printNode(node.west, visited)
    }

    fun findShortestPathToRoot(startCoordinate: Pair<Int, Int>): List<Pair<Int, Int>> {
        val startNode = mazeCoordinateMap[startCoordinate]
            ?: return emptyList() // Return an empty list if the start node doesn't exist
        val visited = mutableSetOf<Node>() // To avoid revisiting nodes
        val queue: ArrayDeque<Pair<Node, List<Pair<Int, Int>>>> = ArrayDeque() // Queue for BFS with path tracking

        // Initialize the queue with the start node and an empty path
        queue.add(Pair(startNode, listOf(startCoordinate)))

        while (queue.isNotEmpty()) {
            val (currentNode, path) = queue.removeFirst()

            // If we reach the root, return the path
            if (currentNode == root) {
                return path
            }

            // Mark the current node as visited
            visited.add(currentNode)

            // Explore all neighbors (north, south, east, west)
            val neighbors = listOf(
                currentNode.north to Direction.NORTH,
                currentNode.south to Direction.SOUTH,
                currentNode.east to Direction.EAST,
                currentNode.west to Direction.WEST
            )

            for ((neighbor, direction) in neighbors) {
                if (neighbor != null && !visited.contains(neighbor)) {
                    // Add the neighbor to the queue with the updated path
                    val neighborCoordinate = when (direction) {
                        Direction.NORTH -> Pair(path.last().first, path.last().second - 30)
                        Direction.SOUTH -> Pair(path.last().first, path.last().second + 30)
                        Direction.EAST -> Pair(path.last().first - 30, path.last().second)
                        Direction.WEST -> Pair(path.last().first + 30, path.last().second)
                    }
                    queue.add(Pair(neighbor, path + neighborCoordinate))
                }
            }
        }

        return emptyList() // No path found, return an empty list
    }

    fun findShortestPathThroughColors(startCoordinate: Pair<Int, Int>): List<Pair<Int, Int>> {
        val startNode = mazeCoordinateMap[startCoordinate]
            ?: return emptyList() // Return an empty list if the start node doesn't exist

        // Tiles to visit
        val colorsToVisit = mutableSetOf(TileColor.RED, TileColor.GREEN, TileColor.BLUE)
        val visitedStates = mutableSetOf<Pair<Node, Set<TileColor>>>() // Tracks (current node, visited colors)
        val queue: ArrayDeque<Triple<Node, List<Pair<Int, Int>>, Set<TileColor>>> = ArrayDeque()
        // Triple: current node, path so far, colors visited

        // Initialize the queue with the start node
        queue.add(Triple(startNode, listOf(startCoordinate), emptySet()))

        while (queue.isNotEmpty()) {
            val (currentNode, path, visitedColors) = queue.removeFirst()

            // If we've visited all colors and returned to the entrance, return the path
            if (visitedColors == colorsToVisit && currentNode == root) {
                return path
            }

            // Mark the current state as visited
            visitedStates.add(Pair(currentNode, visitedColors))

            // Check if this node is a colored tile and update visited colors
            val updatedVisitedColors = if (currentNode.tile.color in colorsToVisit) {
                visitedColors + currentNode.tile.color
            } else {
                visitedColors
            }

            // Explore all neighbors (north, south, east, west)
            val neighbors = listOf(
                currentNode.north to Direction.NORTH,
                currentNode.south to Direction.SOUTH,
                currentNode.east to Direction.EAST,
                currentNode.west to Direction.WEST
            )

            for ((neighbor, direction) in neighbors) {
                if (neighbor != null) {
                    // Calculate the next coordinate
                    val neighborCoordinate = when (direction) {
                        Direction.NORTH -> Pair(path.last().first, path.last().second - 30)
                        Direction.SOUTH -> Pair(path.last().first, path.last().second + 30)
                        Direction.EAST -> Pair(path.last().first - 30, path.last().second)
                        Direction.WEST -> Pair(path.last().first + 30, path.last().second)
                    }

                    // Add the neighbor to the queue if not visited in the current state
                    val nextState = Pair(neighbor, updatedVisitedColors)
                    if (!visitedStates.contains(nextState)) {
                        queue.add(Triple(neighbor, path + neighborCoordinate, updatedVisitedColors))
                    }
                }
            }
        }

        return emptyList() // No path found
    }


    fun findShortestPathThroughColorsAndReturn(
        startCoordinate: Pair<Int, Int>,
        colors: Set<TileColor>
    ): List<Pair<Int, Int>> {
        // Ensure the starting node exists
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

            // Explore all neighbors
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

                    // Add the neighbor to the queue with updated path and remaining colors
                    queue.add(Triple(neighbor, path + neighborCoordinate, updatedColors))
                }
            }
        }

        return emptyList() // No valid path found
    }





}