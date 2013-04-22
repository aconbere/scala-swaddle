package org.conbere.swaddle

import com.fasterxml.jackson.databind.{ ObjectMapper, DeserializationFeature, SerializationFeature, MapperFeature }
import com.fasterxml.jackson.databind.cfg.ConfigFeature;
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonGenerator

import java.lang.reflect.{Type, ParameterizedType}
import com.fasterxml.jackson.core.`type`.TypeReference;

object SwaddleLike {
  def scalaMapper:ObjectMapper = {
    val m = new ObjectMapper()
    m.registerModule(DefaultScalaModule)
    m
  }
}

trait SwaddleLike extends Serializable {
  val mapper:ObjectMapper

  def update(m:ObjectMapper):SwaddleLike

  def updateMapper(f:ObjectMapper => ObjectMapper) = f(mapper.copy)



  def configure(feature:SerializationFeature, state:Boolean) =
    update(updateMapper { _.configure(feature, state) })

  def configure(feature:DeserializationFeature, state:Boolean) =
    update(updateMapper { _.configure(feature, state) })

  def configure(feature:MapperFeature, state:Boolean) =
    update(updateMapper { _.configure(feature, state) })

  def configure(feature:JsonParser.Feature, state:Boolean) =
    update(updateMapper { _.configure(feature, state) })

  def configure(feature:JsonGenerator.Feature, state:Boolean) =
    update(updateMapper { _.configure(feature, state) })



  def enable(feature:SerializationFeature) =
    update(updateMapper { _.enable(feature) })

  def enable(feature:DeserializationFeature) =
    update(updateMapper { _.enable(feature) })


  def disable(feature:SerializationFeature) =
    update(updateMapper { _.disable(feature) })

  def disable(feature:DeserializationFeature) =
    update(updateMapper { _.disable(feature) })


  def deserialize[T: Manifest](value: String) : T =
    mapper.readValue(value, typeReference[T])

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

class Swaddle(val mapper:ObjectMapper) extends SwaddleLike {
  def this() = this(SwaddleLike.scalaMapper)
  def update(m:ObjectMapper) = new Swaddle(m)
}
