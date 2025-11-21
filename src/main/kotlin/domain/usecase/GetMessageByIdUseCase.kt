package com.example.com.domain.usecase

import com.example.com.domain.model.Message
import com.example.com.domain.repository.MessageRepository

class GetMessageByIdUseCase(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(id: String): Message? = repository.findById(id)
}
