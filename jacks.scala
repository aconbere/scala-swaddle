import com.fasterxml.jackson.databind.{ ObjectMapper, DeserializationFeature, SerializationFeature }
import com.fasterxml.jackson.databind.cfg.ConfigFeature;
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.core.JsonParser

import java.lang.reflect.{Type, ParameterizedType}
import com.fasterxml.jackson.core.`type`.TypeReference;

trait JacksLike {
  val serializationFeatures:List[(SerializationFeature, Boolean)]
  val deserializationFeatures:List[(DeserializationFeature, Boolean)]
  val parserFeatures:List[(JsonParser.Feature, Boolean)]

  def deserialize[T: Manifest](value: String) : T =
    mapper.readValue(value, typeReference[T])

  lazy val mapper =  {
    val m = new ObjectMapper()

    m.registerModule(DefaultScalaModule)

    for ((feature, flag) <- serializationFeatures) { 
      m.configure(feature, flag)
    }

    for ((feature, flag) <- deserializationFeatures) { 
      m.configure(feature, flag)
    }

    for ((feature, flag) <- parserFeatures) { 
      m.configure(feature, flag)
    }

    m
  }

  private [this] def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private [this] def typeFromManifest(m: Manifest[_]): Type = {
      if (m.typeArguments.isEmpty) {
        m.runtimeClass 
      } else new ParameterizedType {
        def getRawType = m.runtimeClass

        def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

        def getOwnerType = null
      }
  }
}

class Jacks (
  val serializationFeatures:List[(SerializationFeature, Boolean)] = List(),
  val deserializationFeatures:List[(DeserializationFeature, Boolean)] = List(),
  val parserFeatures:List[(JsonParser.Feature, Boolean)] = List()
) extends JacksLike

object Main {
  val jacks = new Jacks()
  jacks.mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true)

  def deserialize[T:Manifest](in:String) = {
    println("in: " + in)
    val out = jacks.deserialize[T](in)
    println("out: " + out)
    out
  }

  def main(args:Array[String]) {
    deserialize[Map[String,Int]]("""{ "x": 10 }""")
    deserialize[Map[String,Int]]("""{ "x": 0010 }""")
  }
}
