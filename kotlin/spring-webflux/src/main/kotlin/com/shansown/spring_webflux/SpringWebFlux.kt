package com.shansown.spring_webflux

import com.shansown.common.Model
import com.shansown.common.delay
import de.huxhorn.sulky.ulid.ULID
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.ClassLoader.getSystemResource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.time.temporal.ChronoUnit

/**
 * @author yehor.lashkul
 */
@SpringBootApplication
class SpringWebFlux {

  // to start on netty as Spring pick ups Jetty from the classpath
  @Bean
  fun reactiveWebServerFactory(): ReactiveWebServerFactory {
    return NettyReactiveWebServerFactory()
  }
}

private val log = LoggerFactory.getLogger("SpringWebFlux")

fun main(args: Array<String>) {
  runApplication<SpringWebFlux>(*args)
}

@Configuration
@EnableR2dbcRepositories
class DB {

  @Bean
  fun ulid() = ULID()

  @Bean
  fun seeder(client: DatabaseClient): ApplicationRunner {
    return ApplicationRunner {
      getSchema()
        .flatMap { sql -> executeSql(client, sql) }
        .subscribe { log.info("Schema created") }
    }
  }

  private fun getSchema(): Mono<String> {
    val path: Path = Paths.get(getSystemResource("schema.sql").toURI())
    return Flux
      .using({ Files.lines(path) }, { Flux.fromStream(it) }, { it.close() })
      .reduce { line1, line2 -> "$line1\n$line2" }
  }

  private fun executeSql(client: DatabaseClient, sql: String): Mono<Int> {
    return client.sql(sql).fetch().rowsUpdated()
  }
}

@RestController
@Configuration
class Router(private val handler: Handler) {

  @Bean
  fun route() = router {
    POST("/echo") { request ->
      request.bodyToMono(String::class.java)
        .doOnEach { log.info("---> $it") }
        .delayElement(Duration.of(delay, ChronoUnit.MILLIS))
        .doOnEach { log.info("<--- $it") }
        .flatMap { ServerResponse.ok().bodyValue(it) }
    }
    POST("/network") { request ->
      request.bodyToMono(String::class.java)
        .doOnEach { log.info("---> $it") }
        .flatMap { HttpClient.payload(it) }
        .doOnEach { log.info("<--- $it") }
        .flatMap { ServerResponse.ok().bodyValue(it) }
    }
  }

  @Bean
  fun coRoute() = coRouter {
    POST("/echo-2", handler::echo)
    POST("/network-2", handler::network)
    POST("/db", handler::db)
    POST("/mix", handler::mix)
  }
}

@Component
class Handler(private val ulid: ULID, private val repository: ModelRepository) {

  suspend fun echo(request: ServerRequest): ServerResponse {
    val body = request.bodyToMono(String::class.java).awaitSingle()
    log.info("---> $body")
    delay(delay)
    log.info("<--- $body")
    return ServerResponse.ok().bodyValue(body).awaitSingle()
  }

  suspend fun network(request: ServerRequest): ServerResponse {
    val body = request.bodyToMono(String::class.java).awaitSingle()
    log.info("---> $body")
    val payload = HttpClient.payloadSuspend(body)
    log.info("<--- $payload")
    return ServerResponse.ok().bodyValue(payload).awaitSingle()
  }

  suspend fun db(request: ServerRequest): ServerResponse {
    val model = request.bodyToMono(Model::class.java).awaitSingle()
    val id = ulid.nextULID()
    repository.insert(model.copy(id = id))
    val savedModel = repository.findById(id) ?: throw RuntimeException("Couldn't get saved model")
    return ServerResponse.ok().bodyValue(savedModel).awaitSingle()
  }

  suspend fun mix(request: ServerRequest): ServerResponse {
    val model = request.bodyToMono(Model::class.java).awaitSingle()
    val payload = HttpClient.payloadSuspend(model)
    val id = ulid.nextULID()
    repository.insert(model.copy(id = id, text = payload))
    val savedModel = repository.findById(id) ?: throw RuntimeException("Couldn't get saved model")
    return ServerResponse.ok().bodyValue(savedModel).awaitSingle()
  }
}
