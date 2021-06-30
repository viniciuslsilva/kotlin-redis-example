package com.example

import io.ktor.application.*
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun Route.cache() {
    route("/test") {
        val cache = CacheService()

        get {
            cache.setValue(
                "TEST_KEY",
                "value",
                3
            )

            call.respond(cache.getValue("TEST_KEY") ?: "error :/")
        }
    }
}