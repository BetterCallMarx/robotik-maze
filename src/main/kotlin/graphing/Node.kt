package graphing
//Node for the tree structure, tile is itself, directions are parent nodes
class Node(
    val tile: Tile, // The tile (room) this node represents
    var north: Node? = null,
    var east: Node? = null,
    var south: Node? = null,
    var west: Node? = null
)