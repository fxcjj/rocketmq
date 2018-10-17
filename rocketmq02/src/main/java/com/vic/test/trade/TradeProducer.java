package com.vic.test.trade;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * TradeProducer
 *  producerGroup: group-producer-trade
 *  topic: topic-trade
 *  三条消息:
 *      tag: order
 *      tag: pay
 *      tag: logistics
 *
 * 如果consumer group中consumer订阅关系不一致，则消息混乱和丢失。
 * 如：
 * OrderConsumer
 *  consumerGroup: group-consumer-trade
 *  topic: topic-trade
 *  tag: order
 *
 * PayConsumer
 *  consumerGroup: group-consumer-trade
 *  topic: topic-trade
 *  tag: pay
 *
 * LogisticsConsumer
 *  consumerGroup: group-consumer-trade
 *  topic: topic-trade
 *  tag: logistics
 *
 *  上面三个consumer不满足订阅关系一致性，（为一个group时，topic和tag要一致）
 *
 * 测试结果为：
 * 第一种情况：
 *      先启动三个consumer，再启动producer，三个consumer无反应
 * 第二种情况：
 *      先启动三个consumer，再启动producer，LogisticsConsumer执行，其它二个consumer未执行。
 *      执行LogisticsConsumer的原因可能是最后一次发送的消息是logistics。
 *      即tag为order,pay的消息丢失。
 *
 * 当添加AllConsumer
 *  consumerGroup: group-consumer-trade
 *  topic: topic-trade
 *  tag: *
 * 时，
 * 第一种情况：
 *      只有AllConsumer执行，其它三个consumer都不执行。
 * 第二种情况：
 *      OrderConsumer执行，其它不执行
 * 得出，消费是混乱的！！！
 */
public class TradeProducer {

    public static final String NAMESRV_ADDR = "10.10.10.123:9876";

    public static void main(String[] args) throws Exception {
        try {
            // 声明并初始化一个producer
            // 需要一个producer group名字作为构造方法的参数
            DefaultMQProducer producer = new DefaultMQProducer("group-producer-trade");

            // 设置NameServer地址,此处应改为实际NameServer地址，多个地址之间用；分隔
            //NameServer的地址必须有，但是也可以通过环境变量的方式设置，不一定非得写死在代码里
            producer.setNamesrvAddr(NAMESRV_ADDR);

            // 调用start()方法启动一个producer实例
            producer.start();

            String topic = "topic-trade";

            for (int i = 0; i < 1; i++) {

                Message orderMsg =
                        new Message(topic, "order", "orderId-001",
                                ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));

                Message payMsg =
                        new Message(topic, "pay", "orderId-001",
                                ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));

                Message logisticsMsg =
                        new Message(topic, "logistics", "orderId-001",
                                ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));


                SendResult sendResult1 = producer.send(orderMsg);
                SendResult sendResult2 = producer.send(payMsg);
                SendResult sendResult3 = producer.send(logisticsMsg);

                System.out.println(sendResult1);
                System.out.println(sendResult2);
                System.out.println(sendResult3);
            }

            producer.shutdown();
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}