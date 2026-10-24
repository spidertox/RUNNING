package com.running.app.core.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<TrackPoint>)

    @Query("SELECT * FROM runs ORDER BY startTime DESC")
    fun getAllRuns(): Flow<List<Run>>

    @Query("SELECT * FROM track_points WHERE runId = :runId ORDER BY time ASC")
    suspend fun getPointsForRun(runId: Long): List<TrackPoint>

    @Query("SELECT * FROM runs WHERE id = :runId")
    suspend fun getRun(runId: Long): Run?

    @Query("DELETE FROM runs WHERE id = :runId")
    suspend fun deleteRun(runId: Long)

    @Query("UPDATE runs SET isSynced = 1 WHERE id = :runId")
    suspend fun markSynced(runId: Long)

    // Offline regions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegion(region: OfflineRegion)

    @Query("SELECT * FROM offline_regions")
    fun getRegions(): Flow<List<OfflineRegion>>

    @Query("DELETE FROM offline_regions WHERE id = :id")
    suspend fun deleteRegion(id: String)
}
