package com.running.app.data.repository

import com.running.app.core.database.Run
import com.running.app.core.database.RunDao
import com.running.app.core.database.TrackPoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunRepository @Inject constructor(private val dao: RunDao) {

    fun getAllRuns(): Flow<List<Run>> = dao.getAllRuns()

    suspend fun getPoints(runId: Long): List<TrackPoint> = dao.getPointsForRun(runId)

    suspend fun getRun(runId: Long): Run? = dao.getRun(runId)

    suspend fun deleteRun(runId: Long) = dao.deleteRun(runId)
}
