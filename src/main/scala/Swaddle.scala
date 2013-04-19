package org.conbere.swaddle

import com.fasterxml.jackson.databind.{ ObjectMapper, DeserializationFeature, SerializationFeature }
import com.fasterxml.jackson.databind.cfg.ConfigFeature;
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.core.JsonParser

import java.lang.reflect.{Type, ParameterizedType}
import com.fasterxml.jackson.core.`type`.TypeReference;

trait SwaddleLike {
  def deserialize[T: Manifest](value: String) : T =
    mapper.readValue(value, typeReference[T])

  lazy val mapper = {
    val m = new ObjectMapper()
    m.registerModule(DefaultScalaModule)
    m
  }

  private def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) {
      m.erasure 
    } else new ParameterizedType {
      def getRawType = m.erasure
      def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray
      def getOwnerType = null
    }
  }
}

class Swaddle extends SwaddleLike

object Main {
  val swaddle = new Swaddle()
  swaddle.mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true)

  def deserialize[T:Manifest](in:String) = {
    println("in: " + in)
    val out = swaddle.deserialize[T](in)
    println("out: " + out)
    out
  }

  def main(args:Array[String]) {
    deserialize[Map[String,Int]]("""{ "x": 10 }""")
    deserialize[Map[String,Int]]("""{ "x": 0010 }""")
  }
}
