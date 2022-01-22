package core

import (
	"bytes"
	"context"
	"encoding/json"
	"io/ioutil"
	"log"
	"time"
)

const delay = 100

type Handler interface {
	Echo(ctx context.Context, payload []byte)
	Network(ctx context.Context, payload []byte) []byte
	DB(ctx context.Context, payload Model) Model
	Mix(ctx context.Context, payload Model) Model
}

type handler struct {
	rep ModelRepository
}

func NewHandler(rep ModelRepository) Handler {
	return &handler{rep}
}

func (h *handler) Echo(ctx context.Context, payload []byte) {
	log.Printf("---> %s", payload)
	time.Sleep(delay * time.Millisecond)
	log.Printf("<--- %s", payload)
}

func (h *handler) Network(ctx context.Context, payload []byte) []byte {
	log.Printf("---> %s", payload)
	resp, err := post(ctx, bytes.NewReader(payload))
	if err != nil {
		log.Panic("post error", err)
	}

	defer func() { _ = resp.Body.Close() }()
	p, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Panic("Get data error", err)
	}
	log.Printf("<--- %s", p)

	return p
}

func (h *handler) DB(ctx context.Context, payload Model) Model {
	id := NewID()

	// save model
	payload.ID = StrPtr(id)
	if err := h.rep.Insert(ctx, payload); err != nil {
		log.Panic("Failed to insert model")
	}

	// select model by id
	resModel, err := h.rep.FindByID(ctx, id)
	if err != nil {
		log.Panic("Failed to select model")
	}

	return *resModel
}

func (h *handler) Mix(ctx context.Context, payload Model) Model {
	jsonPayload, err := json.Marshal(payload)
	if err != nil {
		log.Panic("Marshal data error", err)
	}

	resp, err := post(ctx, bytes.NewReader(jsonPayload))
	if err != nil {
		log.Panic("post error", err)
	}

	defer func() { _ = resp.Body.Close() }()
	p, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Panic("Get data error", err)
	}

	ID := NewID()
	payload.ID = StrPtr(ID)
	payload.Text = string(p)
	if err := h.rep.Insert(ctx, payload); err != nil {
		log.Panic("Failed to insert model")
	}

	resModel, err := h.rep.FindByID(ctx, ID)
	if err != nil {
		log.Panic("Failed to fetch model")
	}

	return *resModel
}
