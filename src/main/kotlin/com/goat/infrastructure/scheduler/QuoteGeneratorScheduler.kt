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
import org.slf4j.MDC

@ApplicationScoped
internal class QuoteGeneratorScheduler(
    private val quoteGenerator: QuoteGeneratorUseCase,
    taskRepository: TaskRepository,
    instanceRepository: InstanceRepository,
) : TaskStepScheduler(taskRepository, instanceRepository), Loggable {

    @Scheduled(every = "30s", delayed = "30s")
    fun generateQuote() =
        this.getTasksWithStatus(TaskStatus.GENERATING_QUOTE)
            .forEach { task ->
                MDC.putCloseable("taskId", task.id.toString()).use {
                    try {
                        quoteGenerator.generateQuote(task.id, task.stlDirectory, task.customer)
                        task.attempts = 1
                        logger.info("Quote generated successfully.")
                    } catch (exception: QuoteGeneratorException) {
                        logger.error("Error while attempting to generate Quote.", exception)
                        task.lastError = exception.message
                        task.attempts += 1
                    } catch (exception: Exception) {
                        logger.error(
                            "Unexpected error while generating Quote. Task will not be reprocessed.",
                            exception
                        )
                        task.lastError = exception.message
                        task.status = task.status.toError()
                    } finally {
                        logger.debug("Finished processing Quote generation.")
                        task.processingInstance = Constants.INSTANCE_ID
                        task.status = task.status.next()
                        taskRepository.save(task)
                    }
                }

            }
}