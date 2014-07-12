package repository

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession

class TxManager extends PooledDatabase {

  def executeInTx[T](f: => T): T = database withDynTransaction f

}
