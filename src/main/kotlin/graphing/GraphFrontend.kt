package graphing

import enums.Direction
import enums.TileColor
import kotlin.math.abs

//FrontEnd for Graphbased SLAMing, creates Nodes and Edges, based on sensor data
object GraphFrontend {
    /*
    Initiale Position ist die Position, an welcher man beginnt, dieses ist 28/2 cm vom Eingang entfernt und mittig zu diesem auf der y achse
    TODO wir sind immer in der mitte eines feldes 0,0 ist die mitte des eingangs, also starten wir 0,-30
    TODO Der Mittelpunkt des Roboters ist zwischen den rädern, weeder vorne oder mittig
    TODO das nutzen um auf der GUI wände zu platzieren
    TODO ACTUAL TODO TREE FÜR DEN SHORTEST WAY OUT MACHEN KA WIE MAN DA BEIDES VERBINDET WENN MAN BACKTRACKED

    TODO Für bericht: position wird mit der richtung multipliziert und der entfernung errechnet, da eine übersicht machen
    TODO da wir keine perfekte 90 drehung und ausrichtung haben, repositioned wir ihn manuell
    TODO der einfachhalters halber wird bei jeder Drehung die direction angepoasst indem durch eine liste mit vorgefertigten direction iteriert wird
    TODO relative auf absolute Position der Wand wir mittels offset erhalten
    */

    val startPosition: Pair<Int,Int> = Pair(0,0)
    private val facingNorth : Pair<Direction,Pair<Int,Int>> = Pair(Direction.NORTH,Pair(0,1))
    private val facingWest : Pair<Direction,Pair<Int,Int>> = Pair(Direction.WEST,Pair(-1,0))
    private val facingSouth : Pair<Direction,Pair<Int,Int>> = Pair(Direction.SOUTH,Pair(0,-1))
    private val facingEast : Pair<Direction,Pair<Int,Int>> = Pair(Direction.EAST,Pair(1,0))
    private val directions = listOf(facingNorth, facingEast, facingSouth, facingWest)
    private var facing = facingNorth
    var lastPosition : Pair<Int,Int> = Pair(0,0)
    var currentPosition: Pair<Int,Int> = Pair(0,0)

    fun createTile(distances:MutableList<Pair<Int,Direction>>,color: TileColor): Tile {
        //adjust relative Direction to absolute direction
        val absoluteDistances: MutableList<Pair<Int,Direction>> = mutableListOf(
            Pair(distances[0].first,getAbsoluteDirection(distances[0].second)),
            Pair(distances[1].first,getAbsoluteDirection(distances[1].second)),
            Pair(distances[2].first,getAbsoluteDirection(distances[2].second)),
            Pair(distances[3].first,getAbsoluteDirection(distances[3].second))
        )
        //check for walls
        val walls: MutableList<Boolean> = mutableListOf()
        for(d in absoluteDistances){
            if(d.first>14){
                walls.add(true)
            }else{
                walls.add(false)
            }
        }
        //create tiles based on wall shape
        return Tile(walls[0],walls[1],walls[2],walls[3],color, currentPosition)
    }

    private fun getAbsoluteDirection(headDirection: Direction):Direction{
        val currentIndex = directions.indexOf(facing)

        // Map relative direction to absolute direction based on the current facing
        return when (headDirection) {
            Direction.NORTH -> directions[currentIndex].first
            Direction.EAST -> directions[(currentIndex + 1) % directions.size].first
            Direction.SOUTH -> directions[(currentIndex + 2) % directions.size].first
            Direction.WEST -> directions[(currentIndex - 1 + directions.size) % directions.size].first
            else -> throw IllegalArgumentException("Invalid relative direction")
        }
    }

    fun updatePosition(odometry : Pair<Int,Int>){
        val distance = PairArithmetic.multiply(facingNorth.second,odometry)
        currentPosition = PairArithmetic.add(distance, currentPosition)
    }
    fun turnWest() {
        val currentIndex = directions.indexOf(facing)
        facing = directions[(currentIndex - 1 + directions.size) % directions.size]
    }


    fun turnEast(){
        val currentIndex = directions.indexOf(facing)
        facing = directions[(currentIndex + 1) % directions.size]
    }

    fun colorToEnum(color: String):TileColor{
        return when (color) {
            "blue" -> TileColor.BLUE
            "red" -> TileColor.RED
            "green" -> TileColor.GREEN
            "white" -> TileColor.NONE
            else -> TileColor.NONE
        }
    }






}