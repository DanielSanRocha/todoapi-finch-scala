package com.danielsan.todoapi.repositories

import com.danielsan.todoapi.models.Todo
import com.twitter.finagle.mysql.{ByteValue, Client, LongValue, Row, StringValue}

trait TodoRepository extends SQLRepository[Todo] {}
