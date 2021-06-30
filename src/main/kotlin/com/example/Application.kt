package com.example

import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.KtorExperimentalAPI
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val logger: Logger = LogManager.getLogger("Application")

@KtorExperimentalAPI
fun main() {
    Application().start()
}

class Application {
    @KtorExperimentalAPI
    fun start() {
        var deployment: ApplicationEngine? = null

        try {
            logger.info("Started server!")

            deployment = embeddedServer(
                factory = Netty,
                environment = applicationEngineEnvironment {
                    module {
                        routing {
                            cache()
                        }
                    }
                    connector {
                        host = "0.0.0.0"
                        port = 8080
                    }
                }
            ).start(wait = true)
        } catch (t: Throwable) {
            logger.fatal("Failed to start server", t)
            throw t
        } finally {
            deployment?.stop(0, 10000)
        }

        logger.info("Server application ended!")
    }
}
