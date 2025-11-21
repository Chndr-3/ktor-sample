package com.example.com.data.storage

import com.example.com.data.serialization.InstantIsoSerializer
import com.example.com.domain.model.Message
import com.example.com.domain.repository.MessageRepository
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.Instant
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class FileMessageRepository(
    private val storagePath: Path,
    private val json: Json = Json { prettyPrint = true }
) : MessageRepository {

    private val mutex = Mutex()

    override suspend fun list(): List<Message> = mutex.withLock {
        ensureFile()
        readMessages()
    }

    override suspend fun findById(id: String): Message? = mutex.withLock {
        ensureFile()
        readMessages().firstOrNull { it.id == id }
    }

    override suspend fun save(message: Message): Message = mutex.withLock {
        ensureFile()
        val messages = readMessages().toMutableList()
        messages.add(message)
        writeMessages(messages)
        message
    }

    private fun readMessages(): List<Message> {
        val raw = Files.readString(storagePath)
        if (raw.isBlank()) return emptyList()
        val persisted = json.decodeFromString(
            ListSerializer(PersistedMessage.serializer()),
            raw
        )
        return persisted.map { it.toDomain() }
    }

    private fun writeMessages(messages: List<Message>) {
        val persisted = messages.map { PersistedMessage.fromDomain(it) }
        val serialized = json.encodeToString(ListSerializer(PersistedMessage.serializer()), persisted)
        Files.writeString(
            storagePath,
            serialized,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        )
    }

    private fun ensureFile() {
        val dir = storagePath.parent
        if (dir != null && !Files.exists(dir)) {
            Files.createDirectories(dir)
        }
        if (!Files.exists(storagePath)) {
            Files.writeString(storagePath, "[]", StandardOpenOption.CREATE)
        }
    }

    @Serializable
    private data class PersistedMessage(
        val id: String,
        val content: String,
        @Serializable(with = InstantIsoSerializer::class)
        val createdAt: Instant
    ) {
        fun toDomain(): Message = Message(
            id = id,
            content = content,
            createdAt = createdAt
        )

        companion object {
            fun fromDomain(message: Message): PersistedMessage = PersistedMessage(
                id = message.id,
                content = message.content,
                createdAt = message.createdAt
            )
        }
    }
}
