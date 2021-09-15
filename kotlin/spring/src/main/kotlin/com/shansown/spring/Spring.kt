package com.shansown.spring

import com.shansown.common.Model
import com.shansown.common.delay
import de.huxhorn.sulky.ulid.ULID
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author yehor.lashkul
 */
@SpringBootApplication
class Spring

private val log = LoggerFactory.getLogger("Spring")

fun main(args: Array<String>) {
  runApplication<Spring>(*args)
}

@Configuration
class DB {

  @Bean
  fun ulid() = ULID()

  @Bean
  fun seeder(jdbcTemplate: JdbcTemplate): ApplicationRunner {
    return ApplicationRunner {
      getSchema()?.let { sql -> executeSql(jdbcTemplate, sql) }
      log.info("Schema created")
    }
  }

  private fun getSchema(): String? {
    val path: Path = Paths.get(ClassLoader.getSystemResource("schema.sql").toURI())
    return Files.lines(path).use { stream ->
      stream.reduce { line1, line2 -> "$line1\n$line2" }
    }.orElse(null)
  }

  private fun executeSql(jdbcTemplate: JdbcTemplate, sql: String) {
    jdbcTemplate.execute(sql)
  }
}

@Configuration
@RestController
class Controller(private val ulid: ULID, private val repository: ModelRepository) {

  @PostMapping("/echo")
  fun echo(@RequestBody body: String): ResponseEntity<String> {
    log.info("---> $body")
    Thread.sleep(delay)
    log.info("<--- $body")
    return ResponseEntity.ok().body(body)
  }

  @PostMapping("/network")
  fun network(@RequestBody body: String): ResponseEntity<String> {
    log.info("---> $body")
    val payload = HttpClient.payload(body)
    log.info("<--- $payload")
    return ResponseEntity.ok().body(payload)
  }

  @PostMapping("/db")
  fun db(@RequestBody model: Model): ResponseEntity<Model> {
    val id = ulid.nextULID()
    repository.insert(model.copy(id = id))
    val savedModel = repository.findById(id) ?: throw RuntimeException("Couldn't get saved model")
    return ResponseEntity.ok().body(savedModel)
  }

  @PostMapping("/mix")
  fun mix(@RequestBody model: Model): ResponseEntity<Model> {
    val payload = HttpClient.payload(model) ?: ""
    val id = ulid.nextULID()
    repository.insert(model.copy(id = id, text = payload))
    val savedModel = repository.findById(id) ?: throw RuntimeException("Couldn't get saved model")
    return ResponseEntity.ok().body(savedModel)
  }

}
