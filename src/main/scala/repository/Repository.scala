package repository

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import scala.slick.jdbc.{StaticQuery => Q, SetParameter, GetResult}
import scala.Option
import model.BaseEntity
import org.joda.time.DateTime
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Context {
  def tenant: String
}

case object PublicContext extends Context {
  def tenant: String = "public"
}

case class TenantContext(tenant: String) extends Context

abstract class Repository[T <: BaseEntity[T]](implicit m: Manifest[T]) extends PooledDatabase with DateTimeConvertion with ByteArrayConversion {

  def table: String = m.runtimeClass.getSimpleName.toLowerCase

  def view: String = s"v_${table}"

  def sequence: String = s"${table}_ref_seq"

  def selectStatement(context: Context) = s"select * from ${context.tenant}.${view}"

  def selectByRefStatement(context: Context) = s"${selectStatement(context)} where ref = ?"

  def selectNextValStatement(context: Context) = s"select nextval('${context.tenant}.${sequence}')"

  def insertStatement(context: Context) = {
    val noOfFields = m.runtimeClass.getDeclaredFields.length
    val qm = Seq.fill(noOfFields)("?").mkString(",")
    s"insert into ${context.tenant}.${table} values (${qm})"
  }

  def selectNextVal(context: Context): Future[Long] = Future {
    doSelectNextVal(context)
  }

  private def doSelectNextVal(context: Context): Long = database withDynSession {
    Q.queryNA[Long](selectNextValStatement(context)).first
  }

  def insert(context: Context)(t: T): Future[T] = Future {
    database withDynTransaction {
      doInsert(context)(t)
    }
  }

  def insertInParentTx(context: Context)(t: T): Future[T] = Future {
    doInsert(context)(t)
  }

  private[repository] def doInsert(context: Context)(t: T): T = {
    val nt = t.ref match {
      case None => {
        val ref = doSelectNextVal(context)
        t.withRef(ref).withTimestamp(DateTime.now())
      }
      case Some(ref) => t.withTimestamp(DateTime.now())
    }

    Q.update(insertStatement(context)).execute(nt)
    nt
  }

  def selectByRef(context: Context)(ref: Long): Future[Option[T]] = Future {
    database withDynSession {
      doSelectByRef(context)(ref)
    }
  }

  def selectByRefInParentTx(context: Context)(ref: Long): Future[Option[T]] = Future {
    doSelectByRef(context)(ref)
  }

  private[repository] def doSelectByRef(context: Context)(ref: Long): Option[T] = Q.query[Long, T](selectByRefStatement(context)).firstOption(ref)

  def delete(context: Context)(t: T): Future[T] = Future {
    database withDynTransaction {
      doDelete(context)(t)
    }
  }

  def deleteInParentTx(context: Context)(t: T): Future[T] = Future {
    doDelete(context)(t)
  }

  private[repository] def doDelete(context: Context)(t: T) = doInsert(context)(t.withDeleted(true))

  implicit val getResult: GetResult[T]

  implicit val setParameter: SetParameter[T]

}
