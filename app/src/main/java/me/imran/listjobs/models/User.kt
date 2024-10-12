package me.imran.listjobs.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val username: String,
    val password: String,
    val recruiter: Boolean,
)
