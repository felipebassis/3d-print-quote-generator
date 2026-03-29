package com.goat.infrastructure.config

import com.goat.infrastructure.Constants.INSTANCE_ID
import com.goat.infrastructure.persistence.entity.InstanceEntity
import com.goat.infrastructure.persistence.repository.InstanceRepository
import com.goat.infrastructure.scheduler.LeaderElectionScheduler
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.transaction.Transactional
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.nio.file.Files
import kotlin.io.path.Path

@ApplicationScoped
internal class InstanceLifecycleManager(
    @param:ConfigProperty(name = "file-system.database-path")
    private val databasePath: String,
    private val instanceRepository: InstanceRepository,
    private val leaderElectionScheduler: LeaderElectionScheduler
) {

    @Suppress("unused")
    @Transactional(Transactional.TxType.REQUIRED)
    fun onStartup(@Observes startupEvent: StartupEvent) {
        if (Files.notExists(Path(databasePath))) {
            Files.createDirectories(Path(databasePath))
        }
        instanceRepository.save(InstanceEntity(instanceId = INSTANCE_ID))
        leaderElectionScheduler.beginElection()
    }

    @Suppress("unused")
    fun onShutdown(@Observes shutdownEvent: ShutdownEvent) {
        instanceRepository.markAsShuttingDown()
    }
}