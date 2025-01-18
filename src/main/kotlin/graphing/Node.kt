package graphing

/**
 * Knoten KLasse
 *
 * @property tile Hält das eigentliche Tile
 * @property north Verbindung zum Node im Norden
 * @property east Verbindung zum Node im Osten
 * @property south Verbindung zum Node im Süden
 * @property west Verbindung zum Node im Westen
 * @constructor Create empty Node
 *///Node for the tree structure, tile is itself, directions are parent nodes
class Node(
    val tile: Tile, // The tile (room) this node represents
    var north: Node? = null,
    var east: Node? = null,
    var south: Node? = null,
    var west: Node? = null
)