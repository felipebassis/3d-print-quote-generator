package com.goat.infrastructure.election.jpa.repository

interface LeaderRepository {

    fun updateToCurrentLeader(): Boolean

    fun isCurrentLeader(): Boolean
}