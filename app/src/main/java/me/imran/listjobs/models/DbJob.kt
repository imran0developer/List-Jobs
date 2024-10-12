package me.imran.listjobs.models

import kotlinx.serialization.Serializable

@Serializable
data class DbJob(
    val id: Int,
    val title: String,
    val description: String,
    val is_remote: Boolean,
    val pay: Double,
    val location: String,
    val posted_by: String
)
