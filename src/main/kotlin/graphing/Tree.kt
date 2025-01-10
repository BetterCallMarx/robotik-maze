package graphing

import enums.Direction

object Tree {
    //root of the node corresponds to the starting node, which is the entrance
    var root: Node? = null
    var rootSet = false
    private val mazeCoordinateMap = mutableMapOf<Pair<Int,Int>,Node>()

    fun addTileToTile(tileCoordinate: Pair<Int,Int>, parentCoordinate: Pair<Int,Int>, directionParent: Direction, tile: Tile){

        if (!mazeCoordinateMap.containsKey(parentCoordinate)) {




        }


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




}