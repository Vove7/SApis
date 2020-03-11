
fun <T : Comparable<T>> loop(vararg range: Iterable<T>, action: (List<T>) -> Unit) {
    if (range.isEmpty()) return
    startLoop(range.size, range, listOf(), action)
}

fun <T : Comparable<T>> startLoop(
        rawSize: Int,
        rs: Array<out Iterable<T>>,
        ps: List<T>,
        action: (List<T>) -> Unit
) {
    if (rs.size == 1) {
        rs[0].forEach {
            action(ps + it)
        }
        return
    }
    val tar = Array(rs.size - 1) {
        rs[it + 1]
    }
    rs[0].forEach {
        startLoop(rawSize, tar, ps + it, action)
    }
}

/**
 * 多层嵌套循环
 */
val cr = 'a'..'c'
fun main() {

    loop(1..2, 3..4) { (a, b) ->

        println(listOf(a,b))

    }
}