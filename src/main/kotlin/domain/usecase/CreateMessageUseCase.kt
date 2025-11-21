package com.example.com.domain.usecase

import com.example.com.domain.model.Message
import com.example.com.domain.repository.MessageRepository
import java.time.Instant
import java.util.UUID

class CreateMessageUseCase(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(content: String): Message {
        var id: String
        do {
            id = UUID.randomUUID().toString()
        } while (repository.findById(id) != null)

        val message = Message(
            id = id,
            content = content,
            createdAt = Instant.now()
        )

        return repository.save(message)
    }
}
