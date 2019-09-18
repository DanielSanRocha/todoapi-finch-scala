package com.danielsan.todoapi.repositories

import com.danielsan.todoapi.models.User
import com.twitter.finagle.mysql.{Client, LongValue, Row, StringValue}

trait UserRepository extends SQLRepository[User] {}

class UserRepositoryImpl(implicit client: Client) extends SQLRepositoryImpl[User] with UserRepository {
  val tableName = "tb_users"

  override def RowToModelType(row: Row): User = {
    val LongValue(id) = row("id").get
    val StringValue(name) = row("name").get

    User(id, name)
  }
}
