package repository

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import model.Product
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class TxManagerSpec extends Specification {


  val txManager = new TxManager
  val repository = new ProductRepository

  "TxManager" should {

    "commit transaction if all OK" in {
      val (i1, i2) = txManager.executeInTx {
        val inserted1 = repository.insert(PublicContext)(Product(None, None, "description 123", None))
        val inserted2 = repository.insert(PublicContext)(Product(None, None, "description ABC", None))
        (inserted1, inserted2)
      }

      i1.ref must beSome[Long]
      i1.ts must beSome[DateTime]

      i2.ref must beSome[Long]
      i2.ts must beSome[DateTime]
    }

    "rollback transaction if an error occurred" in {
      var ref: Option[Long] = None

      try {

        txManager.executeInTx {
          val inserted = repository.insertInParentTx(PublicContext)(Product(None, None, "description 123", None))

          val fetched = repository.selectByRefInParentTx(PublicContext)(inserted.ref.get)

          ref = fetched.get.ref

          throw new NullPointerException("just testing...")

          repository.insertInParentTx(PublicContext)(Product(None, None, "description ABC", None))
        }

      } catch {
        case npe: NullPointerException if npe.getMessage == "just testing..." =>
        case ex: Exception => failure(s"Didn't expect exception ${ex}")
      }

      ref must beSome[Long]

      val fetched = repository.selectByRef(PublicContext)(ref.get)

      fetched must beNone
    }

  }

}