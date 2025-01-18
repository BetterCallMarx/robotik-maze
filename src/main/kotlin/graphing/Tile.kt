package graphing

import enums.TileColor

/**
 * Tile Klasse
 *
 * @property northOpen
 * @property eastOpen
 * @property southOpen
 * @property westOpen
 * @property color
 * @property coordinates
 * @constructor Create empty Tile
 */
class Tile(
    val northOpen: Boolean,
    val eastOpen: Boolean,
    val southOpen: Boolean,
    val westOpen: Boolean,
    val color: TileColor,
    val coordinates: Pair<Int,Int>
) {
    /**
     * Besuchte Seiten
     */
    var northVisited: Boolean = false
    var eastVisited: Boolean = false
    var southVisited: Boolean = false
    var westVisited: Boolean = false

    /**
     * Print tile
     *
     */
    fun printTile(){
        println("$northOpen $eastOpen $southOpen $westOpen $color $coordinates")

    }

}