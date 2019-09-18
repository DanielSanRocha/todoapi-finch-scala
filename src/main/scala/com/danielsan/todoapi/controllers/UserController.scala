package com.danielsan.todoapi.controllers

import com.danielsan.todoapi.models._
import com.danielsan.todoapi.repositories.UserRepository
import io.finch._

class UserController(repository: UserRepository) {
  private val getUser: Endpoint[User] = get("user" :: path[Long]) { id: Long =>
    repository.getById(id) map {
      case Some(todo) => Ok(todo)
      case None       => NotFound(new Exception("User not found!"))
    }
  }

  private val getUsers: Endpoint[List[User]] = get("users") {
    repository.getAll() map { users =>
      Ok(users.toList)
    }
  }

  def getEndpoints() = (getUser :+: getUsers).handle {
    case e: Exception => InternalServerError(e)
  }
}
