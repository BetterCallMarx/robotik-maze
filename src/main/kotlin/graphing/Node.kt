package graphing

class Node<T>(val value: T, val parent: Node<T>? = null) {
    val children: MutableList<Node<T>> = mutableListOf()

    fun addChild(value: T): Node<T> {
        val child = Node(value, this)
        children.add(child)
        return child
    }
}