package main

import (
	"context"
	"io"
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
)

const delay = 100

func main() {
	r := router()
	h := NewHandler(NewRepository(Init()))

	r.POST("/echo", h.Echo)
	r.POST("/network", h.Network)
	r.POST("/db", h.DB)
	r.POST("/mix", h.Mix)

	err := r.Run(":8888")
	if err != nil {
		log.Fatal("Failed to start server: ", err)
	}
}

func post(ctx context.Context, body io.Reader) (*http.Response, error) {
	req, err := http.NewRequestWithContext(ctx, http.MethodPost, "http://localhost:3000/echo", body)
	if err != nil {
		return nil, err
	}

	req.Header.Set("Content-Type", "application/json")
	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		return nil, err
	}

	return resp, nil
}

func router() (r *gin.Engine) {
	r = gin.New()
	r.Use(gin.Logger())
	r.Use(gin.Recovery())
	return
}
