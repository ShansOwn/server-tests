package com.shansown.spring_webflux

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

/**
 * @author yehor.lashkul
 */
// WebFlux
object HttpClient {

  private val webFluxClient = WebClient
    .builder()
    .baseUrl("http://localhost:3000")
    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    .build()

  fun payload(payload: String): Mono<String> {
    return webFluxClient.post()
      .uri("/echo")
      .bodyValue(payload)
      .retrieve()
      .bodyToMono(String::class.java)
  }

  suspend fun payloadSuspend(payload: Any): String {
    return webFluxClient.post()
      .uri("/echo")
      .bodyValue(payload)
      .retrieve()
      .bodyToMono(String::class.java)
      .awaitSingle()
  }
}
