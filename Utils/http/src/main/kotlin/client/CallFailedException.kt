package client

import java.lang.RuntimeException

class CallFailedException(cause: String): RuntimeException(cause)