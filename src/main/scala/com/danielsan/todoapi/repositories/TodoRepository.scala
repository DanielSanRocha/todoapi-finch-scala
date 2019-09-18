package com.danielsan.todoapi.repositories

import com.danielsan.todoapi.models.Todo
import com.twitter.finagle.mysql.{ByteValue, Client, LongValue, Row, StringValue}

trait TodoRepository extends SQLRepository[Todo] {}

class TodoRepositoryImpl(implicit client: Client) extends SQLRepositoryImpl[Todo] with TodoRepository {
  val tableName = "tb_todos"

  override def RowToModelType(row: Row): Todo = {
    val LongValue(id) = row("id").get
    val StringValue(title) = row("title").get
    val ByteValue(completed) = row("completed").get

    Todo(id, title, completed != 0)
  }
}
