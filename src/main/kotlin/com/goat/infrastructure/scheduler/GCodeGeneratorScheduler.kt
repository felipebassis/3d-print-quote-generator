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
import kotlin.io.path.Path

@ApplicationScoped
internal class GCodeGeneratorScheduler(
    private val gCodeGeneratorUseCase: GCodeGeneratorUseCase,
    private val taskRepository: TaskRepository,
    private val instanceRepository: InstanceRepository,
) : Loggable {

    @Scheduled(every = "15s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    fun generateGCode() {
        val instances = instanceRepository.findAllInstances()
        val pendingTasks = taskRepository.findAllTasksByStatus(TaskStatus.PENDING, TaskStatus.GENERATING_G_CODE)


        val currentTask: Task? = pendingTasks.find {
            it.processingInstance == null
                    && it.id.hashCode() % it.hashCode() % instances.size == instances.indexOfFirst { instance ->
                instance.instanceId == Constants.INSTANCE_ID
            }
        }

        if (currentTask == null) {
            logger.info("No pending tasks for this instance.")
            return
        }

        currentTask.status = currentTask.status.next()

        try {
            gCodeGeneratorUseCase.generateGCode(Path(currentTask.stlDirectory))
        } catch (exception: GCodeGeneratorException) {
            logger.error("Error while attempting to generate G-code for task {}.", currentTask.id, exception)
            currentTask.lastError = exception.message
        } catch (exception: Exception) {
            logger.error("Unexpected error while generating G-code for task {}. Task will not be reprocessed.", currentTask.id, exception)
            currentTask.lastError = exception.message
            currentTask.status = currentTask.status.toError()
        } finally {
            logger.info("Finished processing G-code generation for task {}", currentTask.id)
            currentTask.processingInstance = Constants.INSTANCE_ID
            currentTask.status = currentTask.status.next()
            currentTask.attempts = currentTask.attempts + 1
            taskRepository.save(currentTask)
        }

    }
}