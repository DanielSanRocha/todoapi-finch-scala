package com.danielsan.todoapi.controllers

import com.danielsan.todoapi.models._
import com.danielsan.todoapi.repositories.TodoRepository
import io.finch._

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

  def getEndpoints() = (getTodo :+: getTodos).handle {
    case e: Exception => InternalServerError(e)
  }
}
