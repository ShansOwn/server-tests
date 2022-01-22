package main

import (
	"encoding/json"
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
	"shansown.com/go-gin/cmd"
)

func Echo(h core.Handler) gin.HandlerFunc {
	return func(c *gin.Context) {
		b, err := c.GetRawData()
		if err != nil {
			log.Panic("Error", err)
		}

		h.Echo(c.Request.Context(), b)

		c.Data(http.StatusOK, gin.MIMEJSON, b)
	}
}

func Network(h core.Handler) gin.HandlerFunc {
	return func(c *gin.Context) {
		b, err := c.GetRawData()
		if err != nil {
			log.Panic("Get data error", err)
		}

		p := h.Network(c.Request.Context(), b)

		c.Data(http.StatusOK, gin.MIMEJSON, p)
	}
}

func DB(h core.Handler) gin.HandlerFunc {
	return func(c *gin.Context) {
		var req core.Model
		if err := json.NewDecoder(c.Request.Body).Decode(&req); err != nil {
			log.Panic("Failed to parse request")
		}

		result := h.DB(c.Request.Context(), req)

		c.JSON(http.StatusOK, result)
	}
}

func Mix(h core.Handler) gin.HandlerFunc {
	return func(c *gin.Context) {
		var req core.Model
		if err := json.NewDecoder(c.Request.Body).Decode(&req); err != nil {
			log.Panic("Failed to parse request")
		}

		result := h.Mix(c.Request.Context(), req)

		c.JSON(http.StatusOK, result)
	}
}
