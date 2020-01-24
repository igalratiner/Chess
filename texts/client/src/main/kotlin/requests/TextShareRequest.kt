package requests

import pojo.TextRole

data class TextShareRequest(val textHash: String, val textRole: TextRole, val usernameToShareWith: String)