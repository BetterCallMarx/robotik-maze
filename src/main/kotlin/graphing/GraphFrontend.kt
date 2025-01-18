package graphing

import enums.Direction
import enums.TileColor

//FrontEnd for Graphbased SLAMing, creates Nodes and Edges, based on sensor data
object GraphFrontend {


    private val startPosition: Pair<Int,Int> = Pair(0,-30) //robot start at position 0,-30
    private val facingNorth : Pair<Direction,Pair<Int,Int>> = Pair(Direction.NORTH,Pair(0,1))
    private val facingWest : Pair<Direction,Pair<Int,Int>> = Pair(Direction.WEST,Pair(-1,0))
    private val facingSouth : Pair<Direction,Pair<Int,Int>> = Pair(Direction.SOUTH,Pair(0,-1))
    private val facingEast : Pair<Direction,Pair<Int,Int>> = Pair(Direction.EAST,Pair(1,0))
    private val directions = listOf(facingNorth, facingEast, facingSouth, facingWest)
    var facing = facingNorth //robot starts facing north
    var visitedPositions : MutableList<Pair<Pair<Int,Int>,Direction>> = mutableListOf() //add position of tile to lastPosition to keep track of visited tiles and get last one
    var currentPosition: Pair<Int,Int> = startPosition

    fun createTile(distances:List<Pair<Int,Direction>>,color: TileColor): Tile {
        //adjust relative Direction to absolute direction
        val absoluteDistances: MutableList<Pair<Int,Direction>> = mutableListOf(
            Pair(distances[0].first,getAbsoluteDirection(distances[0].second)),
            Pair(distances[1].first,getAbsoluteDirection(distances[1].second)),
            Pair(distances[2].first,getAbsoluteDirection(distances[2].second)),
            Pair(distances[3].first,getAbsoluteDirection(distances[3].second))
        )
        //Nach wänden gucken, werte wurden empirisch getestet, und stellen den maximal wert + toleranz dar
        val walls: MutableList<Boolean> = mutableListOf(false, false, false, false )
        for(d in absoluteDistances){
            when(d.second){
                Direction.NORTH -> if(d.first>130){
                    walls[0] = true
                }
                Direction.EAST -> if(d.first>170){
                    walls[1] = true
                }
                Direction.SOUTH -> if(d.first>240){
                    walls[2] = true
                }
                Direction.WEST -> if(d.first>260){
                    walls[3] = true
                }
            }
        }

        //create tiles based on wall shape
        return Tile(walls[0],walls[1],walls[2],walls[3],color, currentPosition)
    }

    fun getInverseDirection(): Direction{
        val currentIndex = directions.indexOf(facing)
        return directions[(currentIndex+2) % directions.size].first
    }

    private fun getAbsoluteDirection(headDirection: Direction):Direction{
        val currentIndex = directions.indexOf(facing)

        // Map relative direction to absolute direction based on the current facing
        return when (headDirection) {
            Direction.NORTH -> directions[currentIndex].first
            Direction.EAST -> directions[(currentIndex + 1) % directions.size].first
            Direction.SOUTH -> directions[(currentIndex + 2) % directions.size].first
            Direction.WEST -> directions[(currentIndex - 1 + directions.size) % directions.size].first

        }
    }


    fun updatePosition(odometry : Pair<Int,Int>){
        val distance = PairArithmetic.multiply(facing.second,odometry)
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

    //calculate the needed Direction from two Positions
    private fun calculateDirection(oldPos: Pair<Int, Int>, newPos: Pair<Int, Int>): Pair<Int, Int> {
        return PairArithmetic.divide(PairArithmetic.subtract(newPos, oldPos), Pair(30, 30))
    }

    fun getTotalDirections(toVisit :List<Pair<Int,Int>>): List<Pair<Int, Int>> {
        //list of direction that connect the tiles that are being visited directions[i] is the direction from toVisit[i] to toVisit[i+1]
        val directions: MutableList<Pair<Int,Int>> = mutableListOf()
        for(i in 0 until toVisit.size-1){
           directions.add(calculateDirection(toVisit[i],toVisit[i+1]))
        }
        return directions
    }










}