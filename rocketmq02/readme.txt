1. 普通消息
参考rocketmq01/src/main/com/vic/test/producer/SyncProducer

2. 有序消息
全局有序消息
    topic只有一个queue
局部有序消息
    订单创建->订单付款->订单完成

2. 延时消息
RcoketMQ的延时等级为：1s，5s，10s，30s，1m，2m，3m，4m，5m，6m，7m，8m，9m，10m，20m，30m，1h，2h。level=0，表示不延时。
level=1，表示 1 级延时，对应延时 1s。level=2 表示 2 级延时，对应5s，以此类推。
这种消息一般适用于消息生产和消费之间有时间窗口要求的场景。
比如说我们网购时，下单之后是有一个支付时间，超过这个时间未支付，系统就应该自动关闭该笔订单。
那么在订单创建的时候就会就需要发送一条延时消息（延时15分钟）后投递给 consumer，
consumer 接收消息后再对订单的支付状态进行判断是否关闭订单。


RocketMQ的消息发送方式
同步发送
异步发送
单向发送
    日志收集


集群消费
    当 consumer 使用集群消费时，每条消息只会被 consumer 集群内的任意一个 consumer 实例消费一次。
    举个例子，当一个 consumer 集群内有 3 个consumer 实例（假设为consumer 1、consumer 2、consumer 3）时，
    一条消息投递过来，只会被consumer 1、consumer 2、consumer 3中的一个消费。
    同时记住一点，使用集群消费的时候，consumer 的消费进度是存储在 broker 上，consumer 自身是不存储消费进度的。
    消息进度存储在 broker 上的好处在于，当你 consumer 集群是扩大或者缩小时，由于消费进度统一在broker上，
    消息重复的概率会被大大降低了。
    注意：在集群消费模式下，并不能保证每一次消息失败重投都投递到同一个 consumer 实例。

广播消费
    当 consumer 使用广播消费时，每条消息都会被 consumer 集群内所有的 consumer 实例消费一次，
    也就是说每条消息至少被每一个 consumer 实例消费一次。
    举个例子，当一个 consumer 集群内有 3 个 consumer 实例（假设为 consumer 1、consumer 2、consumer 3）时，
    一条消息投递过来，会被 consumer 1、consumer 2、consumer 3都消费一次。

使用集群消费模拟广播消费


消息过滤
    通过tag过滤
订阅关系一致性
    一个consumer group中所有的consumer订阅的topic和tag都必须一致

参考地址
https://www.jianshu.com/p/11e875074a8f