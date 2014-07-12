package repository

import scala.slick.jdbc.{SetParameter, GetResult}
import model.Product


class ProductRepository extends Repository[Product] {

  implicit val getResult: GetResult[Product] = GetResult(r => Product(r.<<, r.<<, r.<<, r.<<, r.<<))

  implicit val setParameter: SetParameter[Product] = SetParameter((p, w) => {
    w.>>(p.ref)
    w.>>(p.ts)
    w.>>(p.description)
    w.>>(p.specialCode)
    w.>>(p.deleted)
  })

}
