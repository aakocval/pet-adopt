package io.github.aakocval.petadopt.core.logging

/**
 * The app's only logging API (a Timber-style facade).
 *
 * - Debug builds log to logcat; release builds bind [NoOpLogger], so nothing is ever written to
 *   the device log in production (see the `debug` and `release` source sets).
 * - Never pass PII or secrets: no emails, names, tokens, passwords or full URLs. Log *what*
 *   happened ("photo fetch failed: SocketTimeoutException"), not *who* or *what data*.
 */
interface AppLogger {
    fun debug(
        tag: String,
        message: String,
    )

    fun warn(
        tag: String,
        message: String,
    )
}

/** Release builds and tests: logs nothing. */
object NoOpLogger : AppLogger {
    override fun debug(
        tag: String,
        message: String,
    ) = Unit

    override fun warn(
        tag: String,
        message: String,
    ) = Unit
}
