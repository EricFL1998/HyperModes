package com.banana.hypermodes.utils

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Serializable
data class UpdateInfo(
    val tag: String,
    val url: String,
    val changelog: String
)

object UpdateManager {
    private const val TAG = "UpdateManager"
    private const val GITHUB_API_URL = "https://api.github.com/repos/EricFL1998/HyperModes/releases/latest"
    
    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient()

    suspend fun fetchIfNewer(currentVersion: String): UpdateInfo? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(GITHUB_API_URL)
            .header("Accept", "application/vnd.github+json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                
                val body = response.body?.string() ?: return@withContext null
                val data = json.parseToJsonElement(body)
                
                val remoteTag = data.asMap()["tag_name"]?.toString()?.removeSurrounding("\"")?.replaceFirst("v", "") ?: ""
                
                if (remoteTag.isEmpty() || !isNewer(remoteTag, currentVersion)) {
                    null
                } else {
                    UpdateInfo(
                        tag = remoteTag,
                        url = data.asMap()["html_url"]?.toString()?.removeSurrounding("\"") ?: "",
                        changelog = data.asMap()["body"]?.toString()?.removeSurrounding("\"")?.replace("\\n", "\n")?.replace("\\r", "") ?: ""
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch update", e)
            null
        }
    }

    fun isNewer(remote: String, current: String): Boolean {
        val r = remote.split(".").mapNotNull { it.toIntOrNull() }
        val c = current.split(".").mapNotNull { it.toIntOrNull() }
        
        for (i in 0 until maxOf(r.size, c.size)) {
            val rv = r.getOrElse(i) { 0 }
            val cv = c.getOrElse(i) { 0 }
            if (rv > cv) return true
            if (rv < cv) return false
        }
        return false
    }
    
    private fun kotlinx.serialization.json.JsonElement.asMap() = 
        (this as? kotlinx.serialization.json.JsonObject) ?: emptyMap<String, kotlinx.serialization.json.JsonElement>()
}
