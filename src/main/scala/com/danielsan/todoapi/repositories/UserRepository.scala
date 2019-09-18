package com.danielsan.todoapi.repositories

import com.danielsan.todoapi.models.User
import com.twitter.finagle.mysql.{Client, LongValue, Row, StringValue}

trait UserRepository extends SQLRepository[User] {}
