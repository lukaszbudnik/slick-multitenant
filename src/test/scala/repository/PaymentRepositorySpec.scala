package repository

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import model.Payment
import org.joda.time.DateTime
import java.math.BigInteger
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

@RunWith(classOf[JUnitRunner])
class PaymentRepositorySpec extends Specification with PooledDatabase {


  val repository = new PaymentRepository

  "PaymentRepository" should {

    "insert payment into psayment table" in {
      val insertedFuture = repository.insert(TenantContext("a"))(Payment(None, None, "description", "Łukasz Budnik", new BigInt(new BigInteger("123"))))

      val inserted = Await.result(insertedFuture, Duration(10, TimeUnit.SECONDS))

      inserted.ref must beSome[Long]
      inserted.ts must beSome[DateTime]
    }

    "select payment from payment view for correct tenant" in {
      // a tenant
      val insertedFuture = repository.insert(TenantContext("a"))(Payment(None, None, "description", "Łukasz Budnik", new BigInt(new BigInteger("321"))))

      val inserted = Await.result(insertedFuture, Duration(10, TimeUnit.SECONDS))

      inserted.ref must beSome[Long]
      inserted.ts must beSome[DateTime]

      val fetchedFuture = repository.selectByRef(TenantContext("a"))(inserted.ref.get)

      val fetched = Await.result(fetchedFuture, Duration(10, TimeUnit.SECONDS))

      fetched must beSome(inserted)

      // b tenant
      val notFoundFuture = repository.selectByRef(TenantContext("b"))(inserted.ref.get)

      val notFound = Await.result(notFoundFuture, Duration(10, TimeUnit.SECONDS))

      notFound must beNone
    }

  }

}
