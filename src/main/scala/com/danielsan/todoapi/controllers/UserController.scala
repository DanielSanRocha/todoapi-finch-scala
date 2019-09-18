package com.danielsan.todoapi.controllers

import com.danielsan.todoapi.models._
import com.danielsan.todoapi.repositories.{TodoRepositoryImpl, UserRepository, UserRepositoryImpl}
import com.twitter.finagle.mysql.Client
import io.finch._

object UserController {
  def getEndpoints()(implicit client: Client) = {
    val repositoryImpl = new UserRepositoryImpl
    val controller = new UserController(repositoryImpl)

    controller.endpoints.handle {
      case e: Exception => InternalServerError(e)
    }
  }
}

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

  val endpoints = getUser :+: getUsers
}
