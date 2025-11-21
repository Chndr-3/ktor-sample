package com.example.com.domain.repository

import com.example.com.domain.model.Message

interface MessageRepository {
    suspend fun list(): List<Message>
    suspend fun findById(id: String): Message?
    suspend fun save(message: Message): Message
}
