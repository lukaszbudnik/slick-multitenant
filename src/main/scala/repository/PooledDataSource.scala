package repository

import scala.slick.driver.JdbcDriver.backend.Database

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{HikariDataSource, HikariConfig}

trait PooledDatabase {
  lazy val database = {
    val ds = PooledDataSource.dataSource

    Database.forDataSource(ds)
  }
}

object PooledDataSource {

  lazy val config = ConfigFactory.load("config.properties")

  lazy val dataSource = {
    val hikariConfig = new HikariConfig()
    hikariConfig.setMaximumPoolSize(100)
    hikariConfig.setAutoCommit(false)
    hikariConfig.setDataSourceClassName(config.getString("db.dataSource"))
    hikariConfig.addDataSourceProperty("URL", config.getString("db.url"))
    hikariConfig.addDataSourceProperty("User", config.getString("db.user"))

    val ds = new HikariDataSource(hikariConfig)

    ds
  }

}
