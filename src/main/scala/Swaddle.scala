package org.conbere.swaddle

import com.fasterxml.jackson.databind.{ ObjectMapper, DeserializationFeature, SerializationFeature, MapperFeature }
import com.fasterxml.jackson.databind.cfg.ConfigFeature;
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonGenerator

import java.lang.reflect.{Type, ParameterizedType}
import com.fasterxml.jackson.core.`type`.TypeReference;

trait SwaddleLike extends Serializable {
  val mapper:ObjectMapper

  def buildMapper = MapperBuilder.create

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

object MapperBuilder {
  def create = {
    val m = new ObjectMapper()
    m.registerModule(DefaultScalaModule)
    new MapperBuilder(m)
  }
}

class MapperBuilder(val mapper:ObjectMapper) {
  def updateMapper(f:ObjectMapper => Unit) = {
    f(mapper)
    this
  }

  def configure(feature:SerializationFeature, state:Boolean) =
    updateMapper(_.configure(feature, state))

  def configure(feature:DeserializationFeature, state:Boolean) =
    updateMapper(_.configure(feature, state))

  def configure(feature:MapperFeature, state:Boolean) =
    updateMapper(_.configure(feature, state))

  def configure(feature:JsonParser.Feature, state:Boolean) =
    updateMapper(_.configure(feature, state))

  def configure(feature:JsonGenerator.Feature, state:Boolean) =
    updateMapper(_.configure(feature, state))


  def enable(feature:SerializationFeature) =
    updateMapper(_.enable(feature))

  def enable(feature:DeserializationFeature) =
    updateMapper(_.enable(feature))

  def disable(feature:SerializationFeature) =
    updateMapper(_.disable(feature))

  def disable(feature:DeserializationFeature) =
    updateMapper(_.disable(feature))

  def build = mapper
}

object Swaddle extends SwaddleLike {
  val mapper = buildMapper.build
}
