import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import org.conbere.swaddle.Swaddle
import com.fasterxml.jackson.core.JsonParser

case class SimpleCaseClass(foo:String, bar:String)

@RunWith(classOf[JUnitRunner])
class SwaddleSuite extends FunSuite {
  val swaddle = new Swaddle()
  swaddle.mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true)

  test("can parse json into maps") {
    val m = Map[String,Int]("x" -> 10)

    assert(swaddle.deserialize[Map[String,Int]]("""{ "x": 10 }""") === m)
    assert(swaddle.deserialize[Map[String,Int]]("""{ "x": 0010 }""") === m)
  }

  test("can parse json into case classes") {
    assert(swaddle.deserialize[SimpleCaseClass]("""{"foo": "a", "bar": "b"}""") === SimpleCaseClass("a", "b"))
  }
}
