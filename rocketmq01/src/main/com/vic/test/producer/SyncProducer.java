package vic.test.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class SyncProducer {



    public static void main(String[] args) throws Exception {
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new
                DefaultMQProducer("group-producer-order-shop");
        // Specify name server addresses.
        producer.setNamesrvAddr("10.10.10.123:9876");

        //Launch the instance.
        producer.start();
        for (int i = 0; i < 2; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("topicTesta" /* Topic */,
                "TagA" /* Tag */,
                ("Hello Little RocketMQ " +
                    i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );

//            msg.setDelayTimeLevel(3);
            //设置业务主键，幂等处理
//            msg.setKeys();
            //Call send message to deliver message to one of brokers.
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);

        }
        //Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }
}