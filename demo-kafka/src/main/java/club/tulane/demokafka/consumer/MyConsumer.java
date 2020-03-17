package club.tulane.demokafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;

public class MyConsumer {

    private static KafkaConsumer<String, String> consumer;
    private static Properties properties;

    static {
        properties = new Properties();
        properties.put("bootstrap.servers", "10.136.15.246:9092"); // 生产者连接 kafka broker的启动地址和端口
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // key 的反序列化方式
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // value 的反序列化方式
        properties.put("group.id", "KafkaStudy");
    }

    // 自动提交位移
    private static void generalConsumeMessageAutoCommit(){
        properties.put("enable.auto.commit", true);
        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singleton("tulane-b"));

        // 循环拉取数据
        try {
            while(true){
                boolean flag = true;
                ConsumerRecords<String, String> records = consumer.poll(100);

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(String.format(
                            "topic = %s, partition = %s, key = %s, value = %s",
                            record.topic(), record.partition(),
                            record.key(), record.value()
                    ));
                    if(record.value().equals("done")){
                        flag = false;
                    }
                }
                if(!flag){
                    break;
                }
            }
        } finally {
            consumer.close();
        }
    }

    public static void main(String[] args) {
        generalConsumeMessageAutoCommit();
    }
}
