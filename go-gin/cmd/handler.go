package main

import (
	"bytes"
	"encoding/json"
	"io/ioutil"
	"log"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
)

type Handler interface {
	Echo(c *gin.Context)
	Network(c *gin.Context)
	DB(c *gin.Context)
	Mix(c *gin.Context)
}

type handler struct {
	rep ModelRepository
}

func NewHandler(rep ModelRepository) Handler {
	return &handler{rep}
}

func (h *handler) Echo(c *gin.Context) {
	b, err := c.GetRawData()

	if err != nil {
		log.Panic("Error", err)
	}

	log.Printf("---> %s", b)
	time.Sleep(delay * time.Millisecond)
	log.Printf("<--- %s", b)

	c.Data(http.StatusOK, gin.MIMEJSON, b)
}

func (h *handler) Network(c *gin.Context) {
	b, err := c.GetRawData()
	if err != nil {
		log.Panic("Get data error", err)
	}

	log.Printf("---> %s", b)
	resp, err := post(c.Request.Context(), bytes.NewReader(b))
	if err != nil {
		log.Panic("Post error", err)
	}
	//goland:noinspection GoUnhandledErrorResult
	defer resp.Body.Close()
	p, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Panic("Get data error", err)
	}
	log.Printf("<--- %s", p)

	c.Data(http.StatusOK, gin.MIMEJSON, p)
}

func (h *handler) DB(c *gin.Context) {
	var reqModel Model

	if err := json.NewDecoder(c.Request.Body).Decode(&reqModel); err != nil {
		log.Panic("Failed to parse reqModel")
	}

	ID := NewID()
	reqModel.ID = StrPtr(ID)
	if err := h.rep.Insert(c.Request.Context(), reqModel); err != nil {
		log.Panic("Failed to insert model")
	}

	respModel, err := h.rep.FindByID(c.Request.Context(), ID)
	if err != nil {
		log.Panic("Failed to fetch model")
	}

	c.JSON(http.StatusOK, respModel)
}

func (h *handler) Mix(c *gin.Context) {
	var reqModel Model

	if err := json.NewDecoder(c.Request.Body).Decode(&reqModel); err != nil {
		log.Panic("Failed to parse reqModel")
	}

	marshal, err := json.Marshal(reqModel)
	if err != nil {
		log.Panic("Marshal data error", err)
	}

	resp, err := post(c.Request.Context(), bytes.NewReader(marshal))
	if err != nil {
		log.Panic("Post error", err)
	}
	//goland:noinspection GoUnhandledErrorResult
	defer resp.Body.Close()
	p, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Panic("Get data error", err)
	}

	ID := NewID()
	reqModel.ID = StrPtr(ID)
	reqModel.Text = string(p)
	if err := h.rep.Insert(c.Request.Context(), reqModel); err != nil {
		log.Panic("Failed to insert model")
	}

	respModel, err := h.rep.FindByID(c.Request.Context(), ID)
	if err != nil {
		log.Panic("Failed to fetch model")
	}

	c.JSON(http.StatusOK, respModel)
}
