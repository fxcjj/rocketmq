package com.vic.test.trade;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class AllConsumer {

    public static final String NAMESRV_ADDR = "10.10.10.123:9876";

    public static void main(String[] args) throws Exception {

        // Instantiate with specified consumer group name.
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group-consumer-trade");

        // Specify name server addresses.
        consumer.setNamesrvAddr(NAMESRV_ADDR);

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        // Subscribe one more more topics to consume.
        consumer.subscribe("topic-trade", "*");
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                for(MessageExt msg : msgs) {
                    String tag = msg.getProperty("TAGS");

                    System.out.printf("%s AllConsumer Receive New Messages: %s %s %n", Thread.currentThread().getName(), msg, tag);

                    //通过tag判断order/pay/logistics，然后对应处理
                    if("order".equals(tag)) {
                        System.out.println("process order");
                    } else if("pay".equals(tag)) {
                        System.out.println("process pay");
                    } else if("logistics".equals(tag)) {
                        System.out.println("process logistics");
                    }

                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        //Launch the consumer instance.
        consumer.start();

        System.out.printf("Consumer Started.%n");
    }
}