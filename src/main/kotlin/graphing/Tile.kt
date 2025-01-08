package graphing

import enums.TileColor

class Tile(
    val northOpen: Boolean,
    val eastOpen: Boolean,
    val southOpen: Boolean,
    val westOpen: Boolean,
    val color: TileColor,
    val coord: Pair<Int,Int>
) {
    //whether an opening has been visited or not
    var northVisited: Boolean = false
    var eastVisited: Boolean = false
    var southVisited: Boolean = false
    var westVisited: Boolean = false


}