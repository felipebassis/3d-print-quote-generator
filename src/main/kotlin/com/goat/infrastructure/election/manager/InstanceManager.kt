package com.goat.infrastructure.election.manager

import com.goat.infrastructure.Constants.INSTANCE_ID
import com.goat.infrastructure.election.jpa.entity.InstanceEntity
import com.goat.infrastructure.election.jpa.repository.InstanceRepository
import com.goat.infrastructure.election.scheduler.LeaderElectionScheduler
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.event.Observes
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.nio.file.Files
import kotlin.io.path.Path

@Singleton
internal class InstanceManager(
    @param:ConfigProperty(name = "database-path")
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