package com.shansown.ktor

import com.shansown.common.KtorClientApi
import com.shansown.common.delay
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay

/**
 * @author yehor.lashkul
 */
fun main(args: Array<String>) {
  embeddedServer(Netty, 8003) {
    routing {
      post("/echo") {
        val body = call.receiveText()
        log.info("---> $body")
        delay(delay)
        log.info("<-- $body")
        call.respondText(body, ContentType.Application.Json)
      }

      post("/network") {
        val payload = call.receiveText()
        log.info("---> $payload")
        val response: String = KtorClientApi.payload(payload)
        log.info("<-- $response")
        call.respondText(response, ContentType.Application.Json)
      }
    }
  }.start(wait = true)
}
