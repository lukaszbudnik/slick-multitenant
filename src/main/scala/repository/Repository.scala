package repository

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import scala.slick.jdbc.{StaticQuery => Q, SetParameter, GetResult}
import scala.Option
import model.BaseEntity
import org.joda.time.DateTime

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

  def selectNextVal(context: Context): Long = database withDynSession {
    Q.queryNA[Long](selectNextValStatement(context)).first
  }

  def insert(context: Context)(t: T): T = database withDynTransaction {
    insertInParentTx(context)(t)
  }

  def insertInParentTx(context: Context)(t: T) = {
    val nt = t.ref match {
      case None => {
        val ref = selectNextVal(context)
        t.withRef(ref).withTimestamp(DateTime.now())
      }
      case Some(ref) => t.withTimestamp(DateTime.now())
    }

    Q.update(insertStatement(context)).execute(nt)

    nt
  }

  def selectByRef(context: Context)(ref: Long): Option[T] = database withDynSession {
    Q.query[Long, T](selectByRefStatement(context)).firstOption(ref)
  }

  def selectByRefInParentTx(context: Context)(ref: Long): Option[T] = {
    Q.query[Long, T](selectByRefStatement(context)).firstOption(ref)
  }

  def delete(context: Context)(t: T): T = database withDynTransaction {
    deleteInParentTx(context)(t)
  }

  def deleteInParentTx(context: Context)(t: T): T = {
    insertInParentTx(context)(t.withDeleted(true))
  }

  implicit val getResult: GetResult[T]

  implicit val setParameter: SetParameter[T]

}
