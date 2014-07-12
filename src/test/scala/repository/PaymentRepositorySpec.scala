package repository

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import model.Payment
import org.joda.time.DateTime
import java.math.BigInteger
import org.h2.util.IOUtils
import java.io.FileReader

@RunWith(classOf[JUnitRunner])
class PaymentRepositorySpec extends Specification with PooledDatabase {


  val repository = new PaymentRepository

  "PaymentRepository" should {

    "insert payment into payment table" in {
      val inserted = repository.insert(TenantContext("a"))(Payment(None, None, "description", "Łukasz Budnik", new BigInt(new BigInteger("123"))))

      inserted.ref must beSome[Long]
      inserted.ts must beSome[DateTime]
    }

    "select payment from payment view" in {
      val inserted = repository.insert(TenantContext("a"))(Payment(None, None, "description", "Łukasz Budnik", new BigInt(new BigInteger("321"))))

      inserted.ref must beSome[Long]
      inserted.ts must beSome[DateTime]

      val fetched = repository.selectByRef(TenantContext("a"))(inserted.ref.get)

      fetched must beSome(inserted)
    }

  }

}
