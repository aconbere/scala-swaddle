import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import org.conbere.swaddle.Swaddle
import com.fasterxml.jackson.core.JsonParser

@RunWith(classOf[JUnitRunner])
class SwaddleSuite extends FunSuite {
  test("can parse json into maps") {
    val swaddle = new Swaddle()
    swaddle.mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true)

    val m = Map[String,Int]("x" -> 10)

    assert(swaddle.deserialize[Map[String,Int]]("""{ "x": 10 }""") === m)
    assert(swaddle.deserialize[Map[String,Int]]("""{ "x": 0010 }""") === m)
  }
}
