class Tile (
   val northOpen : Boolean,
   val eastOpen : Boolean,
   val southOpen : Boolean,
   val westOpen : Boolean
    )
{
    //whether an opening has been visited or not
    var northVisited: Boolean = false
    var eastVisited: Boolean = false
    var southVisited: Boolean = false
    var westVisited: Boolean = false

    var texture: String = ""


    fun setTexture(){
        //TODO function to set the texture of this file
    }


}