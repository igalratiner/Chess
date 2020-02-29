package pojo

enum class TextRole {
    OWNER, EDITOR, READER;

    companion object {
        val writePrivilegedRoles = arrayOf(EDITOR, OWNER)
    }

    fun writePrivileges(): Boolean {
        if (this in writePrivilegedRoles) {
            return true
        }
        return false
    }
}