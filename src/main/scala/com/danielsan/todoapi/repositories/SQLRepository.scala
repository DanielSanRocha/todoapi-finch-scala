package com.danielsan.todoapi.repositories

import com.twitter.finagle.mysql.{Result, Row}
import com.twitter.util.Future

trait SQLRepository[ModelType] {
  val tableName: String

  protected val createTableQuery: String

  protected def RowToModelType(row: Row): ModelType

  def createTable(): Future[Result]

  def getById(id: Long): Future[Option[ModelType]]
  def getAll(): Future[Seq[ModelType]]
}
