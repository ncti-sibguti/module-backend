package ru.ncti.backend.rabbitmq.config;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ncti.backend.rabbitmq.model.RabbitQueue;


@Configuration
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessage() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Queue certificateQueue() {
        return new Queue(RabbitQueue.EMAIL_UPDATE);
    }
}
