package com.danielsan.todoapi.controllers

import com.danielsan.todoapi.models._
import com.twitter.finagle.mysql.Client
import com.danielsan.todoapi.repositories.{TodoRepository, TodoRepositoryImpl}
import io.finch._

object TodoController {
  def getEndpoints()(implicit client: Client) = {
    val repositoryImpl = new TodoRepositoryImpl
    val controller = new TodoController(repositoryImpl)

    controller.endpoints.handle {
      case e: Exception => InternalServerError(e)
    }
  }
}

class TodoController(repository: TodoRepository) {
  private val getTodo: Endpoint[Todo] = get("todo" :: path[Long]) { id: Long =>
    repository.getById(id) map {
      case Some(todo) => Ok(todo)
      case None       => NotFound(new Exception("Todo not found!"))
    }
  }

  private val getTodos: Endpoint[List[Todo]] = get("todos") {
    repository.getAll() map { todos =>
      Ok(todos.toList)
    }
  }

  val endpoints = getTodo :+: getTodos
}
