package com.goat.infrastructure.persistence.repository

interface LeaderRepository {

    fun updateToCurrentLeader(): Boolean

    fun isCurrentLeader(): Boolean
}