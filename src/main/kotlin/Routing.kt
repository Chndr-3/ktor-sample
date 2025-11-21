package com.example.com

import com.example.com.data.storage.FileMessageRepository
import com.example.com.domain.usecase.CreateMessageUseCase
import com.example.com.domain.usecase.GetMessageByIdUseCase
import com.example.com.domain.usecase.ListMessagesUseCase
import com.example.com.presentation.routes.messageRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.nio.file.Paths
import kotlinx.serialization.json.Json

fun Application.configureRouting() {
    val storagePath = Paths.get(
        environment.config.propertyOrNull("storage.messagesPath")?.getString() ?: "data/messages.json"
    )

    val json = Json { prettyPrint = true }
    val repository = FileMessageRepository(storagePath, json)
    val listMessages = ListMessagesUseCase(repository)
    val getMessageById = GetMessageByIdUseCase(repository)
    val createMessage = CreateMessageUseCase(repository)

    routing {
        get("/") {
            call.respondText("Message service is running")
        }
        messageRoutes(
            listMessages = listMessages,
            getMessageById = getMessageById,
            createMessage = createMessage
        )
    }
}
