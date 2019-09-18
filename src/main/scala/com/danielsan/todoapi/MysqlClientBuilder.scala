package com.danielsan.todoapi

import java.net.InetSocketAddress

import com.twitter.finagle.Mysql
import com.twitter.finagle.mysql._
import com.twitter.util.Future
import com.typesafe.config._

class MySqlClientBuilder(conf: Config) {
  val host: String = conf.getString("mysql.host")
  val port: Int = conf.getInt("mysql.port")
  val user: String = conf.getString("mysql.user")
  val password: String = conf.getString("mysql.password")
  val db: String = conf.getString("mysql.db")

  lazy val url = new InetSocketAddress(host, port)

  private val client = Mysql.client
    .withCredentials(user, password)
    .withDatabase(db)
    .newRichClient("%s:%d".format(url.getHostName, url.getPort))

  def getClient: Client with Transactions = client

  def createTables: Future[Result] = {
    client.query("""
      CREATE TABLE IF NOT EXISTS `tb_users` (
         `id` bigint(20) NOT NULL AUTO_INCREMENT,
         `name` varchar(255) NOT NULL,
         `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
         `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         PRIMARY KEY (`id`)
         ) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8""")
  }
}
