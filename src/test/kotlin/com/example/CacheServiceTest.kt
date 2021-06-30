package com.example

import io.ktor.util.KtorExperimentalAPI
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
@ExtendWith(MockKExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CacheServiceIT {
    private lateinit var cacheService: CacheService

    @BeforeAll
    fun initAll() {
        cacheService = CacheService()
    }

    /**
     * The current Redis cluster has 3 nodes, which means every node in a Redis Cluster is responsible for a subset of the hash slots.
     * According to the Redis documentation, the distribution of hash slots should be:
     * Node A contains hash slots from 0 to 5460.
     * Node B contains hash slots from 5461 to 10922.
     * Node C contains hash slots from 10923 to 16383.
     *
     * So, this test intend to make sure the data is stored across multiple nodes and the cache service is able to read them
     * properly.
     */
    @Test
    fun `Tests service when getting data from multiple nodes`() {
        runBlocking {
            for (i in 1..MAX_ITERATIONS) {
                cacheService.setValue("FEATURE_$i", "value_$i")
            }

            for (i in 1..MAX_ITERATIONS) {
                assertEquals("value_$i", cacheService.getValue("FEATURE_$i"))
            }
        }
    }

    /**
     * Check cache TTL.
     */
    @Test
    fun `Tests service when getting data from TTL cache`() {
        runBlocking {
            cacheService.setValue(
                "TEST_KEY",
                "value",
                TTL_SECONDS
            )

            assertEquals("value", cacheService.getValue("TEST_KEY"))
            delay(DELAY_MILIS)
            assertNull(cacheService.getValue("TEST_KEY"))
        }
    }

    companion object {
        const val MAX_ITERATIONS: Int = 20000
        const val TTL_SECONDS: Long = 3
        const val DELAY_MILIS: Long = 4000
    }
}
