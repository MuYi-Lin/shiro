package org.muyi.chapter04.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitFanoutConfig {
    public final static String FANOUTNAME = "sang-fanout";
    @Bean
    Queue queueOne(){
        return new Queue("queue-one");
    }
    @Bean
    Queue queueTwo(){
        return new Queue("queue-two");
    }
    @Bean
    FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUTNAME,true,false);
    }
    @Bean
    Binding bindingOne(){
        return BindingBuilder.bind(queueOne()).to(fanoutExchange());
    }
    @Bean
    Binding bindingTwo(){
        return BindingBuilder.bind(queueTwo()).to(fanoutExchange());
    }
}
