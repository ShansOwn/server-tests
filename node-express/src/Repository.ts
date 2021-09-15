import {Model} from "./Model"
import {Pool} from "pg"
import {SQL} from "sql-template-strings"

const pool = new Pool({
  host: 'localhost',
  database: 'test',
  user: 'root',
  password: 'root',
  port: 5432,
  max: 10,
  min: 10,
  idleTimeoutMillis: 1800_000 // 30m
});

export class Repository {
  insert = async (model: Model) => {
    try {
      const res = await pool.query(
        SQL`INSERT INTO model(id, text) VALUES (${model.id}, ${model.text})`
      );

      for (let row of res.rows) {
        console.log(row);
      }
    } catch (err) {
      console.error(err);
    }
  }

  findById = async (id: String) => {
    try {
      const res = await pool.query(SQL`SELECT * FROM model WHERE id = ${id}`);

      for (let row of res.rows) {
        console.log(row);
      }
    } catch (err) {
      console.error(err);
    }
  }
}
