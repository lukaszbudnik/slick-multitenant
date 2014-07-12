package repository

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RepositorySqlSpec extends Specification {

  val repository = new ProductRepository

  "Repository" should {

    "translate type T to table name in lowercase" in {
      repository.table must equalTo("product")
    }

    "translate type T to view name in lowercase" in {
      repository.view must equalTo("v_product")
    }

    "translate type T to sequence ref lowercase" in {
      repository.sequence must equalTo("product_ref_seq")
    }

    "generate SQL select statement using v_product" in {
      repository.selectStatement(PublicContext) must equalTo("select * from public.v_product")
    }

    "generate SQL insert statement using product table" in {
      repository.insertStatement(PublicContext) must equalTo("insert into public.product values (?,?,?,?,?)")
    }

    "generate SQL next val sequence" in {
      repository.selectNextValStatement(PublicContext) must equalTo("select nextval('public.product_ref_seq')")
    }

    "generate SQL select statement using v_product with ref" in {
      repository.selectByRefStatement(PublicContext) must equalTo("select * from public.v_product where ref = ?")
    }

  }

}