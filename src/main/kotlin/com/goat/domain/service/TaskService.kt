package com.goat.domain.service

import com.goat.domain.model.Customer
import com.goat.domain.model.Task
import com.goat.domain.usecase.TaskUseCase
import com.goat.infrastructure.extensions.Loggable
import com.goat.infrastructure.persistence.repository.FileRepository
import com.goat.infrastructure.persistence.repository.TaskRepository
import com.goat.infrastructure.web.controller.QuoteRequestDTO
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
internal class TaskService(
    private val taskRepository: TaskRepository,
    private val fileRepository: FileRepository
) : TaskUseCase, Loggable {


    override fun createTaskForGeneratingQuote(quoteRequestDTO: QuoteRequestDTO) {
        logger.info("Creating task for generation of a Quote: $quoteRequestDTO")
        val taskId = UUID.randomUUID()
        val stlDirectory = fileRepository.save(taskId, quoteRequestDTO.stlFiles.map {
            it.filePath()
        })
        taskRepository.save(Task(
            id = taskId,
            stlDirectory = stlDirectory,
            customer = Customer(
                customerName = quoteRequestDTO.customerName,
                customerPhone = quoteRequestDTO.customerPhone,
                customerEmail = quoteRequestDTO.customerEmail,
                customerZipCode = quoteRequestDTO.customerZipCode,
            )
        ))
        logger.info("Task for quote generation '{}' created", taskId)
    }
}
