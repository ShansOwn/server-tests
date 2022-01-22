package core

import (
	"log"
	"time"

	_ "github.com/jackc/pgx/v4/stdlib"
	"github.com/jmoiron/sqlx"
)

const connMaxLifetime = 30 * time.Minute
const maxOpenConns = 10
const maxIdleConns = 10

const psqlInfo = "host=localhost port=5432 user=root password=root dbname=test sslmode=disable"

func DB() (db *sqlx.DB) {
	db, err := sqlx.Connect("pgx", psqlInfo)
	if err != nil {
		log.Fatal("Can not connect to DB: ", err)
	}

	db.SetConnMaxLifetime(connMaxLifetime)
	db.SetMaxOpenConns(maxOpenConns)
	db.SetMaxIdleConns(maxIdleConns)

	return
}
