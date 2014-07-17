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
class ProductRepositorySpec extends Specification {


  val repository = new ProductRepository

  "Repository" should {

    "insert product into product table" in {
      val insertedFuture = repository.insert(PublicContext)(Product(None, None, "description", None))

      val inserted = Await.result(insertedFuture, Duration(10, TimeUnit.SECONDS))

      inserted.ref must beSome[Long]
      inserted.ts must beSome[DateTime]
    }

    "select product from product view" in {
      val insertedFuture = repository.insert(PublicContext)(Product(None, None, "description", None))

      val inserted = Await.result(insertedFuture, Duration(10, TimeUnit.SECONDS))

      inserted.ref must beSome[Long]
      inserted.ts must beSome[DateTime]

      val fetchedFuture = repository.selectByRef(PublicContext)(inserted.ref.get)

      val fetched = Await.result(fetchedFuture, Duration(10, TimeUnit.SECONDS))

      fetched must beSome(inserted)

      val notFoundFuture = repository.selectByRef(PublicContext)(-1)

      val notFound = Await.result(notFoundFuture, Duration(10, TimeUnit.SECONDS))

      notFound must beNone
    }

    "delete product by marking it as deleted" in {
      val insertedFuture = repository.insert(PublicContext)(Product(None, None, "description", None))

      val inserted = Await.result(insertedFuture, Duration(10, TimeUnit.SECONDS))

      inserted.ref must beSome[Long]
      inserted.ts must beSome[DateTime]
      inserted.deleted must beFalse

      val deletedFuture = repository.delete(PublicContext)(inserted)

      val deleted = Await.result(deletedFuture, Duration(10, TimeUnit.SECONDS))

      deleted.ref must beEqualTo(inserted.ref)
      deleted.ts.get.getMillis must beGreaterThan(inserted.ts.get.getMillis)
      deleted.deleted must beTrue

      val deletedNotReturnedFuture = repository.selectByRef(PublicContext)(deleted.ref.get)

      val deletedNotReturned = Await.result(deletedNotReturnedFuture, Duration(10, TimeUnit.SECONDS))

      deletedNotReturned must beNone
    }

  }

}