package com.example


import io.lettuce.core.SocketOptions
import io.lettuce.core.TimeoutOptions
import io.lettuce.core.cluster.ClusterClientOptions
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withTimeout
import java.time.Duration

class CacheService {
    private val redisClusterClient: RedisClusterClient = RedisClusterClient.create("redis://localhost:7000")

    private var redis: RedisClusterAsyncCommands<String, String>? = null

    init {
        initClient()

        try {
            redis = connectRedisCluster()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun initClient() {
        val topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
            .enablePeriodicRefresh(true)
            .enableAllAdaptiveRefreshTriggers()
            .build()

        val socketOptions = SocketOptions.builder()
            .connectTimeout(Duration.ofMillis(1000))
            .build()

        val timeoutOptions = TimeoutOptions.builder()
            .timeoutCommands()
            .fixedTimeout(Duration.ofMillis(1000))
            .build()

        val clusterClientOptions = ClusterClientOptions.builder()
            .topologyRefreshOptions(topologyRefreshOptions)
            .autoReconnect(true)
            .requestQueueSize(1000)
            .socketOptions(socketOptions)
            .timeoutOptions(timeoutOptions)
            .build()

        redisClusterClient.setOptions(clusterClientOptions)
    }

    private fun connectRedisCluster(): RedisClusterAsyncCommands<String, String> {
        val connection = redisClusterClient.connect()
        return connection.async()
    }

    private fun reconnectIfNeeded() {
        if (redis == null) {
            redis = connectRedisCluster()
        }
    }

    suspend fun getValue(key: String): String? {
        return try {
            reconnectIfNeeded()
            withTimeout(1000) {
                redis?.get(key)?.await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun setValue(key: String, value: String, ttl: Long): String {
        return try {
            reconnectIfNeeded()
            withTimeout(1000) {
                redis?.setex(key, ttl, value)?.await()
                value
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun setValue(key: String, value: String): String {
        return try {
            reconnectIfNeeded()
            withTimeout(1000) {
                redis?.set(key, value)?.await()
                value
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
