package matej.lamza.betshops.common

sealed class ConnectionState

object Unavailable : ConnectionState()
object Available : ConnectionState()

