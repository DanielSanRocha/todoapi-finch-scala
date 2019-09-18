package com.danielsan.todoapi

import com.danielsan.todoapi.controllers._
import com.danielsan.todoapi.repositories.SQLRepositoriesImpl
import com.typesafe.config.{Config, ConfigFactory}
import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.finch.Application
import io.circe.generic.auto._
import io.finch.circe._

object TodoApplication extends TwitterServer {
  // Loading Configuration file
  implicit val conf: Config = ConfigFactory.load()
  val port = conf.getInt("api.port")

  // Mysql Database Client Configuration
  implicit val mySqlClient = new MySqlClientBuilder(conf).getClient

  // Loading repositories
  val sqlRepositories = new SQLRepositoriesImpl

  // Loading controllers
  lazy val todoController = new TodoController(sqlRepositories.TodoRepositoryImpl)
  lazy val userController = new UserController(sqlRepositories.UserRepositoryImpl)

  // Joining all endpoints
  lazy val api = todoController.getEndpoints() :+: userController.getEndpoints()

  def start(): Unit = {
//    // Preparing the server
    val server = Http.server
      .withStatsReceiver(statsReceiver)
      .serve(s":${port}", api.toServiceAs[Application.Json])
    closeOnExit(server)

    // Create the tables in the database
    sqlRepositories.createTables() foreach { query =>
      Await.result(query)
    }

    // Starting server
    Await.ready(server)
  }
}
