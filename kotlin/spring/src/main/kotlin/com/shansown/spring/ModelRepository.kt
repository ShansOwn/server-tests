package com.shansown.spring

import com.shansown.common.Model
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

/**
 * @author yehor.lashkul
 */
@Repository
class ModelRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

  fun insert(model: Model) {
    jdbcTemplate.update(
      """
        INSERT INTO model(id, text) VALUES (:id, :text)
      """,
      MapSqlParameterSource()
        .addValue("id", model.id!!)
        .addValue("text", model.text)
    )
  }

  fun findById(id: String): Model? {
    return jdbcTemplate.query(
      """
        SELECT * FROM model WHERE id = :id
      """,
      mapOf("id" to id)
    ) { rs, rowNum ->
      Model(
        rs.getString("id"),
        rs.getString("text"),
        rs.getTimestamp("creation_date").toLocalDateTime()
      )
    }.firstOrNull()
  }

}
