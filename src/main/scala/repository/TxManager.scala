package repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TxManager extends PooledDatabase {

  def executeInTx[T](f: => T): Future[T] = Future {
    database withDynTransaction f
  }

}
