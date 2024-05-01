package me.y9san9.extensions.string


fun String.awesomeCut(maxLength: Int, postfix: String = "…"): String {
    val result = take(maxLength)

    return if(result.length != length) result + postfix else result
}
