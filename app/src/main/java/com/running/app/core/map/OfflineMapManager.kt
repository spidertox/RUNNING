package com.running.app.core.map

import android.content.Context
import com.running.app.core.database.AppDatabase
import com.running.app.core.database.OfflineRegion
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineMapManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase
) {
    // Demo: bundled MBTiles ko internal storage mein copy karta hai.
    // Production mein yahan apna tile server / download URL use karo.
    suspend fun importBundledRegion(
        assetName: String,
        regionId: String,
        name: String,
        centerLat: Double,
        centerLon: Double,
        minZoom: Int = 12,
        maxZoom: Int = 16
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val outFile = File(context.filesDir, "maps/$regionId.mbtiles")
            outFile.parentFile?.mkdirs()
            context.assets.open(assetName).use { input ->
                FileOutputStream(outFile).use { output -> input.copyTo(output) }
            }
            db.runDao().insertRegion(
                OfflineRegion(
                    id = regionId,
                    name = name,
                    centerLat = centerLat,
                    centerLon = centerLon,
                    minZoom = minZoom,
                    maxZoom = maxZoom,
                    filePath = outFile.absolutePath,
                    sizeBytes = outFile.length()
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getRegions() = db.runDao().getRegions()

    suspend fun deleteRegion(id: String) = withContext(Dispatchers.IO) {
        db.runDao().getRegions().let { }
        File(context.filesDir, "maps/$id.mbtiles").delete()
        db.runDao().deleteRegion(id)
    }
}
