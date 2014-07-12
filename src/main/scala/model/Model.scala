package model

import org.joda.time.DateTime

trait BaseEntity[T <: BaseEntity[T]] {
  self: T =>
  val ref: Option[Long]
  val ts: Option[DateTime]
  val deleted: Boolean

  def withRef(ref: Long): T

  def withTimestamp(ts: DateTime): T

  def withDeleted(deleted: Boolean): T
}

case class Product(ref: Option[Long], ts: Option[DateTime], description: String, specialCode: Option[Int], deleted: Boolean = false) extends BaseEntity[Product] {

  def withRef(ref: Long) = copy(ref = Some(ref))

  def withTimestamp(ts: DateTime) = copy(ts = Some(ts))

  def withDeleted(deleted: Boolean) = copy(deleted = deleted)

}

case class Payment(ref: Option[Long], ts: Option[DateTime], description: String, who: String, amount: BigInt, deleted: Boolean = false) extends BaseEntity[Payment] {

  def withRef(ref: Long) = copy(ref = Some(ref))

  def withTimestamp(ts: DateTime) = copy(ts = Some(ts))

  def withDeleted(deleted: Boolean) = copy(deleted = deleted)

}
