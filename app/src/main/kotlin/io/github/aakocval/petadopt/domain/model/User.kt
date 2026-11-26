package io.github.aakocval.petadopt.domain.model

/** The signed-in user. Holds no secrets: tokens are a data-layer concern. */
data class User(
    val displayName: String,
    val email: String,
)
