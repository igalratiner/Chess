package security

private val ALPHANUMERIC_REGEX = """[a-zA-z0-9]*""".toRegex()

fun checkHash(hash: String, hashLength: Int? = null) : Boolean {
    return (hashLength == hash.length) && checkOnlyAlphaNumericChars(hash)
}

fun checkOnlyAlphaNumericChars(str: String) : Boolean {
    return ALPHANUMERIC_REGEX.matches(str)
}

