package me.y9san9.extensions.string


operator fun String.times(count: Int) = buildString {
    for(i in 1..count)
        append(this)
}
