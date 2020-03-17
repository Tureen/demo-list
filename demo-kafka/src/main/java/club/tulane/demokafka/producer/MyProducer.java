package club.tulane.demokafka.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class MyProducer {

    private static KafkaProducer<String, String> producer;

    static {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "10.136.15.246:9092"); // 生产者连接 kafka broker的启动地址和端口
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"); // key 的序列化方式
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer"); // value 的序列化方式
        producer = new KafkaProducer<String, String>(properties);
    }

    // 只发送 不管结果
    private static void sendMessageForgetResult() {
        ProducerRecord<String, String> record = new ProducerRecord<>(
                "tulane-a", "name", "ForgetResult2"
        );
        producer.send(record);
        producer.close();
    }

    // 同步发送
    private static void sendMessageSync() throws ExecutionException, InterruptedException {
        ProducerRecord<String, String> record = new ProducerRecord<>(
                "tulane-a", "name", "sync"
        );
        RecordMetadata result = producer.send(record).get();

        System.out.println(result.topic());
        System.out.println(result.partition()); // 分区
        System.out.println(result.offset()); // 偏移量

        producer.close();
    }

    // 异步发送
    private static void sendMessageCallback() {
        ProducerRecord<String, String> record = new ProducerRecord<>(
                "tulane-a", "name", "callback"
        );
        producer.send(record, new MyProducerCallback());


        record = new ProducerRecord<>(
                "tulane-a", "name", "callback1"
        );
        producer.send(record, new MyProducerCallback());

        record = new ProducerRecord<>(
                "tulane-a", "name", "callback2"
        );
        producer.send(record, new MyProducerCallback());

        record = new ProducerRecord<>(
                "tulane-a", "name", "callback3"
        );
        producer.send(record, new MyProducerCallback());

        record = new ProducerRecord<>(
                "tulane-a", "name", "callback4"
        );
        producer.send(record, new MyProducerCallback());

        producer.close();
    }

    // 异步发送的回调
    private static class MyProducerCallback implements Callback {

        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
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
//        sendMessageForgetResult();
//        sendMessageSync();
        sendMessageCallback();
    }
}
