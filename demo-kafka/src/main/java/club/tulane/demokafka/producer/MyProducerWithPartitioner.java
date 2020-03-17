package club.tulane.demokafka.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class MyProducerWithPartitioner {

    private static KafkaProducer<String, String> producer;

    static {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "10.136.15.246:9092"); // 生产者连接 kafka broker的启动地址和端口
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"); // key 的序列化方式
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer"); // value 的序列化方式

        properties.put("partitioner.class", "club.tulane.demokafka.producer.CustomPartitioner");

        producer = new KafkaProducer<String, String>(properties);
    }


    // 异步发送
    private static void sendMessageCallback(){
        ProducerRecord<String, String> record = new ProducerRecord<>(
                "tulane-b", "name", "callback"
        );
        producer.send(record, new MyProducerCallback());

        record = new ProducerRecord<>(
                "tulane-b", "name-x", "callback-x"
        );
        producer.send(record, new MyProducerCallback());

        record = new ProducerRecord<>(
                "tulane-b", "name-y", "callback-y"
        );
        producer.send(record, new MyProducerCallback());

        record = new ProducerRecord<>(
                "tulane-b", "name-z", "callback-z"
        );
        producer.send(record, new MyProducerCallback());

        producer.close();
    }

    // 异步发送的回调
    private static class MyProducerCallback implements Callback {

        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if(e != null){
                e.printStackTrace();
                return;
            }
            System.out.println(recordMetadata.topic());
            System.out.println(recordMetadata.partition());
            System.out.println(recordMetadata.offset());
            System.out.println("Coming in MyProducerCallback");
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        sendMessageCallback();
    }
}
