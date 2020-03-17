### Kafka的原生 API 使用 Demo

### Spring的Kafka使用
* https://segmentfault.com/a/1190000015316875



### Linux 上 Kafka 的服务器及客户端搭建

#### 安装

1. 下载地址: ```http://kafka.apache.org/downloads```
2. 解压: ```tar -zxvf kafka_2.12-2.1.0.tgz```
3. 修改配置: ```config/server.properties: broker.id、log.dirs```


#### 命令
**使用 Kafka (进入到 kafka 的根目录)**

| 组件         | 启动命令                                                     | 解释          |
| ------------ | ------------------------------------------------------------ | ------------- |
| zookeeper    | bin/zookeeper-server-start.sh -daemon config/zookeeper.properties | 数据库        |
| kafka-server | bin/kafka-server-start.sh config/server.properties           | 服务器        |
| create topic | bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic [xxx] | 创建主题      |
| topic list   | bin/kafka-topics.sh --list --zookeeper localhost:2181        | 所有注意展示  |
| producer     | bin/kafka-console-producer.sh --broker-list localhost:9092 --topic [xxx] | 写消息        |
| consumer     | bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic [xxx] --from-beginning | 消费者        |
| topic info   | bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic [xxx] | topic相关信息 |

