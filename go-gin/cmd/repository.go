package main

import (
	"context"
	"time"

	"github.com/jmoiron/sqlx"
)

type ModelRepository interface {
	Insert(ctx context.Context, model Model) error
	FindByID(ctx context.Context, id string) (*Model, error)
}

type Model struct {
	ID           *string    `json:"id" db:"id"`
	Text         string     `json:"text" db:"text"`
	CreationDate *time.Time `json:"creationDate" db:"creation_date"`
}

type modelRepository struct {
	db *sqlx.DB
}

func NewRepository(db *sqlx.DB) ModelRepository {
	return &modelRepository{db}
}

func (r *modelRepository) Insert(ctx context.Context, model Model) error {
	query := "INSERT INTO model(id, text) VALUES (:id, :text)"
	_, err := r.db.NamedExecContext(ctx, query,
		map[string]interface{}{
			"id":   model.ID,
			"text": model.Text,
		})
	return err
}

func (r *modelRepository) FindByID(ctx context.Context, id string) (*Model, error) {
	query := "SELECT * FROM model WHERE id = $1"
	model := Model{}
	if err := r.db.GetContext(ctx, &model, query, id); err != nil {
		return nil, err
	}
	return &model, nil
}
