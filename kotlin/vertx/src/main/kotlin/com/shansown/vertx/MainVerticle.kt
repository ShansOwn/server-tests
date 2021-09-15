package com.shansown.vertx

import com.shansown.common.delay
import io.vertx.core.http.HttpHeaders
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val log = LoggerFactory.getLogger("Vertx")

class MainVerticle : CoroutineVerticle() {

  override suspend fun start() {

    val router = Router.router(vertx)
    val client = Client(WebClient.create(vertx))

    // This body handler will be called for all routes
    router.route().handler(BodyHandler.create())

    coroutineHandler(router.post("/echo")) { ctx: RoutingContext ->
      val body = ctx.bodyAsString
      log.info("---> $body")
      delay(delay)
      log.info("<-- $body")
      val res = ctx.response()
      // apply the content type header
      res.putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
      res.end(body)
    }

    coroutineHandler(router.post("/network")) { ctx: RoutingContext ->
      val payload = ctx.bodyAsString
      log.info("---> $payload")
      val response = client.payload(payload)
      log.info("<-- $response")
      val res = ctx.response()
      res.putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
      res.end(response)
    }

    val server = vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8005)
      .await()
    log.info("server started on port ${server.actualPort()}")
  }

  private fun coroutineHandler(route: Route, handler: suspend (RoutingContext) -> Unit) {
    route.handler { ctx ->
      GlobalScope.launch(ctx.vertx().dispatcher()) {
        try {
          handler(ctx)
        } catch (e: Exception) {
          log.error("Coroutine handler failed: $e")
          ctx.fail(e)
        }
      }
    }
  }
}
