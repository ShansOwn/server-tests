package core

import (
	"crypto/rand"
	"time"

	"github.com/oklog/ulid/v2"
)

func NewID() string {
	t := time.Now().UTC()
	entropy := rand.Reader
	return ulid.MustNew(ulid.Timestamp(t), entropy).String()
}
