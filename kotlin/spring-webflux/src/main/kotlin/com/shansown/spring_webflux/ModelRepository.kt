package com.shansown.spring_webflux

import com.shansown.common.Model
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * @author yehor.lashkul
 */
@Repository
class ModelRepository(private val client: DatabaseClient) {

  suspend fun insert(model: Model) {
    client.sql(
      """
        INSERT INTO model(id, text) VALUES (:id, :text)
      """
    )
      .bind("id", model.id!!)
      .bind("text", model.text)
      .then()
      .awaitFirstOrNull()
  }

  suspend fun findById(id: String): Model? {
    return client.sql(
      """
        SELECT * FROM model WHERE id = :id
      """
    )
      .bind("id", id)
      .map { row ->
        Model(
          row.get("id", String::class.java),
          row.get("text", String::class.java),
          row.get("creation_date", LocalDateTime::class.java)
        )
      }
      .first()
      .awaitFirstOrNull()
  }
}
