package main

import (
	"github.com/gin-gonic/gin"
	"log"
	core "shansown.com/go-gin/cmd"
)

func main() {
	r := router()
	h := core.NewHandler(core.NewRepository(core.DB()))

	r.POST("/echo", Echo(h))
	r.POST("/network", Network(h))
	r.POST("/db", DB(h))
	r.POST("/mix", Mix(h))

	err := r.Run(":8888")
	if err != nil {
		log.Fatal("Failed to start server: ", err)
	}
}

func router() (r *gin.Engine) {
	r = gin.New()
	r.Use(gin.Logger())
	r.Use(gin.Recovery())
	return
}
