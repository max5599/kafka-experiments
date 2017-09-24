package net.mct.kafka

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.whisk.docker.impl.spotify.DockerKitSpotify
import com.whisk.docker.scalatest.DockerTestKit
import net.manub.embeddedkafka.EmbeddedKafka
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._

class DockerKafkaServiceSpec extends FlatSpec with Matchers with DockerTestKit with DockerKitSpotify with DockerKafkaService with EmbeddedKafka {


  "DockerKafkaService" should "run a Kafka" in {
    val topic = "my-topic"
    produce(topic, "1", "Hello")

    val res = consume(topic)

    res shouldBe Some("1" -> "Hello")
  }

  private def produce(topic: String, key: String, value: String): Unit = {
    val props = new Properties()
    props.put("bootstrap.servers", s"localhost:$KafkaAdvertisedPort")
    props.put("acks", "all")
    props.put("retries", "0")
    props.put("batch.size", "16384")
    props.put("linger.ms", "1")
    props.put("buffer.memory", "33554432")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)
    producer.send(new ProducerRecord[String,String](topic, key, value)).get(10, TimeUnit.SECONDS)
    producer.close()
  }

  private def consume(topic: String) : Option[(String, String)] = {
    val props = new Properties()
    props.put("bootstrap.servers", s"localhost:$KafkaAdvertisedPort")
    props.put("group.id", "test")
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("auto.offset.reset", "earliest")
    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(List(topic).asJava)
    var i = 0
    var res: Option[(String, String)] = None
    while(res.isEmpty && i < 10) {
      i += 1
      val records = consumer.poll(1000)
      val iterator = records.iterator()
      while(iterator.hasNext) {
        val record = iterator.next()
        res = Some(record.key() -> record.value())
        printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value())
      }
    }
    consumer.close()
    res
  }
}
