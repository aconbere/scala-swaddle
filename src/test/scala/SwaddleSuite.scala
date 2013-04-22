import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import org.conbere.swaddle.{ Swaddle, SwaddleLike }
import com.fasterxml.jackson.core.JsonParser

import scala.io.Source

case class SimpleCaseClass(foo:String, bar:String)

@RunWith(classOf[JUnitRunner])
class SwaddleSuite extends FunSuite {


  test("can parse json into maps") {
    val m = Map[String,Int]("x" -> 10)
    assert(Swaddle.deserialize[Map[String,Int]]("""{ "x": 10 }""") === m)
  }

  test("can config for leading zeros") {
    val m = Map[String,Int]("x" -> 10)

    val swaddle = new SwaddleLike {
      val mapper = buildMapper
        .configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true)
        .build
    }

    assert(swaddle.deserialize[Map[String,Int]]("""{ "x": 0010 }""") === m)
  }

  test("can parse json into case classes") {
    assert(Swaddle.deserialize[SimpleCaseClass]("""{"foo": "a", "bar": "b"}""") === SimpleCaseClass("a", "b"))
  }

  test("can parse incorrect single quotes") {
    var json = """{"x": "\'" }"""

    val swaddle = new SwaddleLike {
      val mapper = buildMapper
        .configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true)
        .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        .build
    }

    swaddle.deserialize[Map[String,Any]](json)
  }
}
