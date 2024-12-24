package graphing

//FrontEnd for Graphbased SLAMing, creates Nodes and Edges, based on sensor data
object GraphFrontend {

    fun createTile(distances: List<Int>): String {
        var tileShape: String = ""
        for (d in distances) {
            println(d)
            tileShape += if (d < 28) {
                "1"
            } else {
                "0"
            }
        }
        return tileShape
    }


}