package net.mct.kafka

import com.whisk.docker.{DockerContainer, DockerKit, DockerReadyChecker}

trait DockerKafkaService extends DockerKit {

  val KafkaAdvertisedPort = 9092
  val ZookeeperDefaultPort = 2181

  lazy val kafkaContainer: DockerContainer = DockerContainer("spotify/kafka")
    .withPorts(KafkaAdvertisedPort -> Some(KafkaAdvertisedPort), ZookeeperDefaultPort -> None)
    .withEnv(s"ADVERTISED_PORT=$KafkaAdvertisedPort", s"ADVERTISED_HOST=${dockerExecutor.host}")
    .withReadyChecker(DockerReadyChecker.LogLineContains("kafka entered RUNNING state"))

  abstract override def dockerContainers: List[DockerContainer] =
    kafkaContainer :: super.dockerContainers

}
