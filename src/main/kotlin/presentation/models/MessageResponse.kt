package com.example.com.presentation.models

import com.example.com.domain.model.Message
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val id: String,
    val content: String,
    val createdAt: String
)

fun Message.toResponse(): MessageResponse = MessageResponse(
    id = id,
    content = content,
    createdAt = createdAt.toString()
)
