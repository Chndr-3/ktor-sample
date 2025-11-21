package com.example.com.domain.model

import java.time.Instant

data class Message(
    val id: String,
    val content: String,
    val createdAt: Instant
)
