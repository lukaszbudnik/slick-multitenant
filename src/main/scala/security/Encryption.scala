package security

import java.math.BigInteger

trait Encryption {

  implicit def stringEncryption(str: String): StringEncryption = new StringEncryption(str)

  implicit def stringOptionEncryption(str: Option[String]): StringOptionEncryption = new StringOptionEncryption(str)

  implicit def bigIntEncryption(bi: BigInt): BigIntEncryption = new BigIntEncryption(bi)

  implicit def bigIntOptionEncryption(bi: Option[BigInt]): BigIntOptionEncryption = new BigIntOptionEncryption(bi)

  class StringEncryption(str: String) {
    def enc: Array[Byte] = str.getBytes.reverse
  }

  class StringOptionEncryption(str: Option[String]) {
    def enc: Option[Array[Byte]] = str.map(_.getBytes.reverse)
  }

  class BigIntEncryption(bi: BigInt) {
    def enc: Array[Byte] = bi.toByteArray.reverse
  }

  class BigIntOptionEncryption(bi: Option[BigInt]) {
    def enc: Option[Array[Byte]] = bi.map(_.toByteArray.reverse)
  }

}

trait Decryption {

  implicit def byteArrayToString(data: Array[Byte]): ByteArrayStringDecryption = new ByteArrayStringDecryption(data)

  implicit def byteArrayOptionToString(data: Option[Array[Byte]]): ByteArrayOptionStringDecryption = new ByteArrayOptionStringDecryption(data)

  implicit def byteArrayToBigInt(data: Array[Byte]): ByteArrayBigIntDecryption = new ByteArrayBigIntDecryption(data)

  implicit def byteArrayOptionToBigInt(data: Option[Array[Byte]]): ByteArrayOptionBigIntDecryption = new ByteArrayOptionBigIntDecryption(data)

  class ByteArrayStringDecryption(data: Array[Byte]) {
    def decString: String = new String(data.reverse)
  }

  class ByteArrayOptionStringDecryption(data: Option[Array[Byte]]) {
    def decString: Option[String] = data.map(d => new String(d.reverse))
  }

  class ByteArrayBigIntDecryption(data: Array[Byte]) {
    def decBigInt: BigInt = new BigInt(new BigInteger(data.reverse))
  }

  class ByteArrayOptionBigIntDecryption(data: Option[Array[Byte]]) {
    def decBigInt: Option[BigInt] = data.map(d => new BigInt(new BigInteger(d.reverse)))
  }

}

