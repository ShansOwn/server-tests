package com.shansown.javalin

import com.shansown.common.KtorClientApi
import com.shansown.common.delay
import com.shansown.common.retrofitClientApi
import io.javalin.Javalin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

/**
 * @author yehor.lashkul
 */
private val log = LoggerFactory.getLogger("Javalin")

fun main(args: Array<String>) {
  val app = Javalin.create().start(8002)
  app.post("/echo-sync") { ctx ->
    log.info("---> ${ctx.body()}")
    Thread.sleep(delay)
    log.info("<--- ${ctx.body()}")
    ctx.result(ctx.body())
  }

  app.post("/echo-async") { ctx ->
    log.info("---> ${ctx.body()}")
    runBlocking {
      delay(delay)
      log.info("<--- ${ctx.body()}")
      ctx.result(ctx.body())
    }
  }

  app.post("/network-sync") { ctx ->
    val payload = ctx.body()
    log.info("---> $payload")
    val response: String? = retrofitClientApi.payloadSync(payload).execute().body()
    log.info("<--- $response")
    ctx.result(response ?: "NO RESPONSE")
  }

  app.post("/network-async") { ctx ->
    val payload = ctx.body()
    log.info("---> $payload")

    ctx.result(GlobalScope.future {
      val response: String = KtorClientApi.payload(payload)
      log.info("<--- $response")
      response
    })

  }
}
