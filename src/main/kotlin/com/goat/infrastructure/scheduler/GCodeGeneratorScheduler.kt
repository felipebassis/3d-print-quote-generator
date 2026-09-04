package com.goat.infrastructure.scheduler

import com.goat.domain.exception.GCodeGeneratorException
import com.goat.domain.model.Task
import com.goat.domain.model.TaskStatus
import com.goat.domain.usecase.GCodeGeneratorUseCase
import com.goat.infrastructure.Constants
import com.goat.infrastructure.extensions.Loggable
import com.goat.infrastructure.persistence.repository.InstanceRepository
import com.goat.infrastructure.persistence.repository.TaskRepository
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import org.slf4j.MDC
import kotlin.io.path.Path

@ApplicationScoped
internal class GCodeGeneratorScheduler(
    private val gCodeGeneratorUseCase: GCodeGeneratorUseCase,
    taskRepository: TaskRepository,
    instanceRepository: InstanceRepository,
) : TaskStepScheduler(taskRepository, instanceRepository), Loggable {

    @Scheduled(every = "15s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    fun generateGCode() {
        val currentTask = taskRepository.findAllTasksByStatus(TaskStatus.PENDING, TaskStatus.GENERATING_G_CODE)
            .firstOrNull()

        if (currentTask == null) {
            logger.info("No pending tasks for this instance.")
            return
        }

        currentTask.status = currentTask.status.next()

        MDC.putCloseable("taskId", currentTask.id.toString()).use {
            try {
                gCodeGeneratorUseCase.generateGCode(currentTask.stlDirectory)
                currentTask.attempts = 1
                logger.info("G-code generated successfully")
            } catch (exception: GCodeGeneratorException) {
                logger.error("Error while attempting to generate G-code.", exception)
                currentTask.lastError = exception.message
                currentTask.attempts += 1
            } catch (exception: Exception) {
                logger.error(
                    "Unexpected error while generating G-code. Task will not be reprocessed.",
                    exception
                )
                currentTask.lastError = exception.message
                currentTask.status = currentTask.status.toError()
            } finally {
                logger.debug("Finished processing G-code generation")
                currentTask.processingInstance = Constants.INSTANCE_ID
                currentTask.status = currentTask.status.next()
                taskRepository.save(currentTask)
            }
        }
    }
}