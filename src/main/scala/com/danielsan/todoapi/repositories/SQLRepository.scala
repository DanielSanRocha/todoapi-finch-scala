package com.danielsan.todoapi.repositories

import com.twitter.finagle.mysql.{Client, Row}
import com.twitter.util.Future

trait SQLRepository[ModelType] {
  val tableName: String

  protected def RowToModelType(row: Row): ModelType

  def getById(id: Long): Future[Option[ModelType]]
  def getAll(): Future[Seq[ModelType]]
}

abstract class SQLRepositoryImpl[ModelType](implicit client: Client) extends SQLRepository[ModelType] {
  override def getById(id: Long): Future[Option[ModelType]] = {
    client.select(s"SELECT * FROM $tableName WHERE $tableName.id = $id")(RowToModelType) map (_.headOption)
  }

  override def getAll(): Future[Seq[ModelType]] = {
    client.select(s"SELECT * FROM $tableName")(RowToModelType)
  }
}
