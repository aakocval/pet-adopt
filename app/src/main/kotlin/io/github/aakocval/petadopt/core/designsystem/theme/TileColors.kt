package io.github.aakocval.petadopt.core.designsystem.theme

/**
 * Picks a stable tile index for a key (e.g. a pet id), so the same pet always gets the same
 * pastel color across launches, lists and screens. `String.hashCode` is specified by the JVM,
 * so the result never changes between runs or devices.
 */
fun tileIndexFor(
    key: String,
    tileCount: Int,
): Int {
    require(tileCount > 0) { "tileCount must be positive" }
    return (key.hashCode() and Int.MAX_VALUE) % tileCount
}
