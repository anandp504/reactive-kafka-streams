package com.anand.api.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.routing.FromConfig
import akka.stream.ActorMaterializer
import com.anand.api.config.Configuration
import com.anand.sink.KafkaProducer

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object DataSyncApiService extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatchers.lookup("rest-dispatcher")

  private val kafkaProducerActor =
    system.actorOf(
      props = FromConfig.getInstance.props(KafkaProducer.props(materializer)),
      name = "kafkaProducerActor")

  val telemetryService = new ApiEndPoints(kafkaProducerActor)

  val bindingFuture = Http().bindAndHandle(telemetryService.telemetryServiceRoutes,
    interface = Configuration.host, port = Configuration.port)

  bindingFuture.onComplete {
    case Success(binding) ⇒
      println(s"API service is listening on localhost:8080")
    case Failure(e) ⇒
      println(s"Binding failed with ${e.getMessage}")
      system.terminate()
  }

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run() = {
      system.terminate()
    }
  })

}
