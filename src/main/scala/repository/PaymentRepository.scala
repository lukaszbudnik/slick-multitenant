package repository

import scala.slick.jdbc.{SetParameter, GetResult}
import model.Payment
import security.{Encryption, Decryption}


class PaymentRepository extends Repository[Payment] with Encryption with Decryption {

  implicit val getResult: GetResult[Payment] = {
    GetResult(r => Payment(r.<<, r.<<, r.nextBytes.decString, r.nextBytes.decString, r.nextBytes.decBigInt, r.<<))
  }

  implicit val setParameter: SetParameter[Payment] = SetParameter((p, w) => {
    w.>>(p.ref)
    w.>>(p.ts)
    w.>>(p.description.enc)
    w.>>(p.who.enc)
    w.>>(p.amount.enc)
    w.>>(p.deleted)
  })

}
