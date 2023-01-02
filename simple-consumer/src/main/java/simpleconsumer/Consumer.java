package simpleconsumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

public class Consumer extends Thread
{
    private KafkaConsumer<String, String> consumer;

    public Consumer(String bootstrapServer, String consumerGroupId, String topic)
    {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);        
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Arrays.asList(topic));
    }

    public void run() {
        try {
            while (true) {

                ConsumerRecords<String, String> records = this.consumer.poll(Duration.ofSeconds(7));
                System.out.printf("Consuming %d records %n", records.count());

                for (ConsumerRecord<String, String> record : records) {

                    // Add the record that is being consumed to an offset map that will get committed to Kafka
                    Map<TopicPartition, OffsetAndMetadata> offsetmap = new HashMap<>();
                    offsetmap.put(new TopicPartition(record.topic(), record.partition()),
                    new OffsetAndMetadata(record.offset() + 1));

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    System.out.printf("Received: %s%n", record.value());

                    // Commit offset as soon as it is consumed.
                    consumer.commitSync(offsetmap);
                    System.out.printf("Committed %d offsets %n", offsetmap.size());
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            this.consumer.close();
        }
    }
}
