package com.shansown.vertx

import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.await

/**
 * @author yehor.lashkul
 */
class Client(private val webClient: WebClient) {

  suspend fun payload(payload: String): String {
    return webClient.post(3000, "localhost", "/echo")
      .sendJson(payload)
      .await()
      .bodyAsString()
  }

}
