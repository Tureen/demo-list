package club.tulane.demokafka.consumer;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class MyConsumerTwo {

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

    // 同步提交位移
    private static void generalConsumeMessageSyncCommit(){
        properties.put("enable.auto.commit", false);
        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singletonList("tulane-b"));

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

            try {
                consumer.commitSync();
            } catch (CommitFailedException e) {
                System.out.println("commit failed error: " + e.getMessage());
            }

            if(!flag){
                break;
            }
        }
    }

    // 异步提交位移
    private static void generalConsumeMessageAsyncCommit(){
        properties.put("enable.auto.commit", false);
        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singletonList("tulane-b"));

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

            consumer.commitAsync();

            if(!flag){
                break;
            }
        }
    }

    // 异步提交 + 异步失败回调
    private static void generalConsumeMessageAsyncCommitWithCallback(){
        properties.put("enable.auto.commit", false);
        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singletonList("tulane-b"));

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

            consumer.commitAsync(new OffsetCommitCallback() {
                @Override
                public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
                    // 如果想重试 可以传递全局递增ID, 识别消息的顺序
                    // 如果ID小于当前就不能重试, 否则会使位移回到之前消费过的消息
                    if(e != null){
                        System.out.println("commit failed for offsets: " + e.getMessage());
                        System.out.println("message: " + JSON.toJSONString(map));
                    }
                }
            });

            if(!flag){
                break;
            }
        }
    }

    // 混合 异步提交 与 同步提交
    // 先异步提交, 以提升性能, 如果报错, 同步提交
    private static void mixSyncAndAsyncCommit(){
        properties.put("enable.auto.commit", false);
        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singletonList("tulane-b"));

        try {
            while(true){
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(String.format(
                            "topic = %s, partition = %s, key = %s, value = %s",
                            record.topic(), record.partition(),
                            record.key(), record.value()
                    ));
                }

                consumer.commitAsync();
            }
        } catch (Exception ex){
            System.out.println("commit async error: " + ex.getMessage());
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    public static void main(String[] args) {
        generalConsumeMessageAutoCommit();
    }
}
