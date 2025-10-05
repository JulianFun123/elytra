package ad.julian.elytra.protocol.helper

fun <T> T.mergeWith(other: T): T {
    val copyMethod = this!!::class.members.first { it.name == "copy" }
    val args = copyMethod.parameters.associateWith { param ->
        if (param.name == null) return@associateWith this // skip "this"
        val prop = this::class.members.first { it.name == param.name }
        val otherValue = (prop as kotlin.reflect.KProperty1<T, *>).get(other)
        otherValue ?: prop.get(this)
    }
    return copyMethod.callBy(args) as T
}