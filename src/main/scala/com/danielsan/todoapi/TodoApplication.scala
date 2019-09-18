package com.danielsan.todoapi

import com.danielsan.todoapi.controllers._

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

  // Database Configuration
  val mySqlClientBuilder = new MySqlClientBuilder(conf)
  implicit val mySqlClient = mySqlClientBuilder.getClient

  // Loading controllers endpoints
  lazy val todoEndpoints = TodoController.getEndpoints()
  lazy val userEndpoints = UserController.getEndpoints()
  lazy val api = todoEndpoints :+: userEndpoints

  def start(): Unit = {
//    // Preparing the server
    val server = Http.server
      .withStatsReceiver(statsReceiver)
      .serve(s":${port}", api.toServiceAs[Application.Json])
    closeOnExit(server)

    // Creating tables in the database
    Await.result(mySqlClientBuilder.createTables)

    // Starting server
    Await.ready(server)
  }
}
