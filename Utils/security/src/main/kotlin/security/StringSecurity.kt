package security

private val ALPHANUMERIC_REGEX = """[a-zA-z0-9]*""".toRegex()

private val UNSAFE_CHARS_REGEX = """[+§&@#$%^*!]+""".toRegex()

fun String.validHash(hashLength: Int? = null): Boolean {
    return (hashLength == length) && containsOnlyAlphaNumericChars()
}

fun String.containsOnlyAlphaNumericChars(): Boolean {
    return matches(ALPHANUMERIC_REGEX)
}

fun String.containsUnsecuredChars(): Boolean {
    return matches(UNSAFE_CHARS_REGEX)
}