package com.example.com.domain.usecase

import com.example.com.domain.model.Message
import com.example.com.domain.repository.MessageRepository

class ListMessagesUseCase(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(): List<Message> = repository.list()
}
