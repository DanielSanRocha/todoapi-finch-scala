package com.danielsan.todoapi.repositories

import com.danielsan.todoapi.models.{Todo, User}
import com.twitter.finagle.mysql.{ByteValue, Client, LongValue, Result, Row, StringValue}
import com.twitter.util.Future

import scala.io.Source

class SQLRepositoriesImpl(implicit client: Client) {

  abstract class SQLRepositoryImpl[ModelType](implicit client: Client) extends SQLRepository[ModelType] {

    override def createTable(): Future[Result] = {
      client.query(createTableQuery)
    }

    def loadQueryFromFile(filename: String): String = {
      val createTableSQLFileStream = getClass.getResourceAsStream(s"/sqls/$filename")
      return Source.fromInputStream(createTableSQLFileStream).getLines().mkString(s"\n")
    }

    override def getById(id: Long): Future[Option[ModelType]] = {
      client.select(s"SELECT * FROM $tableName WHERE $tableName.id = $id")(RowToModelType) map (_.headOption)
    }

    override def getAll(): Future[Seq[ModelType]] = {
      client.select(s"SELECT * FROM $tableName")(RowToModelType)
    }
  }

  object TodoRepositoryImpl extends SQLRepositoryImpl[Todo] with TodoRepository {
    val tableName = "tb_todos"
    val createTableQuery = loadQueryFromFile("CreateTodoTable.sql")

    override def RowToModelType(row: Row): Todo = {
      val LongValue(id) = row("id").get
      val StringValue(title) = row("title").get
      val ByteValue(completed) = row("completed").get

      Todo(id, title, completed != 0)
    }
  }

  object UserRepositoryImpl extends SQLRepositoryImpl[User] with UserRepository {
    val tableName = "tb_users"
    val createTableQuery = loadQueryFromFile("CreateUserTable.sql")

    override def RowToModelType(row: Row): User = {
      val LongValue(id) = row("id").get
      val StringValue(name) = row("name").get

      User(id, name)
    }
  }

  def createTables(): Seq[Future[Result]] = Seq(UserRepositoryImpl.createTable(), TodoRepositoryImpl.createTable())
}
