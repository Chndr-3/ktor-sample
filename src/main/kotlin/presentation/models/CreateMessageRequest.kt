package com.example.com.presentation.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateMessageRequest(
    val content: String
)
