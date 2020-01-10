package com.anand.api.config

import com.typesafe.config.ConfigFactory

object Configuration {

  private val config = ConfigFactory.load()
  val host: String = config.getString("api.service.host")
  val port: Int = config.getInt("api.service.port")
  val requestTimeout: Int = config.getInt("api.service.request.timeout")
  val sinkKafkaTopic: String = config.getString("kafka.topic")

}
