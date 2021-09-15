package com.shansown.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration


/**
 * @author yehor.lashkul
 */
object HttpClient {
  private val jsonMapper = ObjectMapper()

  private val client = WebClient
    .builder()
    .baseUrl("http://localhost:3000")
    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    .build()

  fun payload(payload: Any): String? {
    return client.post()
      .uri("/echo")
      .bodyValue(payload)
      .retrieve()
      .bodyToMono(String::class.java)
      .block(Duration.ofMinutes(1))
  }
}


