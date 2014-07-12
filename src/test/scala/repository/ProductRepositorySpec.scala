package repository

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import model.Product
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class ProductRepositorySpec extends Specification {


  val repository = new ProductRepository

  "Repository" should {

    "insert product into product table" in {
      val inserted = repository.insert(PublicContext)(Product(None, None, "description", None))

      inserted.ref must beSome[Long]
      inserted.ts must beSome[DateTime]
    }

    "select product from product view" in {
      val inserted = repository.insert(PublicContext)(Product(None, None, "description", None))

      inserted.ref must beSome[Long]
      inserted.ts must beSome[DateTime]

      val fetched = repository.selectByRef(PublicContext)(inserted.ref.get)

      fetched must beSome(inserted)

      val notFound = repository.selectByRef(PublicContext)(-1)

      notFound must beNone
    }

    "delete product by marking it as deleted" in {
      val inserted = repository.insert(PublicContext)(Product(None, None, "description", None))

      inserted.ref must beSome[Long]
      inserted.ts must beSome[DateTime]
      inserted.deleted must beFalse

      val deleted = repository.delete(PublicContext)(inserted)

      deleted.ref must beEqualTo(inserted.ref)
      deleted.ts.get.getMillis must beGreaterThan(inserted.ts.get.getMillis)
      deleted.deleted must beTrue

      val deletedNotReturned = repository.selectByRef(PublicContext)(deleted.ref.get)
      deletedNotReturned must beNone
    }

  }

}