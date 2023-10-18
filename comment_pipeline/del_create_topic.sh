#!/bin/bash
sh $KAFKA_HOME/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic youtube_comment


# Delete the topic surely by zookeeper command;
# deleting topic by kafka seems not working in our cluster currently.
sh $KAFKA_HOME/bin/zookeeper-shell.sh localhost:2181 deleteall /brokers/topics/youtube_comment
echo "create topic"
sh $KAFKA_HOME/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 12 --topic youtube_comment
echo "complete to create topic"
