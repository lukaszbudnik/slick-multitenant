package repository

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import model.Product
import org.joda.time.DateTime
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

@RunWith(classOf[JUnitRunner])
class TxManagerSpec extends Specification {


  val txManager = new TxManager
  val repository = new ProductRepository

  "TxManager" should {

    "commit transaction if all OK" in {
      val tupleFuture = txManager.executeInTx {
        val inserted1 = repository.doInsert(PublicContext)(Product(None, None, "description 123", None))
        val inserted2 = repository.doInsert(PublicContext)(Product(None, None, "description ABC", None))
        (inserted1, inserted2)
      }

      val (i1, i2) = Await.result(tupleFuture, Duration(10, TimeUnit.SECONDS))

      i1.ref must beSome[Long]
      i1.ts must beSome[DateTime]

      i2.ref must beSome[Long]
      i2.ts must beSome[DateTime]
    }

    "rollback transaction if an error occurred" in {
      var ref: Option[Long] = None

      val resultFuture = txManager.executeInTx {
        val inserted = repository.doInsert(PublicContext)(Product(None, None, "description 123", None))

        val fetched = repository.doSelectByRef(PublicContext)(inserted.ref.get)

        ref = fetched.get.ref

        throw new NullPointerException("just testing...")

        repository.insertInParentTx(PublicContext)(Product(None, None, "description ABC", None))
      }

      Await.ready(resultFuture, Duration(10, TimeUnit.SECONDS))

      ref must beSome[Long]

      val fetchedFuture = repository.selectByRef(PublicContext)(ref.get)

      val fetched = Await.result(fetchedFuture, Duration(10, TimeUnit.SECONDS))

      fetched must beNone
    }

  }

}