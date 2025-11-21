package com.example.com.presentation.routes

import com.example.com.domain.usecase.CreateMessageUseCase
import com.example.com.domain.usecase.GetMessageByIdUseCase
import com.example.com.domain.usecase.ListMessagesUseCase
import com.example.com.presentation.models.CreateMessageRequest
import com.example.com.presentation.models.toResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.messageRoutes(
    listMessages: ListMessagesUseCase,
    getMessageById: GetMessageByIdUseCase,
    createMessage: CreateMessageUseCase
) {
    route("/messages") {
        get {
            val messages = listMessages().map { it.toResponse() }
            call.respond(messages)
        }

        post {
            val request = call.receive<CreateMessageRequest>()
            if (request.content.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Message content cannot be blank")
                return@post
            }

            val created = createMessage(
                content = request.content.trim()
            )
            call.respond(HttpStatusCode.Created, created.toResponse())
        }

        get("{id}") {
            val id = call.parameters["id"]?.takeIf { it.isNotBlank() }
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Message id is required")
                return@get
            }

            val message = getMessageById(id)
            if (message == null) {
                call.respond(HttpStatusCode.NotFound, "Message not found")
            } else {
                call.respond(message.toResponse())
            }
        }
    }
}
