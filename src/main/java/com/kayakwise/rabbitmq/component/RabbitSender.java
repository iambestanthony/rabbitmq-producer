package com.kayakwise.rabbitmq.component;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /*
            确认消息的回调监听接口，用于确认消息是否被broker所收到
     */
    final ConfirmCallback confirmCallback = new ConfirmCallback() {
        /**
         *
         * @param correlationData 作为一个唯一的标识
         * @param ack
         * @param cause
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.err.println("消息ACK结果:" + ack + ", correlationData: " + correlationData.getId());
        }
    };

    public void send(Object message, Map<String,Object> properties) throws Exception {
        MessageHeaders mhs = new MessageHeaders(properties);

        Message<?> msg = MessageBuilder.createMessage(message, mhs);

        rabbitTemplate.setConfirmCallback(confirmCallback);

        CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());

        rabbitTemplate.convertAndSend("exchange-1", "springboot.abc", msg, new MessagePostProcessor() {
            @Override
            public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message) throws AmqpException {
                System.err.println("post to do: " + message);
                return message;
            }
        },cd);
    }
}
