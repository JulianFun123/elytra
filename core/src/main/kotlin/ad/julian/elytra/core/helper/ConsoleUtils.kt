package ad.julian.elytra.core.helper

fun logTable(headers: List<String>, rows: List<List<Any>>) {
    val columnWidths = headers.mapIndexed { i, h ->
        maxOf(h.length, rows.maxOfOrNull { it.getOrNull(i)?.toString()?.length ?: 0 } ?: 0)
    }

    fun formatRow(row: List<Any>) =
        row.mapIndexed { i, cell ->
            cell.toString().padEnd(columnWidths[i])
        }.joinToString(" | ", "| ", " |")

    val separator = "+" + columnWidths.joinToString("+") { "-".repeat(it + 2) } + "+"

    println(separator)
    println(formatRow(headers))
    println(separator)
    rows.forEach { println(formatRow(it)) }
    println(separator)
}