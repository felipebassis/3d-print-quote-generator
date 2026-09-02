package com.goat.infrastructure.scheduler

import com.goat.domain.exception.QuoteGeneratorException
import com.goat.domain.model.TaskStatus
import com.goat.domain.usecase.QuoteGeneratorUseCase
import com.goat.infrastructure.Constants
import com.goat.infrastructure.extensions.Loggable
import com.goat.infrastructure.persistence.repository.InstanceRepository
import com.goat.infrastructure.persistence.repository.TaskRepository
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
internal class QuoteGeneratorScheduler(
    private val quoteGenerator: QuoteGeneratorUseCase,
    taskRepository: TaskRepository,
    instanceRepository: InstanceRepository,
) : TaskStepScheduler(taskRepository, instanceRepository), Loggable {

    @Scheduled(every = "30s", delayed = "30s")
    fun generateQuote() =
        this.getTasksWithStatus(TaskStatus.GENERATING_QUOTE)
            .forEach {
                try {
                    quoteGenerator.generateQuote(it.stlDirectory, it.customer)
                    it.attempts = 1
                    logger.info("Quote generated successfully for task {}", it.id)
                } catch (exception: QuoteGeneratorException) {
                    logger.error("Error while attempting to generate Quote for task {}.", it.id, exception)
                    it.lastError = exception.message
                    it.attempts += 1
                } catch (exception: Exception) {
                    logger.error(
                        "Unexpected error while generating Quote for task {}. Task will not be reprocessed.",
                        it.id,
                        exception
                    )
                    it.lastError = exception.message
                    it.status = it.status.toError()
                } finally {
                    logger.debug("Finished processing Quote generation for task {}", it.id)
                    it.processingInstance = Constants.INSTANCE_ID
                    it.status = it.status.next()
                    taskRepository.save(it)
                }
            }
}