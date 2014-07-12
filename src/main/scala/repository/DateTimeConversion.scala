package repository

import scala.slick.jdbc.{GetResult, SetParameter}
import org.joda.time.DateTime
import java.sql.Timestamp

trait DateTimeConvertion {

  implicit val timestamptToDateTime: Timestamp => DateTime = (t: Timestamp) => new DateTime(t.getTime)

  implicit val timestamptToDateTimeOption: Option[Timestamp] => Option[DateTime] = (t: Option[Timestamp]) => t.map(t => new DateTime(t.getTime))

  implicit val getResultTimestamp: GetResult[DateTime] = GetResult(r => new DateTime(r.nextTimestamp.getTime))

  implicit val getResultTimestampOption: GetResult[Option[DateTime]] = GetResult(r => r.nextTimestampOption.map(ts => new DateTime(ts.getTime)))

  implicit val setDateTime: SetParameter[DateTime] = SetParameter((dateTime: DateTime, p) => {
    p.setTimestamp(new Timestamp(dateTime.getMillis))
  })

  implicit val setDateTimeOption: SetParameter[Option[DateTime]] = SetParameter((dateTime: Option[DateTime], p) => {
    p.setTimestampOption(dateTime.map { dt =>
      new Timestamp(dt.getMillis)
    })
  })

}