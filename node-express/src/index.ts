import express from "express"
import fetch from "node-fetch";
import {Repository} from "./Repository";
import {Model} from "./Model";
import {ulid} from 'ulid'

import cluster from "cluster"
import os from "os"

const numCPUs = os.cpus().length;

const initWorker = () => {
  const repository = new Repository()

  const app = express()
  app.use(express.json());
  const port = 3000

  app.post('/echo', (req, res) => {
    const body = req.body
    console.log(`---> ${JSON.stringify(body)}`)
    setTimeout(() => {
      console.log(`<--- ${JSON.stringify(body)}`)
      res.send(body)
    }, 100)
  })

  app.post('/network', async (req, res) => {
    const body = req.body
    console.log(`---> ${JSON.stringify(body)}`)
    const response = await fetch('http://localhost:3000/echo', {method: 'POST', body: JSON.stringify(body), headers: {'Content-Type': 'application/json'}})
    const json = await response.json()
    console.log(`<--- ${JSON.stringify(json)}`)
    res.send(json)
  })

  app.post('/db', async (req, res) => {
    const reqModel = req.body as Model
    reqModel.id = ulid()
    await repository.insert(reqModel)
    const respModel = await repository.findById(reqModel.id)
    res.send(respModel)
  })

  app.post('/mix', async (req, res) => {
    const reqModel = req.body as Model

    const response = await fetch('http://localhost:3000/echo', {method: 'POST', body: JSON.stringify(reqModel), headers: {'Content-Type': 'application/json'}})
    const json = await response.json()

    reqModel.id = ulid()
    reqModel.text = json as string
    await repository.insert(reqModel)
    const respModel = await repository.findById(reqModel.id)
    res.send(respModel)
  })

  app.listen(port, () => {
    console.log(`App listening at http://localhost:${port}`)
  })
}

if (cluster.isMaster) {
  console.log(`Primary ${process.pid} is running`);

  // Fork workers.
  for (let i = 0; i < numCPUs; i++) {
    cluster.fork();
  }

  cluster.on('exit', (worker, code, signal) => {
    console.log(`worker ${worker.process.pid} died`);
  });
} else {
  initWorker()
}
