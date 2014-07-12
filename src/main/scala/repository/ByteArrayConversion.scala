package repository

import scala.slick.jdbc.{PositionedResult, PositionedParameters, GetResult, SetParameter}

trait ByteArrayConversion {

  implicit object SetByteArray extends SetParameter[Array[Byte]] {
    def apply(v: Array[Byte], pp: PositionedParameters) {
      pp.setBytes(v)
    }
  }

  // allows to save None as Option[Array[Byte]], https://github.com/slick/slick/issues/85
  implicit val setByteArrayOption: SetParameter[Option[Array[Byte]]] = SetParameter((v: Option[Array[Byte]], p) => v match {
    case Some(a) => p.setBytes(a)
    case None => p.setNull(java.sql.Types.BINARY)
  })

  implicit object GetByteArray extends GetResult[Array[Byte]] {
    def apply(rs: PositionedResult): Array[Byte] = rs.nextBytes()
  }

  implicit object GetByteArrayOption extends GetResult[Option[Array[Byte]]] {
    def apply(rs: PositionedResult): Option[Array[Byte]] = Option(rs.nextBytes)
  }

}